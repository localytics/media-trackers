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

- (instancetype)initWithMediaLength:(double)mediaLength;
- (instancetype)initWithMediaLength:(double)mediaLength eventAttributes:(NSDictionary *)attributes;

- (void)tagEvent;

- (void)playAtTime:(NSTimeInterval)time;
- (void)stopAtTime:(NSTimeInterval)time;
- (void)completeAtTime:(NSTimeInterval)time;


@end
