//
//  RNAliyunShortVideoManager.m
//  RNAliyunShortVideo
//
//  Created by Leo on 05/07/2018.
//

#import "RNAliyunShortVideoManager.h"
#import <React/RCTBridge.h>
#import <React/RCTLog.h>
#import <React/RCTConvert.h>
#import <React/RCTEventDispatcher.h>
#import <AliyunVideoSDK/AliyunVideoSDK.h>
#import "AliyunRootViewController.h"

#import <VODUpload/VODUploadSVideoClient.h>
#import <VODUpload/VODUploadClient.h>
#import <AssetsLibrary/AssetsLibrary.h>

#define RGBToColor(r, g, b)  [UIColor colorWithRed:((r) / 255.0) green:((g) / 255.0) blue:((b) / 255.0) alpha:1.0]
#define ScreenWidth CGRectGetWidth([UIScreen mainScreen].bounds)
#define ScreenHeight CGRectGetHeight([UIScreen mainScreen].bounds)

@implementation RNAliyunShortVideoManager{
  UINavigationController *nav;
}
RCT_EXPORT_MODULE(RNShortVideo);

@synthesize bridge = _bridge;


RCT_REMAP_METHOD(recordShortVideo, recordShortVideo:(NSDictionary *)options
                 resolve:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject){
  dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
    self.resolve = resolve;
    //    resolve(@"test");

    AliyunRootViewController *recordViewController = [[AliyunRootViewController alloc] init];
    [recordViewController setValue:self forKey:@"delegate"];
      
    // set Configuration
    [recordViewController setConfiguration:options];
    
    nav = [[UINavigationController alloc] initWithRootViewController:recordViewController];
    
    [[self getRootVC] presentViewController:nav animated:NO completion:nil];
    
    
  });
}

- (UIViewController*) getRootVC {
    UIViewController *root = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
    while (root.presentedViewController != nil) {
        root = root.presentedViewController;
    }
    
    return root;
}


// upload vadio

RCT_EXPORT_METHOD(uploadVideo:(NSDictionary *)params
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    self.resolve = resolve;
    NSString *accessKeyId = [RCTConvert NSString:params[@"accessKeyId"]];
    NSString *accessKeySecret = [RCTConvert NSString:params[@"accessKeySecret"]];
    NSString *securityToken = [RCTConvert NSString:params[@"securityToken"]];
    NSString *expriedTime = [RCTConvert NSString:params[@"expriedTime"]];
    NSString *mp4Path = [RCTConvert NSString:params[@"mp4Path"]];
    
    _client = [[VODUploadSVideoClient alloc] init];
    _client.delegate = self;
    _client.transcode = false;//是否转码，建议开启转码
    
    // init video info
    VodSVideoInfo *svideoInfo = [VodSVideoInfo new];
    svideoInfo.title = [self currentTimeStr];
    //  [svideoInfo setTitle:@"test"];
    //  [svideoInfo setDesc:@"test"];
    //  [svideoInfo setTags:@"test"];
    //  [svideoInfo setCateId:0];
    
    // get fisrt pic
    UIImage *img = [self getScreenShotImageFromVideoPath:mp4Path];
    
    NSLog(@"img  width:%g height:%g", img.size.width, img.size.height);
    
    // Create path.
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *filePath = [[paths objectAtIndex:0] stringByAppendingPathComponent:@"Image.png"];
    
    // Save image.
    [UIImagePNGRepresentation(img) writeToFile:filePath atomically:YES];
    //  ALAssetsLibrary *library = [[ALAssetsLibrary alloc] init];
    //  [library writeVideoAtPathToSavedPhotosAlbum:[NSURL fileURLWithPath:filePath] completionBlock:^(NSURL *assetURL, NSError *error) {
    //    // upload
    //    BOOL res = [client uploadWithVideoPath:mp4Path imagePath:filePath svideoInfo: svideoInfo accessKeyId:accessKeyId accessKeySecret:accessKeySecret accessToken:securityToken];
    //  }];
    NSLog(@"video Path  %@", mp4Path);
    NSLog(@"img Path  %@", filePath);
    // upload
    [_client uploadWithVideoPath:mp4Path imagePath:filePath svideoInfo: svideoInfo accessKeyId:accessKeyId accessKeySecret:accessKeySecret accessToken:securityToken];
    
}





RCT_EXPORT_METHOD(dismiss) {
  [self dismissView];
}

- (void)dismissView {
  [[UIApplication sharedApplication] setStatusBarHidden:FALSE];
  [[self getRootVC] dismissViewControllerAnimated:NO completion:nil];
//  [self.bridge.eventDispatcher sendAppEventWithName:@"RNShortVideoDismissView" body:@"dismissView"];
}

#pragma support function
/**
 *  获取视频的缩略图方法
 *
 *  @param filePath 视频的本地路径
 *
 *  @return 视频截图
 */
- (UIImage *)getScreenShotImageFromVideoPath:(NSString *)filePath{
  UIImage *shotImage;
  //视频路径URL
  NSURL *fileURL = [NSURL fileURLWithPath:filePath];
  AVURLAsset *asset = [[AVURLAsset alloc] initWithURL:fileURL options:nil];
  AVAssetImageGenerator *gen = [[AVAssetImageGenerator alloc] initWithAsset:asset];
  gen.appliesPreferredTrackTransform = YES;
  CMTime time = CMTimeMakeWithSeconds(0.0, 600);
  NSError *error = nil;
  CMTime actualTime;
  CGImageRef image = [gen copyCGImageAtTime:time actualTime:&actualTime error:&error];
  shotImage = [[UIImage alloc] initWithCGImage:image];
  CGImageRelease(image);
  return shotImage;
}

//获取当前时间戳
- (NSString *)currentTimeStr{
  NSDate* date = [NSDate dateWithTimeIntervalSinceNow:0];//获取当前时间0秒后的时间
  NSTimeInterval time=[date timeIntervalSince1970]*1000;// *1000 是精确到毫秒，不乘就是精确到秒
  NSString *timeString = [NSString stringWithFormat:@"%.0f", time];
  return timeString;
}


#pragma mark - AliyunRootViewController delegate
- (void)recordResolved:(NSString *)filePath{
  self.resolve(filePath);
  [self dismissView];
}

#pragma mark - VODUploadSVideoClientDelegate
/**
 上传成功
 @param vid 视频vid
 @param imageUrl 图片路径
 */
- (void)uploadSuccessWithVid:(NSString *)vid imageUrl:(NSString *)imageUrl{
  
  self.resolve(@{
                 @"vid":vid,
                 @"imageUrl":imageUrl
                 });
};
/**
 上传失败
 @param code 错误码
 @param message 错误日志
 */
- (void)uploadFailedWithCode:(NSString *)code message:(NSString *)message{
  self.resolve(@{
                 @"code":code,
                 @"message":message
                 });
};
/**
 上传进度
 @param uploadedSize 已上传的文件大小
 @param totalSize 文件总大小
 */
- (void)uploadProgressWithUploadedSize:(long long)uploadedSize totalSize:(long long)totalSize{
  NSLog(@"%l / %l", uploadedSize, totalSize);
};
/**
 token过期
 */
- (void)uploadTokenExpired{
  NSLog(@"uploadTokenExpired");
};
/**
 开始重试
 */
- (void)uploadRetry{
  NSLog(@"uploadRetry");
};
/**
 重试完成，继续上传
 */
- (void)uploadRetryResume{
  NSLog(@"uploadRetryResume");
};


#pragma - vodupload listener


@end

