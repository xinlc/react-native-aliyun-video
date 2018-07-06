//
//  AliyunRootViewController.m
//  RNAliyunShortVideo
//
//  Created by Leo on 05/07/2018.
//
#import "AliyunRootViewController.h"
#import <AssetsLibrary/AssetsLibrary.h>
#import <AliyunVideoSDK/AliyunVideoSDK.h>


#define DEBUG_TEST 0

#define RGBToColor(r, g, b)  [UIColor colorWithRed:((r) / 255.0) green:((g) / 255.0) blue:((b) / 255.0) alpha:1.0]
#define ScreenWidth CGRectGetWidth([UIScreen mainScreen].bounds)
#define ScreenHeight CGRectGetHeight([UIScreen mainScreen].bounds)
#define DebugModule 0b100101

typedef NS_OPTIONS(NSUInteger, DebugModuleOption) {
    DebugModuleOptionVideo = 1 << 5,
    DebugModuleOptionMagicCamera = 1 << 4,
    DebugModuleOptionImportEdit = 1 << 3,
    DebugModuleOptionImportClip = 1 << 2,
    DebugModuleOptionLive = 1 << 1,
    DebugModuleOptionComposition = 1 << 0,
};

@interface AliyunRootViewController ()

@property (assign, nonatomic) DebugModuleOption moduleOption;
@property (assign, nonatomic) BOOL isClipConfig;
@property (assign, nonatomic) BOOL isPhotoToRecord;
@end

@implementation AliyunRootViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.moduleOption = DebugModule;
  
    [self setupSDKBaseVersionUI];

    
//    UIImageView *image = [[UIImageView alloc] initWithImage:[self imageNamed:@"root_bg"]];
//    image.frame = CGRectMake(0, 0, ScreenWidth, ScreenHeight);
//    [self.view addSubview:image];
//    [self setupButtons];
  
  [[UIApplication sharedApplication] setStatusBarHidden:TRUE];
  
  AliyunVideoRecordParam *videoRecordParam = [[AliyunVideoRecordParam alloc] init];
  videoRecordParam.ratio = AliyunVideoVideoRatio1To1;
  videoRecordParam.size = AliyunVideoVideoSize360P;
  videoRecordParam.minDuration = 2;
  videoRecordParam.maxDuration = 30;
  videoRecordParam.position = AliyunCameraPositionBack;
  videoRecordParam.beautifyStatus = YES;
  videoRecordParam.beautifyValue = 100;
//  videoRecordParam.torchMode = AliyunCameraTorchModeOff;
  
  UIViewController *recordViewController = [[AliyunVideoBase shared] createRecordViewControllerWithRecordParam:videoRecordParam];
  [AliyunVideoBase shared].delegate = (id)self;
  [self.navigationController pushViewController:recordViewController animated:YES];
  
//  UIViewController *vc = [[AliyunMediator shared] recordModule];
//  [vc setValue:self forKey:@"delegate"];
//  [self.navigationController pushViewController:vc animated:YES];
  
}

- (void)viewDidDisappear {
  
}

- (void)setupButtons
{

    CGFloat width = 360;
    CGFloat height = 480;
    
    CGFloat gapWidth = width/3;
    CGFloat gapHeight = height/4;
    
    UIView *contentView = [[UIView alloc] initWithFrame:CGRectMake((ScreenWidth-width)/2, (ScreenHeight-height)/2, width, height)];
    [self.view addSubview:contentView];
    int count = 0;
    for (int i = 0; i < 6; i++) {
        BOOL shouldAdd = self.moduleOption & 1 << (5-i);
        if (shouldAdd) {
            UIView *view = [self createElementWithIndex:i];
            int column = count % 2 + 1;
            int row = count / 2 + 1;
            [contentView addSubview:view];
            view.center = CGPointMake(gapWidth*column, gapHeight*row);
            count++;
        }
        
    }
}

