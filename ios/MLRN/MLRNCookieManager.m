#import "MLRNCookieManager.h"
#import <React/RCTLog.h>

@implementation MLRNCookieManager

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(setCookie:(NSString *)url
                  value:(NSString *)value
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    NSURL *cookieURL = [NSURL URLWithString:url];
    if (!cookieURL) {
        reject(@"invalid_url", @"Invalid URL provided", nil);
        return;
    }

    NSHTTPCookieStorage *cookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    
    // Create cookie properties
    NSMutableDictionary *cookieProperties = [NSMutableDictionary dictionary];
    [cookieProperties setObject:value forKey:NSHTTPCookieValue];
    [cookieProperties setObject:cookieURL.host forKey:NSHTTPCookieDomain];
    [cookieProperties setObject:cookieURL.path ?: @"/" forKey:NSHTTPCookiePath];
    [cookieProperties setObject:@"TRUE" forKey:NSHTTPCookieSecure];
    
    // Create and store the cookie
    NSHTTPCookie *cookie = [NSHTTPCookie cookieWithProperties:cookieProperties];
    if (cookie) {
        [cookieStorage setCookie:cookie];
        RCTLogInfo(@"[MLRNCookieManager] Set cookie for domain: %@", cookieURL.host);
        resolve(@YES);
    } else {
        reject(@"cookie_creation_failed", @"Failed to create cookie", nil);
    }
}

RCT_EXPORT_METHOD(getCookie:(NSString *)url
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    NSURL *cookieURL = [NSURL URLWithString:url];
    if (!cookieURL) {
        reject(@"invalid_url", @"Invalid URL provided", nil);
        return;
    }

    NSHTTPCookieStorage *cookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    NSArray *cookies = [cookieStorage cookiesForURL:cookieURL];
    
    if (cookies.count > 0) {
        NSHTTPCookie *cookie = cookies.firstObject;
        RCTLogInfo(@"[MLRNCookieManager] Retrieved cookie for domain: %@", cookieURL.host);
        resolve(cookie.value);
    } else {
        RCTLogInfo(@"[MLRNCookieManager] No cookie found for domain: %@", cookieURL.host);
        resolve(nil);
    }
}

@end 