//
//  AliyunRootViewController.h
//  RNAliyunShortVideo
//
//  Created by Leo on 05/07/2018.
//

#import <UIKit/UIKit.h>

@protocol AliyunRootViewControllerDelegate <NSObject>

- (void)recordResolved:(NSString *)filePath;

@end

@interface AliyunRootViewController : UIViewController
 @property (assign, nonatomic) id<AliyunRootViewControllerDelegate> delegate;
 @property (nonatomic, strong) NSDictionary *defaultOptions;
 @property (nonatomic, retain) NSMutableDictionary *options;

- (void)setConfiguration:(NSDictionary *)options;
@end