-(UIView *)createElementWithIndex:(int)index {
    CGFloat width = 80 ,height = 100;
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, width, height)];
    UIButton *button = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, width, width)];
    [view addSubview:button];
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(0, width, width, height-width)];
    [label setTextAlignment:NSTextAlignmentCenter];
    [label setTextColor:[UIColor whiteColor]];
    [view addSubview:label];
    
    switch (index) {
        case 0:
            [button addTarget:self action:@selector(buttonVideoClick:) forControlEvents:UIControlEventTouchUpInside];
            [button setBackgroundImage:[self imageNamed:@"vedio"] forState:UIControlStateNormal];
            [label setText:NSLocalizedString(@"拍摄", nil)];
            break;
        case 1:
            [button addTarget:self action:@selector(butttonMaigcCameraClick:) forControlEvents:UIControlEventTouchUpInside];
            [button setBackgroundImage:[self imageNamed:@"camerra"] forState:UIControlStateNormal];
            [label setText:NSLocalizedString(@"魔法相机", nil)];
            break;
        case 2:
            [button addTarget:self action:@selector(buttonImportEditClick:) forControlEvents:UIControlEventTouchUpInside];
            [button setBackgroundImage:[self imageNamed:@"import_edit"] forState:UIControlStateNormal];
            [label setText:NSLocalizedString(@"导入编辑", nil)];
            break;
        case 3:
            [button addTarget:self action:@selector(buttonCompositionClick:) forControlEvents:UIControlEventTouchUpInside];
            [button setBackgroundImage:[self imageNamed:@"import_cut"] forState:UIControlStateNormal];
            [label setText:NSLocalizedString(@"导入裁剪", nil)];
            break;
        case 4:
            [button setBackgroundImage:[self imageNamed:@"live"] forState:UIControlStateNormal];
            [label setText:NSLocalizedString(@"直播", nil)];
            break;
        case 5:
            [button addTarget:self action:@selector(buttonComponentClick:) forControlEvents:UIControlEventTouchUpInside];
            [button setBackgroundImage:[self imageNamed:@"others"] forState:UIControlStateNormal];
            [label setText:NSLocalizedString(@"其他", nil)];
            break;
        default:
            break;
    }
    return view;
}




- (void)setupSDKBaseVersionUI {
    AliyunVideoUIConfig *config = [[AliyunVideoUIConfig alloc] init];
    
    config.backgroundColor = RGBToColor(35, 42, 66);
    config.timelineBackgroundCollor = RGBToColor(35, 42, 66);
    config.timelineDeleteColor = [UIColor redColor];
    config.timelineTintColor = RGBToColor(239, 75, 129);
    config.durationLabelTextColor = [UIColor redColor];
    config.cutTopLineColor = [UIColor redColor];
    config.cutBottomLineColor = [UIColor redColor];
    config.noneFilterText = @"无滤镜";
    config.hiddenDurationLabel = NO;
    config.hiddenFlashButton = NO;
    config.hiddenBeautyButton = NO;
    config.hiddenCameraButton = NO;
    config.hiddenImportButton = NO;
    config.hiddenDeleteButton = NO;
    config.hiddenFinishButton = NO;
    config.recordOnePart = NO;
//    config.filterArray = @[@"炽黄",@"粉桃",@"海蓝",@"红润",@"灰白",@"经典",@"麦茶",@"浓烈",@"柔柔",@"闪耀",@"鲜果",@"雪梨",@"阳光",@"优雅",@"朝阳",@"波普",@"光圈",@"海盐",@"黑白",@"胶片",@"焦黄",@"蓝调",@"迷糊",@"思念",@"素描",@"鱼眼",@"马赛克",@"模糊"];
    config.imageBundleName = @"QPSDK";
//    config.filterBundleName = @"FilterResource";
    config.recordType = AliyunVideoRecordTypeCombination;
    config.showCameraButton = NO;
    
    [[AliyunVideoBase shared] registerWithAliyunIConfig:config];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.navigationController.navigationBar.hidden = YES;
}

- (void)clipLayerBoundsWithButton:(UIButton *)button {
    button.layer.cornerRadius = 25;
    button.layer.masksToBounds = YES;
}

