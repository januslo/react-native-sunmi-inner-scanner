package com.sunmi.scanner;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.sunmi.scan.*;

/**
 * Created by januslo on 2017/5/16.
 */

public class SunmiInnerScannerView extends FrameLayout implements Camera.PreviewCallback {
    private CameraPreview mPreview;
    private ImageScanner scanner;
    private SoundUtils soundUtils;
    private AsyncDecode asyncDecode;
    private static final int PADDING=10;
    private long scanInterval;
    private int mute;

    private static final String TAG = "SunmiInnerScannerView";

    public SunmiInnerScannerView(Context context) {
        super(context);
        scanner = new ImageScanner();
        mPreview = new CameraPreview(context, this);
        mPreview.startCamera();
        try {
            soundUtils = new SoundUtils(context, SoundUtils.RING_SOUND);
            soundUtils.putSound(0, context.getResources().getIdentifier("beep", "raw", context.getPackageName()));
        }catch(Exception e){
            // ignore the error for the sound is not mandatory.
            Log.e(TAG,e.getMessage(),e);
        }
        this.addView(mPreview);
    }

    public void setXDensity(int desity) {
        this.scanner.setConfig(0, Config.X_DENSITY, desity);
    }

    public void setYDensity(int desity) {
        this.scanner.setConfig(0, Config.Y_DENSITY, desity);
    }

    public void setMutilScanEnable(int enable) {
        this.scanner.setConfig(0, Config.ENABLE_MULTILESYMS, enable);
    }

    public void setInverseEnable(int enable) {
        this.scanner.setConfig(0, Config.ENABLE_INVERSE, enable);
    }

    public int isMute() {
        return mute;
    }

    public void setMute(int mute) {
        this.mute = mute;
    }

    public long getScanInterval() {
        return scanInterval;
    }

    public void setScanInterval(long scanInterval) {
        this.scanInterval = scanInterval;
    }

    public void onResume() {
        mPreview.startCamera(); // workaround for reload js
        // mPreview.onResume();
    }

    public void onPause() {
        mPreview.stopCamera();  // workaround for reload js
        // mPreview.onPause();
    }

    public void setCameraType(String cameraType) {
        mPreview.setCameraType(cameraType);
    }

    public void setFlash(boolean flag) {
        mPreview.setFlash(flag);
    }

    public void stopCamera() {
        mPreview.stopCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        try {
            long now = System.currentTimeMillis();
            if(asyncDecode == null ||
                    (asyncDecode.isStoped() && now - asyncDecode.getEndTimeMillis()>this.scanInterval)) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();
                int width = size.width;
                int height = size.height;

                if (getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
                    byte[] rotatedData = new byte[data.length];
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++)
                            rotatedData[x * height + height - y - 1] = data[x + y * width];
                    }

                    int tmp = width;
                    width = height;
                    height = tmp;
                    data = rotatedData;
                }

                Image source = new Image(width, height, "Y800");
                Rect scanImageRect = new Rect(PADDING,PADDING,width-(2*PADDING),height-(2*PADDING));
                source.setCrop(scanImageRect.top,scanImageRect.left,
                        scanImageRect.width(),scanImageRect.height());
                source.setData(data);// 填充数据
                asyncDecode = new AsyncDecode();
                asyncDecode.setMute(this.isMute()>0);//静音
                asyncDecode.execute(source);// 调用异步执行解码
            }

        } catch (Exception e) {
            // TODO: Terrible hack. It is possible that this method is invoked after camera is released.
            Log.e(TAG, e.toString(), e);
        }
    }


    protected void sendEvent(ReactContext context, String eventName, WritableMap params){
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName,params);
    }

    private static int getScreenOrientation(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int orientation;
        if (display.getWidth() == display.getHeight()) {
            orientation = Configuration.ORIENTATION_SQUARE;
        } else {
            if (display.getWidth() < display.getHeight()) {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    private class AsyncDecode extends AsyncTask<Image, Void, Void> {
        private boolean stoped = true;
        private  WritableArray array;
        private long endTimeMillis;
        private boolean mute;
        @Override
        protected Void doInBackground(Image... params) {
            stoped = false;
            StringBuilder sb = new StringBuilder();
            Image src_data = params[0];// 获取灰度数据

            // 解码，返回值为0代表失败，>0表示成功
            int nsyms = scanner.scanImage(src_data);
            if (nsyms != 0) {
                if(!this.mute) {
                    soundUtils.playSound(0, SoundUtils.SINGLE_PLAY);// 解码成功播放提示音
                }
                array = new WritableNativeArray();
                SymbolSet syms = scanner.getResults();// 获取解码结果
                for (Symbol sym : syms) {
                    WritableMap r = new WritableNativeMap();
                    r.putString("symbolName",sym.getSymbolName());
                    r.putString("result",sym.getResult());
                    array.pushMap(r);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            stoped = true;
            if(array!=null){
                WritableMap map = new WritableNativeMap();
                map.putArray("result",array);
                sendEvent((ReactContext)getContext(),TAG+".RESULT",map);
            }
            endTimeMillis = System.currentTimeMillis();
        }

        public boolean isStoped() {
            return stoped;
        }
        public long getEndTimeMillis(){
            return this.endTimeMillis;
        }

        public void setMute(boolean mute) {
            this.mute = mute;
        }
    }
}