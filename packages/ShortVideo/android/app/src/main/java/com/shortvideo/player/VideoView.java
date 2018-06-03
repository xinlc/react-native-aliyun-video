package com.shortvideo.player;

import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;

/**
 * Created by lichao on 2018/1/15.
 */

public class VideoView extends FrameLayout implements TextureView.SurfaceTextureListener, LifecycleEventListener {

    private static final String TAG = "VideoView";
    private final String CACHE_DIR = Environment.getExternalStorageDirectory() + "/ShortVideoCache";
    private IAliyunVodPlayer.PlayerState mPlayerState;  // 用来记录前后台切换时的状态，以供恢复。
    private AliyunVodPlayer mVodPlayer;
    private ThemedReactContext mContext;
    private FrameLayout mContainer;
    private NiceTextureView mTextureView;

    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private AliyunVidSts mVidSts;


    public enum Events {
        EVENT_PREPARED("onVideoPrepared"),
        EVENT_END("onVideoEnd"),
        EVENT_ERROR("onVideoError"),
        EVENT_LOAD_START("onVideoLoadStart"),
        EVENT_LOAD_END("onVideoLoadEnd"),
        EVENT_LOAD_PROGRESS("onVideoLoadProgress"),
        EVENT_SEEK("onVideoSeek");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private RCTEventEmitter mEventEmitter;

    public VideoView(ThemedReactContext context) {
        super(context);
        mContext = context;
        context.addLifecycleEventListener(this);
        mEventEmitter = context.getJSModule(RCTEventEmitter.class);

        initVodPlayer();
        initContainer();
        initTextureView();
        addTextureView();
    }

    public void start() {
        if(mVodPlayer != null) {
            mVodPlayer.start();
        }
    }
    public void pause() {
        if (mVodPlayer != null) {
            mVodPlayer.pause();
        }
    }

    private void initVodPlayer() {
        mVidSts = new AliyunVidSts();
        mVodPlayer = new AliyunVodPlayer(mContext.getApplicationContext());
        mVodPlayer.setPlayingCache(true, CACHE_DIR, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
//        mVodPlayer.setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        mVodPlayer.setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        mVodPlayer.setAutoPlay(true);
        initVodPlayerListener();
    }

    private void initContainer() {
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
    }

    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new NiceTextureView(mContext);
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    private void addTextureView() {
        mContainer.removeView(mTextureView);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        mContainer.addView(mTextureView, 0, params);
    }

    private void openVodPlayer() {
        if (mSurface == null) {
            mSurface = new Surface(mSurfaceTexture);
        }
        mVodPlayer.setSurface(mSurface);

        // 设置view的布局，宽高之类
//        if (mTextureView != null) {
//            int height = (int) (ScreenUtil.getWidth(mContext.getCurrentActivity()) * 9.0f / 16);
//            int width = ViewGroup.LayoutParams.MATCH_PARENT;
//            mTextureView.adaptVideoSize(width, height);
//        }

    }


    public void setVidSts(String vid, String akid, String aks, String token) {
        if(mVodPlayer != null) {
            mVidSts.setVid(vid);
            mVidSts.setAcId(akid);
            mVidSts.setAkSceret(aks);
            mVidSts.setSecurityToken(token);
            mVodPlayer.prepareAsync(mVidSts);
        }
    }
    public void setVid(String vid) {
        mVidSts.setVid(vid);
    }
    public void setAcId(String akid) {
        mVidSts.setAcId(akid);
    }
    public void setAkSceret(String aks) {
        mVidSts.setAkSceret(aks);
    }
    public void setSecurityToken(String token) {
        mVidSts.setSecurityToken(token);
    }
    public void setLocalSrc(String src) {
        if(mVodPlayer != null) {
            AliyunLocalSource.AliyunLocalSourceBuilder asb = new AliyunLocalSource.AliyunLocalSourceBuilder();
            asb.setSource(src);
            mVodPlayer.prepareAsync(asb.build());
        }
    }
    public void setLooping(boolean looping) {
        if(mVodPlayer != null) {
            mVodPlayer.setCirclePlay(looping);
        }
    }
    public void setAutoPlay(boolean auto) {
        if(mVodPlayer != null) {
            mVodPlayer.setAutoPlay(auto);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surfaceTexture;
            openVodPlayer();
        } else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if(mVodPlayer != null) {
            mVodPlayer.surfaceChanged();
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return mSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void initVodPlayerListener() {
        mVodPlayer.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                //准备完成触发
                Log.i("initVodPlayerListener", "1");
                sendEvent(Events.EVENT_PREPARED, Arguments.createMap());
//                mVodPlayer.start();
            }
        });
        mVodPlayer.setOnFirstFrameStartListener(new IAliyunVodPlayer.OnFirstFrameStartListener() {
            @Override
            public void onFirstFrameStart() {
                //首帧显示触发
                Log.i("initVodPlayerListener", "2");
            }
        });
        mVodPlayer.setOnErrorListener(new IAliyunVodPlayer.OnErrorListener() {
            @Override
            public void onError(int arg0, int arg1, String msg) {
                //出错时处理，查看接口文档中的错误码和错误消息
                Log.i("initVodPlayerListener3", msg);
                WritableMap event = Arguments.createMap();
                event.putString("code", String.valueOf(arg0));
                event.putString("msg", msg);
                sendEvent(Events.EVENT_ERROR, event);
            }
        });
        mVodPlayer.setOnCompletionListener(new IAliyunVodPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                //播放正常完成时触发
                Log.i("initVodPlayerListener", "4");
                sendEvent(Events.EVENT_END, Arguments.createMap());
            }

        });
        mVodPlayer.setOnLoadingListener(new IAliyunVodPlayer.OnLoadingListener() {
            @Override
            public void onLoadStart() {
                sendEvent(Events.EVENT_LOAD_START, Arguments.createMap());
            }

            @Override
            public void onLoadEnd() {
                sendEvent(Events.EVENT_LOAD_END, Arguments.createMap());
            }

            @Override
            public void onLoadProgress(int i) {
//                WritableMap event = Arguments.createMap();
                // sendEvent(Events.EVENT_LOAD_PROGRESS, event);
            }
        });

