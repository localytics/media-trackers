//
//  LLMediaTracker.h
//  Copyright (C) 2014 Char Software Inc., DBA Localytics
//
//  This code is provided under the Localytics Modified BSD License.
//  A copy of this license has been distributed in a file called LICENSE
//  with this source code.
//
// Please visit www.localytics.com for more information.
//

#import <UIKit/UIKit.h>

@interface LLMediaTracker: NSObject

- (instancetype)initWithContentLength:(double)videoLength;
- (instancetype)initWithContentLength:(double)videoLength eventAttributes:(NSDictionary *)attributes;

- (void)processTaggingData;

- (void)playAtTime:(double)currentTime;
- (void)stopAtTime:(double)currentTime;
- (void)seekToTime:(double)newTime;
- (void)completeAtTime:(double)currentTime;


@end
