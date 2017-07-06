package com.sunmi.scanner;

/**
 * Created by januslo on 2017/5/16.
 */

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import java.util.List;

public class CameraPreview extends TextureView implements TextureView.SurfaceTextureListener {
    private SurfaceTexture _surfaceTexture;
    private int _surfaceTextureWidth;
    private int _surfaceTextureHeight;
    private CameraManager mCameraManager;
    private Camera mCamera;
    private String mCameraType="back";
    private Camera.PreviewCallback mPreviewCallback;

    private Handler mAutoFocusHandler;
    private boolean mSurfaceCreated;
    private boolean mPreviewing = true;
    private boolean mAutoFocus = true;

    private static final String TAG = "CameraPreview";

    public CameraPreview(Context context, Camera.PreviewCallback previewCallback) {
        super(context);
        this.setSurfaceTextureListener(this);
        mCameraManager = new CameraManager();
        mAutoFocusHandler = new Handler();
        mPreviewCallback = previewCallback;
    }

    public void startCamera() {
        mCamera = mCameraManager.getCamera(mCameraType);
        startCameraPreview();
    }

    public void stopCamera() {
        stopCameraPreview();
        mCameraManager.releaseCamera();
    }

    public void setCameraType(String cameraType) {
        mCameraType = cameraType;
        stopCamera();
        startCamera();
    }

    public void startCameraPreview() {
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size picSizes = getBestSize(parameters.getSupportedPictureSizes(),_surfaceTextureWidth,_surfaceTextureHeight);
                parameters.setPictureSize(picSizes.width,picSizes.height);
                Camera.Size previewSizes = getBestSize(parameters.getSupportedPreviewSizes(),_surfaceTextureWidth,_surfaceTextureHeight);
                parameters.setPreviewSize(previewSizes.width,previewSizes.height);
                mPreviewing = true;
                mCamera.setParameters(parameters);
                mCamera.setPreviewTexture(this._surfaceTexture);
                mCamera.setDisplayOrientation(getDisplayOrientation());
                mCamera.setPreviewCallback(mPreviewCallback);
                mCamera.startPreview();
                if (mAutoFocus) {
                    if (mSurfaceCreated) { // check if surface created before using autofocus
                        safeAutoFocus();
                    } else {
                        scheduleAutoFocus(); // wait 1 sec and then do check again
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }

        }
    }

    public void stopCameraPreview() {
        if (mCamera != null) {
            try {
                mPreviewing = false;
                mCamera.cancelAutoFocus();
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    public int getDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    public void safeAutoFocus() {
        try {
            mCamera.autoFocus(autoFocusCB);
        } catch (RuntimeException re) {
            scheduleAutoFocus(); // wait 1 sec and then do check again
        }
    }

    public void setAutoFocus(boolean state) {
        if (mCamera != null && mPreviewing) {
            if (state == mAutoFocus) {
                return;
            }
            mAutoFocus = state;
            if (mAutoFocus) {
                if (mSurfaceCreated) { // check if surface created before using autofocus
                    Log.v(TAG, "Starting autofocus");
                    safeAutoFocus();
                } else {
                    scheduleAutoFocus(); // wait 1 sec and then do check again
                }
            } else {
                Log.v(TAG, "Cancelling autofocus");
                mCamera.cancelAutoFocus();
            }
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (mCamera != null && mPreviewing && mAutoFocus && mSurfaceCreated) {
                safeAutoFocus();
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            Log.v(TAG, "Autofocus:"+String.valueOf(success));
            scheduleAutoFocus();
        }
    };

    private void scheduleAutoFocus() {
        mAutoFocusHandler.postDelayed(doAutoFocus, 1000);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurfaceCreated = true;
        _surfaceTexture = surface;
        _surfaceTextureWidth = width;
        _surfaceTextureHeight = height;
        startCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        _surfaceTextureWidth = width;
        _surfaceTextureHeight = height;
        if (mCamera != null) {
            try {
                mCamera.setDisplayOrientation(getDisplayOrientation());
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        _surfaceTexture = null;
        _surfaceTextureWidth = 0;
        _surfaceTextureHeight = 0;
        stopCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void onPause() {
       stopCameraPreview();
    }

    public void onResume() {
        startCameraPreview();
    }

    public void setFlash(boolean flag) {
        if (mCamera != null && mCameraManager.isFlashSupported(mCamera)) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (flag) {
                if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                    return;
                }
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                    return;
                }
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            mCamera.setParameters(parameters);
        }
    }

    public Camera.Size getBestSize(List<Camera.Size> supportedSizes, int maxWidth, int maxHeight) {
        Camera.Size bestSize = null;
        for (Camera.Size size : supportedSizes) {
            if (size.width > maxWidth || size.height > maxHeight) {
                continue;
            }

            if (bestSize == null) {
                bestSize = size;
                continue;
            }

            int resultArea = bestSize.width * bestSize.height;
            int newArea = size.width * size.height;

            if (newArea > resultArea) {
                bestSize = size;
            }
        }

        return bestSize;
    }

}
