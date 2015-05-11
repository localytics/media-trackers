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
//#import "Localytics+Internal.h"
#import "Localytics.h"

@interface LLMediaTracker ()

@property (nonatomic, strong) NSDictionary *userDefinedAttributes;
@property (nonatomic, assign) UIInterfaceOrientation orientation;
@property (nonatomic, assign) NSTimeInterval videoDuration;
@property (nonatomic, assign) NSTimeInterval timeWatched;
@property (nonatomic, assign) NSTimeInterval currentTime;
@property (nonatomic, assign) BOOL didComplete;

@end


@implementation LLMediaTracker

- (instancetype)initWithContentLength:(double)videoLength
{
    self = [super init];
    if (self)
    {
        _videoDuration = videoLength;
        _orientation = [UIApplication sharedApplication].statusBarOrientation;
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

- (void)tagEvent
{
    long percentWatched = lround(self.timeWatched / self.videoDuration * 100);
    NSMutableDictionary *videoAttributes = [NSMutableDictionary dictionaryWithDictionary:self.userDefinedAttributes];
    
    videoAttributes[@"Final Orientation"] =					[LLMediaTracker stringForOrientation:self.orientation];
    
    videoAttributes[@"Did Complete"] =                  [LLMediaTracker stringForBool:self.didComplete];

    videoAttributes[@"Percent completed"] =               [LLMediaTracker stringForInt:(int)percentWatched];
    videoAttributes[@"Media Length"] =                  [LLMediaTracker stringForDouble:self.videoDuration];
    videoAttributes[@"Duration Consumed"] =             [LLMediaTracker stringForDouble:self.timeWatched];
    
    [Localytics tagEvent:@"Media Consumed" attributes:videoAttributes];
//    LocalyticsLog(@"%@", videoAttributes);
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

- (void)playAtTime:(NSTimeInterval)currentTime
{
    self.currentTime = currentTime;
}

- (void)stopAtTime:(NSTimeInterval)currentTime
{
    self.currentTime = currentTime;
}

- (void)completeAtTime:(NSTimeInterval)currentTime {
    self.currentTime = currentTime;
    self.didComplete = YES;
}

#pragma mark - private methods

- (void)orientationChanged:(NSNotification *)notification
{
    self.orientation = [notification.userInfo[UIApplicationStatusBarOrientationUserInfoKey] integerValue];
}

- (void)dealloc
{
    [self unregisterObservers];
}


@end
