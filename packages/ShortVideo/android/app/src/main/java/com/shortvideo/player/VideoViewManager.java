package com.shortvideo.player;

import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.shortvideo.player.VideoView.Events;

import java.util.Map;

/**
 * Created by lichao on 2018/1/15.
 */

public class VideoViewManager extends SimpleViewManager<VideoView> {

    public enum Commands {
        COMMAND_START("start", 1),
        COMMAND_PAUSE("pause", 2);

        private final String mName;
        private final int mId;

        Commands(final String name, final int id) {
            mName = name;
            mId = id;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    public static final String REACT_CLASS = "RNVideoPlayerView";
    public static final String PROP_SRC = "src";
    public static final String PROP_LOCAL_SRC = "localSrc";
    public static final String PROP_PLAYING = "playing";
    public static final String PROP_LOOPING = "looping";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected VideoView createViewInstance(ThemedReactContext reactContext) {
        return new VideoView(reactContext);
    }

    @Override
    public void onDropViewInstance(VideoView view) {
        super.onDropViewInstance(view);
        view.releasePlayer();
        Log.i(REACT_CLASS, "dropViewInstance");
    }

    @ReactProp(name = PROP_SRC)
    public void setSrc(VideoView videoView, @Nullable ReadableMap source){
        if(source != null) {
            if (source.hasKey("vid")) {
                String vid = source.getString("vid");
                String akid = source.getString("akid");
                String aks = source.getString("aks");
                String token = source.getString("token");
                videoView.setVidSts(vid, akid, aks, token);
            }
        }
    }

    @ReactProp(name = PROP_LOCAL_SRC)
    public void setLocalSrc(VideoView videoView, String src){
        videoView.setLocalSrc(src);
    }

    @ReactProp(name = PROP_LOOPING, defaultBoolean = false)
    public void setLooping(VideoView videoView, boolean looping) {
        videoView.setLooping(looping);
    }

    @ReactProp(name = PROP_PLAYING, defaultBoolean = false)
    public void setPlaying(VideoView videoView, boolean playing) {
        if (playing) {
            videoView.start();
        } else {
            videoView.pause();
        }
    }

    /**
     * 自定义事件名 native to js
     * @return
     */
    @Override
    @Nullable
    public Map getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder builder = MapBuilder.builder();
        for (Events event : Events.values()) {
            builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
        }
        return builder.build();
    }

    /**
     * 自定义事件名 js to native
     * @return
     */
    @Override
    @Nullable
    public Map<String, Integer> getCommandsMap() {
        MapBuilder.Builder builder = MapBuilder.builder();
        for (Commands command : Commands.values()) {
            builder.put(command.mName, command.mId);
        }
        return builder.build();
    }

    /**
     * 接收js事件
     * @param videoView
     * @param commandId
     * @param args
     */
    @Override
    public void receiveCommand(VideoView videoView, int commandId, @Nullable ReadableArray args) {
        if (commandId == Commands.COMMAND_PAUSE.mId) {
            videoView.pause();
        } else if (commandId == Commands.COMMAND_START.mId) {
            videoView.start();
        }
    }

}
