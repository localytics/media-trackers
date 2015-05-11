//
//  LLMediaTracker.m
//  Copyright (C) 2014 Char Software Inc., DBA Localytics
//
//  This code is provided under the Localytics Modified BSD License.
//  A copy of this license has been distributed in a file called LICENSE
//  with this source code.
//
// Please visit www.localytics.com for more information.
//

#import "LLMediaTracker.h"
#import "Localytics+Internal.h"

@interface LLMediaTracker ()

@property (nonatomic, strong) NSDictionary *userDefinedAttributes;
@property (nonatomic, strong) NSDate *loadTime;
@property (nonatomic, assign) UIInterfaceOrientation orientation;
@property (nonatomic, assign) BOOL didChangeOrientation;
@property (nonatomic, assign) int stopCount;
@property (nonatomic, assign) int playCount;
@property (nonatomic, assign) int seekForwardCount;
@property (nonatomic, assign) int seekBackwardsCount;
@property (nonatomic, assign) double videoDuration;
@property (nonatomic, assign) double timeWatched;
@property (nonatomic, assign) double currentTime;
@property (nonatomic, assign) BOOL didComplete;

@property (nonatomic, assign) BOOL isSeeking;

@end


@implementation LLMediaTracker

- (instancetype)initWithContentLength:(double)videoLength
{
    self = [super init];
    if (self)
    {
        _videoDuration = videoLength;
        _orientation = [UIApplication sharedApplication].statusBarOrientation;
        _loadTime = [NSDate date];
        [self registerObservers];
    }
    return self;
}

- (instancetype)initWithContentLength:(double)videoLength eventAttributes:(NSDictionary *)attributes;
{
    if (self = [self initWithContentLength:videoLength])
    {
        _userDefinedAttributes = attributes;
    }
    return self;
}

- (void)setCurrentTime:(double)currentTime {
    _currentTime = currentTime;
    
    if (_currentTime > _timeWatched)
    {
        _timeWatched = _currentTime;
    }
}

-(void)registerObservers
{
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(orientationChanged:)
                                                 name:UIApplicationDidChangeStatusBarOrientationNotification
                                               object:nil];
}

-(void)unregisterObservers
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)processTaggingData
{
    long percentWatched = lround(self.timeWatched / self.videoDuration * 100);
    NSMutableDictionary *videoAttributes = [NSMutableDictionary dictionaryWithDictionary:self.userDefinedAttributes];
    
    videoAttributes[@"Final Orientation"] =					[LLMediaTracker stringForOrientation:self.orientation];
    
    videoAttributes[@"Did Complete"] =                  [LLMediaTracker stringForBool:self.didComplete];

    videoAttributes[@"Percent completed"] =               [LLMediaTracker stringForInt:(int)percentWatched];
    videoAttributes[@"Media Length"] =                  [LLMediaTracker stringForDouble:self.videoDuration];
    videoAttributes[@"Duration Consumed"] =             [LLMediaTracker stringForDouble:self.timeWatched];
    
    [Localytics tagEvent:@"Media Consumed" attributes:videoAttributes];
    LocalyticsLog(@"%@", videoAttributes);
}

+ (NSString *)stringForOrientation:(UIInterfaceOrientation)orientation
{
    switch (orientation) {
        case UIInterfaceOrientationLandscapeLeft:
        case UIInterfaceOrientationLandscapeRight:
            return @"Landscape";
        default:
            return @"Portrait";
    }
}

+ (NSString *)stringForBool:(BOOL)boolean
{
    return boolean ? @"true" : @"false";
}

+ (NSString *)stringForInt:(int)count
{
    return [NSString stringWithFormat:@"%d", count];
}

+ (NSString *)stringForDouble:(double)dbl
{
    return [NSString stringWithFormat:@"%.f", floor(dbl)];
}

+ (NSString *)stringForSeconds:(int)seconds
{
    return [NSString stringWithFormat:@"%d seconds", seconds];
}

- (void)playAtTime:(double)currentTime
{
    self.playCount += 1;
    self.currentTime = currentTime;
}

- (void)stopAtTime:(double)currentTime
{
    self.stopCount += 1;
    self.currentTime = currentTime;
}

- (void)seekToTime:(double)newTime
{
    if (newTime > self.currentTime)
    {
        self.seekForwardCount += 1;
    }
    else if (newTime < self.currentTime)
    {
        self.seekBackwardsCount += 1;
    }
    
    self.currentTime = newTime;
}

- (void)completeAtTime:(double)currentTime {
    self.currentTime = currentTime;
    self.didComplete = YES;
}

#pragma mark - private methods

- (void)orientationChanged:(NSNotification *)notification
{
    self.orientation = [notification.userInfo[UIApplicationStatusBarOrientationUserInfoKey] integerValue];
    self.didChangeOrientation = YES;
}

- (void)dealloc
{
    [self unregisterObservers];
}


@end
