//
//  RNVideoPlayerView.m
//  RNAliyunShortVideo
//
//  Created by Leo on 05/07/2018.
//

#import "RNVideoPlayerView.h"
#import <React/RCTEventDispatcher.h>

#define PLS_SCREEN_WIDTH CGRectGetWidth([UIScreen mainScreen].bounds)
#define PLS_SCREEN_HEIGHT CGRectGetHeight([UIScreen mainScreen].bounds)

@implementation RNVideoPlayerView
{
  BOOL _looping;
  NSDictionary *_src;
  BOOL _playing;
  NSString *_localSrc;
}

- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher
{
  self = [super init];
  
  [self aliPlayer];
  NSLog(@"UIView size %f:%f", self.frame.size.width, self.frame.size.height);
  self.player.playerView.frame = CGRectMake(0, 0, PLS_SCREEN_WIDTH, PLS_SCREEN_WIDTH);
  self.player.playerView.contentMode = UIViewContentModeScaleAspectFit;
  [self addSubview:self.player.playerView];
  
  return self;
}

#pragma UI handler


#pragma mark - 播放器初始化
-(AliyunVodPlayer *)aliPlayer{
  if (!_player) {
    _player = [[AliyunVodPlayer alloc] init];
    _player.delegate = self;
    [_player setAutoPlay:YES];
    _player.quality=  0;
    _player.circlePlay = YES;
    NSArray *pathArray = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *docDir = [pathArray objectAtIndex:0];
    //maxsize MB    maxDuration 秒
    [_player setPlayingCache:YES saveDir:docDir maxSize:3000 maxDuration:100000];
  }
  return _player;
}

#pragma mark - Prop setters
- (void)setSrc:(NSDictionary *)source
{
  NSLog(@"video src set %@", source);
  _src = source;
  
  NSString *vid = _src[@"vid"];
  NSString *accessKeyId = _src[@"akid"];
  NSString *accessKeySecret = _src[@"aks"];
  NSString *securityToken = _src[@"token"];
  
  //播放方式一：使用vid+STS方式播放（点播用户推荐使用）
  [self.aliPlayer prepareWithVid:vid accessKeyId:accessKeyId accessKeySecret:accessKeySecret securityToken:securityToken];
  
}

- (void)setLocalSrc:(NSString *)localSrc
{
  _localSrc = localSrc;
  NSURL *fileUrl = [NSURL fileURLWithPath:_localSrc];//本地视频,填写文件路径
  [self.aliPlayer prepareWithURL:fileUrl];
}

- (void)setLooping:(BOOL)looping
{
  _looping = looping;
}

- (void)setPlaying:(BOOL)playing
{
  // start
  if(! _playing && playing){
      [self.player resume];
  }
  
  // pause
  if(_playing && !playing){
    [self.player pause];
  }
  _playing = playing;
  
}

#pragma mark - ali video delegate
- (void)vodPlayer:(AliyunVodPlayer *)vodPlayer onEventCallback:(AliyunVodPlayerEvent)event{
  //这里监控播放事件回调
  //主要事件如下：
  switch (event) {
    case AliyunVodPlayerEventPrepareDone:
      //播放准备完成时触发
      NSLog(@"播放准备完成");
      if (self.onVideoPrepared) {
          self.onVideoPrepared(@{
                                 @"status":@"Prepared"
                                 });
      }
     
      break;
    case AliyunVodPlayerEventPlay:
      //暂停后恢复播放时触发
      NSLog(@"暂停后恢复播放");
      break;
    case AliyunVodPlayerEventFirstFrame:
      //播放视频首帧显示出来时触发
      NSLog(@"播放视频首帧显示出来");
      break;
    case AliyunVodPlayerEventPause:
      //视频暂停时触发
      NSLog(@"视频暂停");
      break;
    case AliyunVodPlayerEventStop:
      //主动使用stop接口时触发
      NSLog(@"主动使用stop接口");
      break;
    case AliyunVodPlayerEventFinish:
      //视频正常播放完成时触发
      NSLog(@"视频正常播放完成");
          if (self.onVideoEnd) {
              self.onVideoEnd(@{
                                @"status":@"end"
                                });
          }
      break;
    case AliyunVodPlayerEventBeginLoading:
      //视频开始载入时触发
      NSLog(@"视频开始载入");
      if (self.onVideoLoadStart) {
          self.onVideoLoadStart(@{
                                 @"status":@"LoadStart"
                                 });
      }
      break;
    case AliyunVodPlayerEventEndLoading:
      //视频加载完成时触发
      NSLog(@"视频加载完成");
           if (self.onVideoLoadEnd) {
              self.onVideoLoadEnd(@{
                                      @"status":@"LoadEnd"
                                      });
           }
      break;
    case AliyunVodPlayerEventSeekDone:
      //视频Seek完成时触发
      NSLog(@"视频Seek完成");
      break;
    default:
      break;
  }
}
//- (void)vodPlayer:(AliyunVodPlayer *)vodPlayer playBackErrorModel:(ALPlayerVideoErrorModel *)errorModel{
//  //播放出错时触发，通过errorModel可以查看错误码、错误信息、视频ID、视频地址和requestId。
//   NSLog(@"播放出错");
//  self.onVideoLoadEnd(@{
//                        @"code": [NSNumber numberWithInt:errorModel.errorCode],
//                        @"msg": errorModel.errorMsg
//                        });
//}
- (void)vodPlayer:(AliyunVodPlayer*)vodPlayer willSwitchToQuality:(AliyunVodPlayerVideoQuality)quality{
  //将要切换清晰度时触发
}
- (void)vodPlayer:(AliyunVodPlayer *)vodPlayer didSwitchToQuality:(AliyunVodPlayerVideoQuality)quality{
  //清晰度切换完成后触发
}
- (void)vodPlayer:(AliyunVodPlayer*)vodPlayer failSwitchToQuality:(AliyunVodPlayerVideoQuality)quality{
  //清晰度切换失败触发
}
- (void)onCircleStartWithVodPlayer:(AliyunVodPlayer*)vodPlayer{
  //开启循环播放功能，开始循环播放时接收此事件。
}

@end

