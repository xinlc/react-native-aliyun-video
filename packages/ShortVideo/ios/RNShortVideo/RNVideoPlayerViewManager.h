//
//  RNVideoPlayerViewManager.h
//  RNAliyunShortVideo
//
//  Created by Leo on 05/07/2018.
//


#import <React/RCTViewManager.h>
#import "RNVideoPlayerView.h"

@interface RNVideoPlayerViewManager : RCTViewManager

@property (strong) RNVideoPlayerView *player;
@property (assign) BOOL playling;
@end


