# react-native-aliyun-video 

[![npm package](https://img.shields.io/npm/v/react-native-aliyun-short-video.svg?style=flat-square)](https://www.npmjs.org/package/react-native-aliyun-short-video)
[![npm downloads](https://img.shields.io/npm/dt/react-native-aliyun-short-video.svg)](https://www.npmjs.com/package/react-native-aliyun-short-video)

<img width="300" src="./doc/android.gif" />
&nbsp;&nbsp;&nbsp;&nbsp;
<img width="300" src="./doc/ios.gif" />

## TOC

* [Installation](#installation)
* [Linking](#linking)
* [Usage](#usage)
* [API](#api)
* [Troubleshooting](#troubleshooting)

## Installation

Using npm:

```shell
npm install --save react-native-aliyun-short-video
```

or using yarn:

```shell
yarn add react-native-aliyun-short-video
```

## Linking

### Automatic

```shell
react-native link react-native-aliyun-short-video
```

(or using [`rnpm`](https://github.com/rnpm/rnpm) for versions of React Native < 0.27)

```shell
rnpm link react-native-aliyun-short-video
```

### Manual

<details>
    <summary>Android</summary>

* in `android/app/build.gradle`:

```diff
dependencies {
    ...
    compile "com.facebook.react:react-native:+"  // From node_modules
+   compile project(':react-native-aliyun-short-video')
    ...
}
```

* in `android/settings.gradle`:

```diff
...
include ':app'
+ include ':react-native-aliyun-short-video'
+ project(':react-native-aliyun-short-video').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-aliyun-short-video/android')
...
```

#### With React Native 0.29+

* in `MainApplication.java`:

```diff
...
+ import com.rnshortvideo.RNShortVideoPackage;

  public class MainApplication extends Application implements ReactApplication {
    ...

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
+         new RNShortVideoPackage()
      );
    }

    ...
  }
```

#### With older versions of React Native:

* in `MainActivity.java`:

```diff
...
+ import com.rnshortvideo.RNShortVideoPackage;

  public class MainActivity extends ReactActivity {
    ...

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
        new MainReactPackage(),
+       new RNShortVideoPackage()
      );
    }
  }
```
</details>

<details>
    <summary>iOS</summary>

In XCode, in the project navigator:

* Right click _Libraries_
* Add Files to _[your project's name]_
* Go to `node_modules/react-native-aliyun-short-video/ios`
* Add the `RNShortVideo.xcodeproj` file
* Drag and drop the `node_modules/react-native-aliyun-short-video/ios/AliyunSDK` folder to your xcode project. (Make sure Copy items if needed IS ticked)

Click on project _General_ tab

* Under _Embedded Binaries_ click `+` and add `AliThirdparty.framework`, `AliyunPlayerSDK.framework`, `AliyunVideoSDK.framework`, `AliyunVodPlayerSDK.framework`, `QuCore-ThirdParty.framework`
* Under _Linked Frameworks and Libraries_ click `+` and add `RNShortVideo.framework`, `libz.tbd`, `libc++.tbd`, `libiconv.tbd`, `libresolv.tbd`, `ImageIO.framework`, `CoreMedia.framework`, `CoreVideo.framework`, `SystemConfiguration.framework`, `Photos.framework`, `OpenAL.framework`, `VideoToolbox.framework`

Click on project _Build Settings_ tab

* Look for _Enable Bitcode_ set to No
* Look for _Other Linker Flags_ and make sure it contain `-ObjC`
* Look for _Framework Search Paths_ and make sure it contain `$(SRCROOT)/../node_modules/react-native-aliyun-short-video/ios/AliyunSDK`
* Look for _Header Search Paths_ and make sure it contain `$(SRCROOT)/../node_modules/react-native-aliyun-short-video/ios/RNShortVideo` (Mark as recursive)

Click on project _Build Phase_ tab

* Under _Copy Bundle Resources_ click `+` and add `QPSDK.bundle` and `AliyunLanguageSource.bundle`


In the project navigator:

* Click _Info.plist_
* Add the `NSPhotoLibraryUsageDescription`, `NSMicrophoneUsageDescription`, `NSCameraUsageDescription` and `NSPhotoLibraryAddUsageDescription`keys to your `Info.plist` with strings describing why your app needs these permissions. **Note: You will get a SIGABRT crash if you don't complete this step**

Run your project (Cmd+R) 

</details>

## Usage
See the [example](https://github.com/xinlc/react-native-aliyun-video/blob/master/packages/Example/src/App.js)

```js
...
import RNShortVideo, { VideoView } from 'react-native-aliyun-short-video';
...

...
onRecord = () => {
  RNShortVideo.recordShortVideo()
    .then((path) => {
      this.setState({ path });
    })
    .catch((err) => {
      console.error(err);
    });
};
...

...
<View>
  {
    this.state.path ? (
      <VideoView
        fullscreen
        source={{ url: this.state.path }}
        poster="http://t.cn/RuWRYzv?1=1"
      />
    ) : null
  }
</View>
...
```

## API
[android short video](https://help.aliyun.com/document_detail/53421.html)

## Troubleshooting

When installing or using `react-native-aliyun-short-video`, you may encounter the following problems:

<details>
  <summary>[android] - Duplicate files copied in APK lib/armeabi-v7a/libgnustl_shared.so</summary>

* in `android/app/build.gradle`:

```diff
android {
  ...
  packagingOptions {
    exclude('META-INF/LICENSE')
+    pickFirst "**/libgnustl_shared.so"
  }
  ...
}
```

</details>

<details>
  <summary>[android] - Could not find `:QuSdk-RC:`...</summary>

* in `android/app/build.gradle`:

```diff
...
repositories {
  flatDir {
-   dirs "libs"
+   dirs "libs", "$rootDir/../node_modules/react-native-aliyun-short-video/android/libs"
  }
}
...
```

</details>

## TODO

* [x] Compatible with iOS
