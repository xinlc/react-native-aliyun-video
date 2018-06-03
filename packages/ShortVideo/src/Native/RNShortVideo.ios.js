import { NativeModules } from 'react-native';

// funciton
// public void recordShortVideo(Promise promise)


// ReadableMap:
// String token = params.getString("token");
// String key = params.getString("key");
// String mp4Path = params.getString("mp4Path");
// public void uploadVideo(ReadableMap params, Promise promise)
module.exports = NativeModules.RNShortVideo;

