package com.rnshortvideo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.vod.upload.VODSVideoUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClient;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.VODUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.SvideoInfo;
import com.alibaba.sdk.android.vod.upload.session.VodHttpClientConfig;
import com.alibaba.sdk.android.vod.upload.session.VodSessionCreateInfo;
import com.aliyun.common.utils.StorageUtils;
import com.aliyun.demo.recorder.AliyunVideoRecorder;
import com.aliyun.struct.common.CropKey;
import com.aliyun.struct.common.VideoQuality;
import com.aliyun.struct.recorder.CameraType;
import com.aliyun.struct.recorder.FlashType;
import com.aliyun.struct.snap.AliyunSnapVideoParam;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.rnshortvideo.utils.PermissionChecker;
import com.rnshortvideo.utils.ToastUtils;
import com.rnshortvideo.utils.Utils;

import java.io.File;

import com.rnshortvideo.utils.ToastUtils;
import com.rnshortvideo.utils.PermissionChecker;

/**
 * Created by lichao on 2018/1/24.
 */
public class RNShortVideoModule extends ReactContextBaseJavaModule {
    private static final String TAG = "RNShortVideoModule";

    private ReactApplicationContext context = null;

    private static final int REQUEST_RECORD = 2001;
    private String[] eff_dirs;
    private Promise promise;

    public RNShortVideoModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;

