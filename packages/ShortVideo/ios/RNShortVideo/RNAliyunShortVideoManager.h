//
//  RNAliyunShortVideoManager.h
//  RNAliyunShortVideo
//
//  Created by Leo on 05/07/2018.
//

#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <VODUpload/VODUploadSVideoClient.h>

@interface RNAliyunShortVideoManager : NSObject  <RCTBridgeModule, VODUploadSVideoClientDelegate>
//@interface RNAliyunShortVideoManager : NSObject  <RCTBridgeModule>
@property (strong, nonatomic) RCTPromiseResolveBlock resolve;
@property (nonatomic, strong) VODUploadSVideoClient *client;
@end
