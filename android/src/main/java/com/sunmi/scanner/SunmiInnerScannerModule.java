package com.sunmi.scanner;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.WritableNativeMap;
import android.widget.Toast;
import java.util.Map;
import java.io.IOException;
import android.os.RemoteException;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Base64;
import android.graphics.Bitmap;
import java.nio.charset.StandardCharsets;
import android.util.Log;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.app.Activity;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
public class SunmiInnerScannerModule extends ReactContextBaseJavaModule {

	private static final String TAG = "SunmiInnerScannerModule";
	private Promise promise ;

	private final ActivityEventListener eventListener = new BaseActivityEventListener() {

		@Override
		public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		try{
			if (requestCode == 2345 && data!=null) {
				Bundle bundle = data.getExtras();
				ArrayList<HashMap<String, String>> result = (ArrayList<HashMap<String, String>>) bundle.getSerializable("data");
				Iterator<HashMap<String, String>> it = result.iterator();
				WritableNativeMap wnm =null;
				while (it.hasNext()) {
					Map<String,String> hashMap = it.next();
					wnm = new WritableNativeMap();
	            wnm.putString("type", hashMap.get("TYPE"));//这个是扫码的类型
	            wnm.putString("value", hashMap.get("VALUE"));//这个是扫码的结果                         
	        }
	        if(wnm!=null){
	        	promise.resolve(wnm);
	        }else{
	        	promise.reject("DATA_NOT_FOUND", "No data found");
	        }
	        promise = null;
	    }
	}catch(Exception ex){
		ex.printStackTrace();
		promise.reject("ERROR", ex.getMessage());
		 promise = null;
	}

	}
};

public SunmiInnerScannerModule(ReactApplicationContext reactContext) {
	super(reactContext);
     // Add the listener for `onActivityResult`
	reactContext.addActivityEventListener(eventListener);
}

@Override
public String getName() {
	return "SunmiInnerScanner";
}
@ReactMethod
public void openScanner(final Promise p){
	WritableNativeMap options = new WritableNativeMap();
	options.putBoolean("showSetting",false);
	options.putBoolean("showAlbum",false);
	openScannerWithOptions(options,p);
}

@ReactMethod
public void openScannerWithOptions(ReadableMap options,final Promise p){
	promise = p;
	Activity currentActivity = getCurrentActivity();
	if (currentActivity == null) {
		promise.reject("E_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist");
		return;
	}
	final Intent intent = new Intent("com.summi.scan");
	intent.setPackage("com.sunmi.sunmiqrcodescanner");

	if(options!=null){
		if(options.hasKey("paySound")){
			intent.putExtra("PLAY_SOUND",options.getBoolean("paySound"));
		}
		if(options.hasKey("payVibrate")){
			intent.putExtra("PLAY_VIBRATE",options.getBoolean("payVibrate"));
		}
		if(options.hasKey("showSetting")){
			intent.putExtra("IS_SHOW_SETTING",options.getBoolean("showSetting"));
		}
		if(options.hasKey("showAlbum")){
			intent.putExtra("IS_SHOW_ALBUM",options.getBoolean("showAlbum"));
		}
	}
/**

	//扫码模块有一些功能选项，开发者可以通过传递参数控制这些参数，

	//所有参数都有一个默认值，开发者只要在需要的时候添加这些配置就可以。

	intent.putExtra("CURRENT_PPI", 0X0003);//当前分辨率 

	//M1和V1的最佳是800*480,PPI_1920_1080 = 0X0001;PPI_1280_720 = 

	//0X0002;PPI_BEST = 0X0003;

	intent.putExtra("PLAY_SOUND", true);// 扫描完成声音提示  默认true

	intent.putExtra("PLAY_VIBRATE", false);

	//扫描完成震动,默认false，目前M1硬件支持震动可用该配置，V1不支持

	intent.putExtra("IDENTIFY_INVERSE_QR_CODE", true);// 识别反色二维码，默认true

	intent.putExtra("IDENTIFY_MORE_CODE", false);// 识别画面中多个二维码，默认false        

	intent.putExtra("IS_SHOW_SETTING", true);// 是否显示右上角设置按钮，默认true

	intent.putExtra("IS_SHOW_ALBUM", true);// 是否显示从相册选择图片按钮，默认true

	**/

	currentActivity.startActivityForResult(intent, 2345);
}


}