- (UIStatusBarStyle)preferredStatusBarStyle
{
    return UIStatusBarStyleLightContent;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - tool
- (UIImage *)imageNamed:(NSString *)name {

    NSString *path = [NSString stringWithFormat:@"%@.bundle/%@",[AliyunVideoBase shared].videoUIConfig.imageBundleName,name];
    return [UIImage imageNamed:path];

}

#pragma mark - action

- (IBAction)buttonVideoClick:(id)sender {
    UIViewController *vc = [[AliyunMediator shared] recordModule];
    [vc setValue:self forKey:@"delegate"];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)butttonMaigcCameraClick:(id)sender {
    UIViewController *vc = [[AliyunMediator shared] magicCameraModule];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)buttonImportEditClick:(id)sender {
    _isClipConfig = NO;
    UIViewController *vc = [[AliyunMediator shared] configureViewController];
    [vc setValue:self forKey:@"delegate"];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)buttonCompositionClick:(id)sender {
    _isClipConfig = YES;
    UIViewController *vc = [[AliyunMediator shared] configureViewController];
    [vc setValue:self forKey:@"delegate"];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)buttonComponentClick:(id)sender {
     UIViewController *vc = [[AliyunMediator shared] uiComponentModule];
    [self.navigationController pushViewController:vc animated:YES];
}

#pragma mark - RecordParamViewControllerDelegate
- (void)toRecordViewControllerWithMediaConfig:(id)config {

    UIViewController *recordViewController = [[AliyunVideoBase shared] createRecordViewControllerWithRecordParam:(AliyunVideoRecordParam*)config];
    [AliyunVideoBase shared].delegate = (id)self;
    [self.navigationController pushViewController:recordViewController animated:YES];

}

#pragma mark - ConfigureViewControllerdelegate
- (void)configureDidFinishWithMedia:(AliyunMediaConfig *)mediaConfig {
    if (_isClipConfig) {

        UIViewController *vc = [[AliyunMediator shared] cropModule];
        [vc setValue:mediaConfig forKey:@"cutInfo"];
        [vc setValue:self forKey:@"delegate"];
        [self.navigationController pushViewController:vc animated:YES];
    }else {
        UIViewController *vc = [[AliyunMediator shared] editModule];
        [vc setValue:mediaConfig forKey:@"compositionConfig"];
        [self.navigationController pushViewController:vc animated:YES];
    }
}

#pragma mark - PhotoViewControllerDelgate
- (void)recodBtnClick:(UIViewController *)vc {
    self.isPhotoToRecord = YES;
    UIViewController *recordVC = [[AliyunMediator shared] recordViewController];
    [recordVC setValue:self forKey:@"delegate"];
    [recordVC setValue:[vc valueForKey:@"cutInfo"] forKey:@"quVideo"];
    [recordVC setValue:@(NO) forKey:@"isSkipEditVC"];
    [self.navigationController pushViewController:recordVC animated:YES];
}

- (void)videoBase:(AliyunVideoBase *)base cutCompeleteWithCropViewController:(UIViewController *)cropVC image:(UIImage *)image {
    //裁剪图片
    if (image) {
        UIImageWriteToSavedPhotosAlbum(image, self, @selector(image:didFinishSavingWithError:contextInfo:), NULL);
    }
}

- (void)cropFinished:(UIViewController *)cropViewController videoPath:(NSString *)videoPath sourcePath:(NSString *)sourcePath {
    
    ALAssetsLibrary *library = [[ALAssetsLibrary alloc] init];
    [library writeVideoAtPathToSavedPhotosAlbum:[NSURL fileURLWithPath:videoPath] completionBlock:^(NSURL *assetURL, NSError *error) {
        if (error) {
            NSLog(@"裁剪完成，保存到相册失败");
            return;
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.navigationController popViewControllerAnimated:YES];
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"裁剪完成" message:@"已保存到手机相册" delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
            [alert show];
        });
    }];
}

- (void)cropFinished:(UIViewController *)cropViewController mediaType:(kPhotoMediaType)type photo:(UIImage *)photo videoPath:(NSString *)videoPath {
    if (type == kPhotoMediaTypePhoto) {
        UIImageWriteToSavedPhotosAlbum(photo, self, @selector(image:didFinishSavingWithError:contextInfo:), NULL);
    }
}

- (void)image:(UIImage *)image didFinishSavingWithError:(NSError *)error contextInfo:(void *)contextInfo {
    if(error != NULL){
        NSLog(@"裁剪完成，保存到相册失败");
        return;
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
//        [[UIApplication sharedApplication] setStatusBarHidden:FALSE];
        [self.navigationController popViewControllerAnimated:YES];
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"裁剪完成" message:@"已保存到手机相册" delegate:nil cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
        [alert show];
    });
}

