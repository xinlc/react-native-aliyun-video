import { NativeModules } from 'react-native';

// funciton
// int size = options.hasKey("size") ? options.getInt("size") : 1; // 0->360p，1:480p，2->540p，3->720p
// int ratio = options.hasKey("ratio") ? options.getInt("ratio") : 0; // 0->1:1,1->3:4,2->9:16
// int min = options.hasKey("min") ? options.getInt("min") * 1000 : 2000; // 2000
// int max = options.hasKey("max") ? options.getInt("max") * 1000 : 20000; // 20000
// int videoQuality = options.hasKey("quality") ? options.getInt("quality") : 3; // 0->SSD, 1->HD, 2->SD, 3->LD, 4->PD, 5->EPD
// int gop = options.hasKey("gop") ? options.getInt("gop") : 5;    // 建议GOP值为5-30
// public void recordShortVideo(ReadableMap params, Promise promise)


// ReadableMap:
// String token = params.getString("token");
// String key = params.getString("key");
// String mp4Path = params.getString("mp4Path");
// public void uploadVideo(ReadableMap params, Promise promise)
module.exports = NativeModules.RNShortVideo;

