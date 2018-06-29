//
//  VODUploadSVideoModel.h
//  VODUpload
//
//  Created by Worthy on 2017/11/2.
//  Copyright © 2017年 Leigang. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface VodSVideoInfo : NSObject
@property (nonatomic, strong) NSString* title;
@property (nonatomic, strong) NSString* tags;
@property (nonatomic, strong) NSString* desc;
@property (nonatomic, strong) NSNumber* cateId;
@property (nonatomic, assign) BOOL isProcess;
@property (nonatomic, assign) BOOL isShowWaterMark;
@property (nonatomic, assign) NSNumber* priority;
@end
