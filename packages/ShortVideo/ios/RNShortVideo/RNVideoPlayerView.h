//
//  RNVideoPlayerView.h
//  RNAliyunShortVideo
//
//  Created by Leo on 05/07/2018.
//


#import <React/RCTView.h>
#import <AliyunVodPlayerSDK/AliyunVodPlayer.h>

@class RCTEventDispatcher;

@interface RNVideoPlayerView : UIView

@property (strong, nonatomic) AliyunVodPlayer *player;

@property (nonatomic, copy) RCTBubblingEventBlock onVideoEnd;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoPrepared;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoLoadStart;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoLoadEnd;
@property (nonatomic, copy) RCTBubblingEventBlock onVideoError;


- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher NS_DESIGNATED_INITIALIZER;
@end

