//
//  VODUploadSimpleClient.h
//  VODUpload
//
//  Created by Worthy on 2017/11/2.
//  Copyright © 2017年 Leigang. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "VODUploadSVideoModel.h"

@protocol VODUploadSVideoClientDelegate <NSObject>

- (void)uploadSuccessWithVid:(NSString *)vid imageUrl:(NSString *)imageUrl;

- (void)uploadFailedWithCode:(NSString *)code message:(NSString *)message;

- (void)uploadProgressWithUploadedSize:(long long)uploadedSize totalSize:(long long)totalSize;

- (void)uploadTokenExpired;

- (void)uploadRetry;

- (void)uploadRetryResume;

@end

@interface VODUploadSVideoClient : NSObject

@property (nonatomic, weak) id<VODUploadSVideoClientDelegate> delegate;

@property (nonatomic, assign) BOOL transcode;

@property (nonatomic, assign) uint32_t maxRetryCount;

@property (nonatomic, assign) NSTimeInterval timeoutIntervalForRequest;

@property (nonatomic, copy) NSString * recordDirectoryPath;

@property (nonatomic, assign) NSInteger uploadPartSize;

- (BOOL)uploadWithVideoPath:(NSString *)videoPath
                  imagePath:(NSString *)imagePath
                 svideoInfo:(VodSVideoInfo *)svideoInfo
                accessKeyId:(NSString *)accessKeyId
            accessKeySecret:(NSString *)accessKeySecret
                accessToken:(NSString *)accessToken;

- (void)pause;

- (void)resume;

- (void)refreshWithAccessKeyId:(NSString *)accessKeyId
               accessKeySecret:(NSString *)accessKeySecret
                   accessToken:(NSString *)accessToken
                    expireTime:(NSString *)expireTime;

- (void)cancel;

@end