- (void)backBtnClick:(UIViewController *)vc {
//    [[UIApplication sharedApplication] setStatusBarHidden:FALSE];
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - RecordViewControllerDelegate 
- (void)exitRecord {
    if (self.isPhotoToRecord) {
        self.isPhotoToRecord = NO;
    }
//    [[UIApplication sharedApplication] setStatusBarHidden:FALSE];
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)recoderFinish:(UIViewController *)vc videopath:(NSString *)videoPath {
    if (self.isPhotoToRecord) {
        self.isPhotoToRecord = NO;
        ALAssetsLibrary *library = [[ALAssetsLibrary alloc] init];
        [library writeVideoAtPathToSavedPhotosAlbum:[NSURL fileURLWithPath:videoPath] completionBlock:^(NSURL *assetURL, NSError *error) {
            if (error) {
                NSLog(@"录制完成，保存到相册失败");
                return;
            }
            dispatch_async(dispatch_get_main_queue(), ^{
//              [[UIApplication sharedApplication] setStatusBarHidden:FALSE];
                [self.navigationController popViewControllerAnimated:YES];
            });
        }];
        return;
    }
    UIViewController *editVC = [[AliyunMediator shared] editViewController];
    NSString *outputPath = [[vc valueForKey:@"recorder"] valueForKey:@"taskPath"];
    [editVC setValue:outputPath forKey:@"taskPath"];
    [editVC setValue:[vc valueForKey:@"quVideo"] forKey:@"config"];
    [self.navigationController pushViewController:editVC animated:YES];
}

- (void)recordViewShowLibrary:(UIViewController *)vc {
    UIViewController *compositionVC = [[AliyunMediator shared] compositionViewController];
    AliyunMediaConfig *mediaConfig = [[AliyunMediaConfig alloc] init];
    mediaConfig.fps = 25;
    mediaConfig.gop = 5;
    mediaConfig.videoQuality = 1;
    mediaConfig.cutMode = AliyunMediaCutModeScaleAspectFill;
    mediaConfig.encodeMode = AliyunEncodeModeHardH264;
    mediaConfig.outputSize = CGSizeMake(540, 720);
    mediaConfig.videoOnly = NO;
    [compositionVC setValue:mediaConfig forKey:@"compositionConfig"];
    [self.navigationController pushViewController:compositionVC animated:YES];

}

#pragma mark - AliyunVideoBaseDelegate
-(void)videoBaseRecordVideoExit {
    NSLog(@"退出录制");
//    [self.navigationController popViewControllerAnimated:YES];
  if (self.delegate && [self.delegate respondsToSelector:@selector(recordResolved:)]) {
    [self.delegate recordResolved: @""];
  }
}

- (void)videoBase:(AliyunVideoBase *)base recordCompeleteWithRecordViewController:(UIViewController *)recordVC videoPath:(NSString *)videoPath {
    NSLog(@"录制完成  %@", videoPath);
    ALAssetsLibrary *library = [[ALAssetsLibrary alloc] init];
    [library writeVideoAtPathToSavedPhotosAlbum:[NSURL fileURLWithPath:videoPath]
                                completionBlock:^(NSURL *assetURL, NSError *error) {

                                  if (self.delegate && [self.delegate respondsToSelector:@selector(recordResolved:)]) {
                                 
                                    [self.delegate recordResolved: videoPath];
                                  }
//                                    dispatch_async(dispatch_get_main_queue(), ^{
//                                        [recordVC.navigationController popViewControllerAnimated:YES];
//                                    });
                                }];
}

- (AliyunVideoCropParam *)videoBaseRecordViewShowLibrary:(UIViewController *)recordVC {
    
    NSLog(@"录制页跳转Library");
    // 可以更新相册页配置
    AliyunVideoCropParam *mediaInfo = [[AliyunVideoCropParam alloc] init];
    mediaInfo.minDuration = 2.0;
    mediaInfo.maxDuration = 30;
    mediaInfo.fps = 25;
    mediaInfo.gop = 5;
    mediaInfo.videoQuality = 1;
    mediaInfo.size = AliyunVideoVideoSize360P;
    mediaInfo.ratio = AliyunVideoVideoRatio1To1;
    mediaInfo.cutMode = AliyunVideoCutModeScaleAspectFill;
    mediaInfo.videoOnly = YES;
    mediaInfo.outputPath = [NSHomeDirectory() stringByAppendingPathComponent:@"Documents/cut_save.mp4"];
    return mediaInfo;
    
}

// 裁剪
- (void)videoBase:(AliyunVideoBase *)base cutCompeleteWithCropViewController:(UIViewController *)cropVC videoPath:(NSString *)videoPath {
    
    NSLog(@"裁剪完成  %@", videoPath);
    ALAssetsLibrary *library = [[ALAssetsLibrary alloc] init];
    [library writeVideoAtPathToSavedPhotosAlbum:[NSURL fileURLWithPath:videoPath]
                                completionBlock:^(NSURL *assetURL, NSError *error) {
                                  dispatch_async(dispatch_get_main_queue(), ^{
                                    [cropVC.navigationController popViewControllerAnimated:YES];
                                    if (self.delegate && [self.delegate respondsToSelector:@selector(recordResolved:)]) {
                                      
                                      [self.delegate recordResolved: videoPath];
                                    }
                                  });
                                }];
    
}

- (AliyunVideoRecordParam *)videoBasePhotoViewShowRecord:(UIViewController *)photoVC {
    
    NSLog(@"跳转录制页");
    return nil;
}

- (void)videoBasePhotoExitWithPhotoViewController:(UIViewController *)photoVC {
    
    NSLog(@"退出相册页");
    [photoVC.navigationController popViewControllerAnimated:YES];
}


@end