        // 添加onResult监听
        reactContext.addActivityEventListener(new ActivityResultListener());
        initAssetPath();
    }

    @Override
    public String getName() {
        return "RNShortVideoAndroidManager";
    }


    /**
     * 录制短视频
     */
    @ReactMethod
    public void recordShortVideo(@Nullable ReadableMap options, Promise promise) {
        if (isPermissionOK()) {
            this.promise = promise;

            int size = options.hasKey("size") ? options.getInt("size") : 1; // 0->360p，1:480p，2->540p，3->720p
            int ratio = options.hasKey("ratio") ? options.getInt("ratio") : 0; // 0->1:1,1->3:4,2->9:16
            int min = options.hasKey("min") ? options.getInt("min") * 1000 : 2000; // 2000
            int max = options.hasKey("max") ? options.getInt("max") * 1000 : 20000; // 20000
            int videoQuality = options.hasKey("quality") ? options.getInt("quality") : 3; // 0->SSD, 1->HD, 2->SD, 3->LD, 4->PD, 5->EPD
            int gop = options.hasKey("gop") ? options.getInt("gop") : 5;    // 建议GOP值为5-30

            VideoQuality quality = VideoQuality.values()[videoQuality];

            AliyunSnapVideoParam recordParam = new AliyunSnapVideoParam.Builder()
                    //设置录制分辨率，目前支持360p，480p，540p，720p
                    .setResulutionMode(size)  // AliyunSnapVideoParam.RESOLUTION_480P
                    //设置视频比例，目前支持1:1,3:4,9:16
                    .setRatioMode(ratio)  // AliyunSnapVideoParam.RATIO_MODE_1_1
                    .setRecordMode(AliyunSnapVideoParam.RECORD_MODE_AUTO) //设置录制模式，目前支持按录，点录和混合模式
                    .setFilterList(eff_dirs) //设置滤镜地址列表,具体滤镜接口接收的是一个滤镜数组
                    .setBeautyLevel(80) //设置美颜度
                    .setBeautyStatus(true) //设置美颜开关
                    .setCameraType(CameraType.BACK) //设置前后置摄像头
                    .setFlashType(FlashType.AUTO) // 设置闪光灯模式
                    .setNeedClip(true) //设置是否需要支持片段录制
                    .setMaxDuration(max) //设置最大录制时长 单位毫秒
                    .setMinDuration(min) //设置最小录制时长 单位毫秒
                    .setVideQuality(quality) //设置视频质量
                    .setGop(gop) //设置关键帧间隔
                    // .setVideoBitrate(2000) //设置视频码率，如果不设置则使用视频质量videoQulity参数计算出码率
                    .setSortMode(AliyunSnapVideoParam.SORT_MODE_VIDEO)//设置导入相册过滤选择视频
                    .build();

            AliyunVideoRecorder.startRecordForResult(getCurrentActivity(), REQUEST_RECORD, recordParam);
        }

    }

    /**
     * 上传视频
     */
    @ReactMethod
    public void uploadVideo(ReadableMap params, final Promise promise) {
        if (isPermissionOK()) {
            String videoPath = params.getString("mp4Path");
            // String imagePath = params.getString("imagePath");
            String accessKeyId = params.getString("accessKeyId");
            String accessKeySecret = params.getString("accessKeySecret");
            String securityToken = params.getString("securityToken");
            String expriedTime = params.getString("expriedTime");

            final String imagePath = Utils.getFirstFramePath(videoPath);

            // 1.初始化短视频上传对象
            final VODSVideoUploadClient vodsVideoUploadClient = new VODSVideoUploadClientImpl(context.getApplicationContext());
            vodsVideoUploadClient.init();

            // 参数请确保存在，如不存在SDK内部将会直接将错误throw Exception
            // 文件路径保证存在之外因为Android 6.0之后需要动态获取权限，请开发者自行实现获取"文件读写权限".
            VodHttpClientConfig vodHttpClientConfig = new VodHttpClientConfig.Builder()
                    .setMaxRetryCount(2)             // 重试次数
                    .setConnectionTimeout(15 * 1000) // 连接超时
                    .setSocketTimeout(15 * 1000)     // socket超时
                    .build();

            // 构建短视频VideoInfo,常见的描述，标题，详情都可以设置
            SvideoInfo svideoInfo = new SvideoInfo();
            svideoInfo.setTitle(new File(videoPath).getName());
            svideoInfo.setDesc("");
            svideoInfo.setCateId(1);

            // 构建点播上传参数(重要)
            VodSessionCreateInfo vodSessionCreateInfo = new VodSessionCreateInfo.Builder()
                    .setImagePath(imagePath)        // 图片地址
                    .setVideoPath(videoPath)        // 视频地址
                    .setAccessKeyId(accessKeyId)    // 临时accessKeyId
                    .setAccessKeySecret(accessKeySecret)    // 临时accessKeySecret
                    .setSecurityToken(securityToken)        // securityToken
                    .setExpriedTime(expriedTime)            // STStoken过期时间
                    // .setRequestID(requestID)                // requestID，开发者可以传将获取STS返回的requestID设置也可以不设.
                    .setIsTranscode(false)                   // 是否转码.如开启转码请AppSever务必监听服务端转码成功的通知
                    .setSvideoInfo(svideoInfo)              // 短视频视频信息
                    .setVodHttpClientConfig(vodHttpClientConfig)    //网络参数
                    .build();


            vodsVideoUploadClient.uploadWithVideoAndImg(vodSessionCreateInfo, new VODSVideoUploadCallback() {
                @Override
                public void onUploadSucceed(String videoId, String imageUrl) {
                    //上传成功返回视频ID和图片URL.
                    Log.d(TAG, "onUploadSucceed" + "videoId:" + videoId + "imageUrl" + imageUrl);
                    WritableMap map = Arguments.createMap();
                    map.putString("vid", videoId);
                    map.putString("imageUrl", imageUrl);
                    promise.resolve(map);
                    vodsVideoUploadClient.release();
                }

                @Override
                public void onUploadFailed(String code, String message) {
                    //上传失败返回错误码和message.错误码有详细的错误信息请开发者仔细阅读
                    Log.d(TAG, "onUploadFailed" + "code" + code + "message" + message);
                    promise.reject(code, message);
                    vodsVideoUploadClient.release();
                }

                @Override
                public void onUploadProgress(long uploadedSize, long totalSize) {
                    //上传的进度回调,非UI线程
                    Log.d(TAG, "onUploadProgress" + uploadedSize * 100 / totalSize);
                    // progress = uploadedSize * 100 / totalSize;
                    // handler.sendEmptyMessage(0);
                }

                @Override
                public void onSTSTokenExpried() {
                    Log.d(TAG, "onSTSTokenExpried");
                    //STS token过期之后刷新STStoken，如正在上传将会断点续传
                    // vodsVideoUploadClient.refreshSTSToken(accessKeyId,accessKeySecret,securityToken,expriedTime);
                    promise.reject("401", "token 过期，请重新操作");
                    vodsVideoUploadClient.release();
                }

                @Override
                public void onUploadRetry(String code, String message) {
                    //上传重试的提醒
                    Log.d(TAG, "onUploadRetry" + "code" + code + "message" + message);
                }

                @Override
                public void onUploadRetryResume() {
                    //上传重试成功的回调.告知用户重试成功
                    Log.d(TAG, "onUploadRetryResume");
                }
            });
        }

    }

    private class ActivityResultListener extends BaseActivityEventListener {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_RECORD) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    int type = data.getIntExtra(AliyunVideoRecorder.RESULT_TYPE, 0);
                    if (type == AliyunVideoRecorder.RESULT_TYPE_CROP) {
                        String path = data.getStringExtra(CropKey.RESULT_KEY_CROP_PATH);
                        // Toast.makeText(context, "文件路径为 " + path + " 时长为 " + data.getLongExtra(CropKey.RESULT_KEY_DURATION, 0), Toast.LENGTH_SHORT).show();

                        promise.resolve(path);
                    } else if (type == AliyunVideoRecorder.RESULT_TYPE_RECORD) {
//                        Toast.makeText(context, "文件路径为 " + data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH), Toast.LENGTH_SHORT).show();

                        promise.resolve(data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH));
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(context, "取消录制", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void initAssetPath() {
        String path = StorageUtils.getCacheDirectory(context).getAbsolutePath() + File.separator + Utils.QU_NAME + File.separator;
        File filter = new File(new File(path), "filter");

        String[] list = filter.list();
        if (list == null || list.length == 0) {
            return;
        }
        eff_dirs = new String[list.length + 1];
        eff_dirs[0] = null;
        for (int i = 0; i < list.length; i++) {
            eff_dirs[i + 1] = filter.getPath() + "/" + list[i];
        }
//        eff_dirs = new String[]{
//                null,
//                path + "filter/chihuang",
//                path + "filter/fentao",
//                path + "filter/hailan",
//                path + "filter/hongrun",
//                path + "filter/huibai",
//                path + "filter/jingdian",
//                path + "filter/maicha",
//                path + "filter/nonglie",
//                path + "filter/rourou",
//                path + "filter/shanyao",
//                path + "filter/xianguo",
//                path + "filter/xueli",
//                path + "filter/yangguang",
//                path + "filter/youya",
//                path + "filter/zhaoyang",
//                path + "filter/mosaic",
//                path + "filter/blur",
//                path + "filter/bulge",
//                path + "filter/false",
//                path + "filter/gray",
//                path + "filter/haze",
//                path + "filter/invert",
//                path + "filter/miss",
//                path + "filter/pixellate",
//                path + "filter/rgb",
//                path + "filter/sepiatone",
//                path + "filter/threshold",
//                path + "filter/tone",
//                path + "filter/vignette"
//        };
    }

    private boolean isPermissionOK() {
        PermissionChecker checker = new PermissionChecker(context.getCurrentActivity());
        boolean isPermissionOK = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checker.checkPermission();
        if (!isPermissionOK) {
            ToastUtils.s(context, "请给予相应的权限。");
        }
        return isPermissionOK;
    }
}

