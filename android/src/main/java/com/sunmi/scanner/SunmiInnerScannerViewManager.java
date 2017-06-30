package com.sunmi.scanner;

import android.view.View;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

/**
 * Created by januslo on 2017/5/16.
 */
public class SunmiInnerScannerViewManager extends ViewGroupManager<SunmiInnerScannerView> {
    private static final String TAG="SunmiScanner";
    @Override
    public String getName() {
        return TAG;
    }

    @Override
    protected SunmiInnerScannerView createViewInstance(ThemedReactContext reactContext) {
        return new SunmiInnerScannerView(reactContext);
    }

    @ReactProp(name="xDensity")
    public void setXDensity(SunmiInnerScannerView view,final int desity){
        view.setXDensity(desity);
    }

    @ReactProp(name="yDensity")
    public void setYDensity(SunmiInnerScannerView view, int desity){
        view.setYDensity(desity);
    }

    @ReactProp(name="mutilScanEnable")
    public void setMutilScanEnable(SunmiInnerScannerView view, int enable){
        view.setMutilScanEnable(enable);
    }

    @ReactProp(name="inverseEnable")
    public void setInverseEnable(SunmiInnerScannerView view, int enable){
        view.setInverseEnable(enable);
    }

    @ReactProp(name="scanInterval")
    public void setScanInterval(SunmiInnerScannerView view, int interval){
        view.setScanInterval(Long.valueOf(interval));
    }

    @ReactProp(name = "mute")
    public void setMute(SunmiInnerScannerView view, int mute){view.setMute(mute);}

    @Override
    public void addView(SunmiInnerScannerView parent, View child, int index) {
        parent.addView(child,index+1);
    }
}