        mVodPlayer.setOnSeekCompleteListener(new IAliyunVodPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {
                //seek完成时触发
                Log.i("initVodPlayerListener", "5");
            }
        });
        mVodPlayer.setOnStoppedListner(new IAliyunVodPlayer.OnStoppedListener() {
            @Override
            public void onStopped() {
                //使用stop功能时触发
                Log.i("initVodPlayerListener", "6");
            }
        });
        mVodPlayer.setOnChangeQualityListener(new IAliyunVodPlayer.OnChangeQualityListener() {
            @Override
            public void onChangeQualitySuccess(String finalQuality) {
                //视频清晰度切换成功后触发
                Log.i("initVodPlayerListener", "7");
            }
            @Override
            public void onChangeQualityFail(int code, String msg) {
                //视频清晰度切换失败时触发
                Log.i("initVodPlayerListener", "8");
            }
        });
        mVodPlayer.setOnCircleStartListener(new IAliyunVodPlayer.OnCircleStartListener(){
            @Override
            public void onCircleStart() {
                //循环播放开始
                Log.i("initVodPlayerListener", "9");
            }
        });
    }


    /**
     * 向js发送事件
     * @param name
     * @param event
     */
    private void sendEvent(Events name, WritableMap event) {
//        WritableMap event = Arguments.createMap();
        mEventEmitter.receiveEvent(
                getId(),        // native层和js层两个视图会依据getId()而关联在一起
                name.mName,     // 事件名称
                event           // 事件携带的数据
        );
    }


    public void releasePlayer() {
        if(mContainer != null) {
            mContainer.removeView(mTextureView);
        }
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if(mVodPlayer != null) {
            mVodPlayer.stop();
            mVodPlayer.release();
            mVodPlayer = null;
        }
    }

    // 恢复播放状态
    private void resumePlayerState() {
        if (mPlayerState == IAliyunVodPlayer.PlayerState.Paused && mVodPlayer != null) {
            mVodPlayer.pause();
        } else if (mPlayerState == IAliyunVodPlayer.PlayerState.Started && mVodPlayer != null) {
            mVodPlayer.start();
        }
    }

    // 保存播放状态
    private void savePlayerState() {
        if(mVodPlayer != null) {
            mPlayerState = mVodPlayer.getPlayerState();
            if (mVodPlayer.isPlaying()) {
                //然后再暂停播放器
                mVodPlayer.pause();
            }
        }
    }


    @Override
    public void onHostResume() {
        resumePlayerState();
    }

    @Override
    public void onHostPause() {
        savePlayerState();
    }

    @Override
    public void onHostDestroy() {
        // 在 videoViewManager.createViewInstance 中销毁
        // releasePlayer();
    }
}
