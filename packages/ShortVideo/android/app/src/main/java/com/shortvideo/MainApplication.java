package com.shortvideo;

import android.app.Application;

import com.aliyun.common.httpfinal.QupaiHttpFinal;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.shortvideo.BuildConfig;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
          new MyReactPackage()
      );
    }

    @Override
    protected String getJSMainModuleName() {
      return "index";
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
    return mReactNativeHost;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SoLoader.init(this, /* native exopackage */ false);


    // aliyun short video
    loadLibs();
    QupaiHttpFinal.getInstance().initOkHttpFinal();
  }

  // aliyun short video
  private void loadLibs(){
    System.loadLibrary("live-openh264");
    System.loadLibrary("QuCore-ThirdParty");
    System.loadLibrary("QuCore");
  }
}
