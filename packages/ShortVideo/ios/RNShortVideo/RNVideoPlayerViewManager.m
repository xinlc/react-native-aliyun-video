//
//  RNVideoPlayerViewManager.m
//  RNAliyunShortVideo
//
//  Created by Leo on 05/07/2018.
//

#import <React/RCTBridge.h>
#import "RNVideoPlayerViewManager.h"
#import "RNVideoPlayerView.h"




@implementation RNVideoPlayerViewManager

RCT_EXPORT_MODULE()

@synthesize bridge = _bridge;
- (UIView *)view
{
  RNVideoPlayerView *player = [[RNVideoPlayerView alloc] initWithEventDispatcher:self.bridge.eventDispatcher];
  return player;
}

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

RCT_EXPORT_VIEW_PROPERTY(looping, BOOL)
//RCT_EXPORT_VIEW_PROPERTY(fullscreen, BOOL)
RCT_EXPORT_VIEW_PROPERTY(playing, BOOL)
RCT_EXPORT_VIEW_PROPERTY(src, NSDictionary)
RCT_EXPORT_VIEW_PROPERTY(localSrc, NSString)

RCT_EXPORT_VIEW_PROPERTY(onVideoEnd, RCTBubblingEventBlock)

RCT_EXPORT_VIEW_PROPERTY(onVideoLoadStart, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onVideoLoadEnd, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onVideoPrepared, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onVideoError, RCTBubblingEventBlock)

- (void)start{
  [self.player.player resume];
}

- (void)pause{
  [self.player.player pause];
}


@end

