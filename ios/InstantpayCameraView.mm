#import "InstantpayCameraView.h"

#import <React/RCTConversions.h>

#import <react/renderer/components/InstantpayCameraViewSpec/ComponentDescriptors.h>
#import <react/renderer/components/InstantpayCameraViewSpec/Props.h>
#import <react/renderer/components/InstantpayCameraViewSpec/RCTComponentViewHelpers.h>

///Add Below import to send Event to RN
#import <react/renderer/components/InstantpayCameraViewSpec/EventEmitters.h>

#import "RCTFabricComponentsPlugins.h"

#if __has_include("InstantpayCamera-Swift.h")
#import "InstantpayCamera-Swift.h"
#elif __has_include("InstantpayCamera/InstantpayCamera-Swift.h")
#import "InstantpayCamera/InstantpayCamera-Swift.h"
#endif

using namespace facebook::react;

@implementation InstantpayCameraView {
    //UIView * _view;
    IpayCameraView * _view; //Added Swift View
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<InstantpayCameraViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const InstantpayCameraViewProps>();
    _props = defaultProps;

    //_view = [[UIView alloc] init];
    _view = [[IpayCameraView alloc] init]; //Added Swift View

    self.contentView = _view;
      
    __weak InstantpayCameraView *weakSelf = self;
      
    _view.onSendReactNativeEvent = ^(NSString* actionType, NSDictionary<NSString *, id>* eventData) {
        __strong InstantpayCameraView *strongSelf = weakSelf;
        if (!strongSelf) return;
        [strongSelf handleSubmit:actionType eventData:eventData];
    };
      
    
  }

  return self;
}

///View is being reused by Fabric, so reset/cleanup state before next use (not destroyed).
- (void)prepareForRecycle {
    [super prepareForRecycle];

    //NSLog(@"[InstantpayCodePush.mm] updateBundle called.");
}

///Destory class Instance : Can force Fabric to destroy it and create a fresh instance every time by overriding shouldBeRecycled
+ (BOOL)shouldBeRecycled {
    return NO; // Forces a new [[CameraView alloc] init] every time
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &oldViewProps = *std::static_pointer_cast<InstantpayCameraViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<InstantpayCameraViewProps const>(props);

    if (oldViewProps.color != newViewProps.color) {
        [_view setBackgroundColor: RCTUIColorFromSharedColor(newViewProps.color)];
    }
    
    ///cameraFacing
    if (oldViewProps.cameraFacing != newViewProps.cameraFacing) {
        NSString *facing;
        switch (newViewProps.cameraFacing) {
            case InstantpayCameraViewCameraFacing::FRONT :
                facing = @"front";
                break;
            case InstantpayCameraViewCameraFacing::BACK :
                facing = @"back";
                break;
            default:
                facing = @"back";
                break;
        }
        [_view setValue:facing forKey:@"cameraFacing"];
    }
    
    ///Torch Mode
    if (oldViewProps.torchMode != newViewProps.torchMode){
        NSString *torchMode;
        switch (newViewProps.torchMode) {
            case InstantpayCameraViewTorchMode::ON :
                torchMode = @"ON";
                break;
            case InstantpayCameraViewTorchMode::OFF :
                torchMode = @"OFF";
                break;
            default:
                torchMode = @"NOT_SET";
                break;
        }
        [_view setValue:torchMode forKey:@"torchMode"];
    }
    
    ///Photo Capture Config
    
    if(newViewProps.photoCaptureConfig.enableConfig){
        NSMutableDictionary *options = [NSMutableDictionary dictionary];
        
        if (oldViewProps.photoCaptureConfig.quality != newViewProps.photoCaptureConfig.quality){
            switch (newViewProps.photoCaptureConfig.quality) {
                case InstantpayCameraViewQuality::HIGH :
                    options[@"quality"] = @"HIGH";
                    break;
                case InstantpayCameraViewQuality::MEDIUM :
                    options[@"quality"] = @"MEDIUM";
                    break;
                case InstantpayCameraViewQuality::LOW :
                    options[@"quality"] = @"LOW";
                    break;
                    
                default:
                    options[@"quality"] = @"MEDIUM";
                    break;
            }
        }
        
        if (oldViewProps.photoCaptureConfig.flash != newViewProps.photoCaptureConfig.flash){
            switch (newViewProps.photoCaptureConfig.flash) {
                case InstantpayCameraViewFlash::ON :
                    options[@"flash"] = @"ON";
                    break;
                case InstantpayCameraViewFlash::OFF :
                    options[@"flash"] = @"OFF";
                    break;
                default:
                    options[@"flash"] = @"AUTO";
                    break;
            }
        }
        
        if (oldViewProps.photoCaptureConfig.saveToGallery != newViewProps.photoCaptureConfig.saveToGallery){
            options[@"saveToGallery"] = newViewProps.photoCaptureConfig.saveToGallery ? @YES : @NO;
        }
        
        if (oldViewProps.photoCaptureConfig.maxWidth != newViewProps.photoCaptureConfig.maxWidth){
            options[@"maxWidth"] = @(newViewProps.photoCaptureConfig.maxWidth);
        }
        
        if (oldViewProps.photoCaptureConfig.maxHeight != newViewProps.photoCaptureConfig.maxHeight){
            options[@"maxHeight"] = @(newViewProps.photoCaptureConfig.maxHeight);
        }
        
        if (oldViewProps.photoCaptureConfig.base64ImageOutput != newViewProps.photoCaptureConfig.base64ImageOutput){
            options[@"base64ImageOutput"] = newViewProps.photoCaptureConfig.base64ImageOutput ? @YES : @NO;
        }
        
        if (oldViewProps.photoCaptureConfig.compressBase64ImageOutput != newViewProps.photoCaptureConfig.compressBase64ImageOutput){
            options[@"compressBase64ImageOutput"] = newViewProps.photoCaptureConfig.compressBase64ImageOutput ? @YES : @NO;
        }
        
        if (oldViewProps.photoCaptureConfig.captureSound != newViewProps.photoCaptureConfig.captureSound){
            options[@"captureSound"] = newViewProps.photoCaptureConfig.captureSound ? @YES : @NO;
        }
        
        if (oldViewProps.photoCaptureConfig.enableConfig != newViewProps.photoCaptureConfig.enableConfig){
            options[@"enableConfig"] = newViewProps.photoCaptureConfig.enableConfig ? @YES : @NO;
        }
        
        if (options.count > 0) {
            [_view updatePhotoCaptureOptions:options];
        }
    }
    
    ///OCR Config : Detect Text
    NSMutableDictionary *ocrOptions = [NSMutableDictionary dictionary];
    
    if(oldViewProps.ocrConfig.language != newViewProps.ocrConfig.language){
        switch (newViewProps.ocrConfig.language) {
            case InstantpayCameraViewLanguage::EN :
                ocrOptions[@"language"] = @"EN";
                break;
            case InstantpayCameraViewLanguage::HI :
                ocrOptions[@"language"] = @"HI";
                break;
                
            default:
                ocrOptions[@"language"] = @"EN";
                break;
        }
    }
    
    if(oldViewProps.ocrConfig.detectAadhaar != newViewProps.ocrConfig.detectAadhaar){
        ocrOptions[@"detectAadhaar"] = newViewProps.ocrConfig.detectAadhaar ? @YES : @NO;
    }
    
    if(oldViewProps.ocrConfig.detectPan != newViewProps.ocrConfig.detectPan){
        ocrOptions[@"detectPan"] = newViewProps.ocrConfig.detectPan ? @YES : @NO;
    }
    
    if(ocrOptions.count > 0){
        [_view updateOcrConfiguration:ocrOptions];
    }
    

    [super updateProps:props oldProps:oldProps];
}

- (void)handleSubmit:(NSString*)actionType eventData:(NSDictionary<NSString *, id> *)eventData
{
    if(!_eventEmitter) {
        NSLog(@"❌ EventEmitter is null");
        return;
    }
    
    if([actionType  isEqual: @"CLOSE"]){
        //React side onCloseCallback fn but here OnCloseCallback
        InstantpayCameraViewEventEmitter::OnCloseCallback closeEvent = {
            .message = [eventData[@"message"] UTF8String]
        };
        
        std::dynamic_pointer_cast<const InstantpayCameraViewEventEmitter>(self->_eventEmitter)->onCloseCallback(closeEvent);
    }
    else if([actionType  isEqual: @"ERRORED"]){
        //React side onErrorCallback fn but here OnErrorCallback
        InstantpayCameraViewEventEmitter::OnErrorCallback errorEvent = {
            .code = [eventData[@"code"] UTF8String],
            .errorMessage = [eventData[@"errorMessage"] UTF8String],
            .errorCause = [eventData[@"errorCause"] UTF8String],
            .recoverySuggestion = [eventData[@"recoverySuggestion"] UTF8String],
        };
        
        std::dynamic_pointer_cast<const InstantpayCameraViewEventEmitter>(self->_eventEmitter)->onErrorCallback(errorEvent);
    }
    else if([actionType  isEqual: @"SUCCESS"]){
        //React side onSuccessCallback fn but here OnSuccessCallback
        InstantpayCameraViewEventEmitter::OnSuccessCallback successEvent = {
            .message = [eventData[@"message"] UTF8String],
        };
        
        std::dynamic_pointer_cast<const InstantpayCameraViewEventEmitter>(self->_eventEmitter)->onSuccessCallback(successEvent);
    }
    else if([actionType  isEqual: @"CAMERA_STARTED"]){
        //React side onCameraStartedCallback fn but here OnCameraStartedCallback
        InstantpayCameraViewEventEmitter::OnCameraStartedCallback cameraStartedEvent = {
            .status = [eventData[@"status"] UTF8String],
        };
        
        std::dynamic_pointer_cast<const InstantpayCameraViewEventEmitter>(self->_eventEmitter)->onCameraStartedCallback(cameraStartedEvent);
    }
    else if([actionType  isEqual: @"PHOTO_CAPTURED"]){
        //React side onPhotoCapturedCallback fn but here OnPhotoCapturedCallback
        
        NSNumber *sizeNumber = eventData[@"size"];
        double sizeValue = sizeNumber != nil && sizeNumber != (id)kCFNull ? [sizeNumber doubleValue] : 0.0;
        
        NSNumber *widthNumber = eventData[@"imageWidth"];
        double widthValue = widthNumber != nil && widthNumber != (id)kCFNull ? [widthNumber doubleValue] : 0.0;
        
        NSNumber *heightNumber = eventData[@"imageHeight"];
        double heightValue = heightNumber != nil && heightNumber != (id)kCFNull ? [heightNumber doubleValue] : 0.0;
        
        NSString *base64ImageContent = eventData[@"base64Image"];
        std::string base64ImageValue = base64ImageContent != nil && base64ImageContent != (id)kCFNull ? [base64ImageContent UTF8String] : "";
        
        NSString *base64ImageCompressContent = eventData[@"base64ImageCompress"];
        std::string base64ImageCompressValue = base64ImageCompressContent != nil && base64ImageCompressContent != (id)kCFNull ? [base64ImageCompressContent UTF8String] : "";
        
        
        InstantpayCameraViewEventEmitter::OnPhotoCapturedCallback photoCapturedEvent = {
            .name = [eventData[@"name"] UTF8String],
            .mimeType = [eventData[@"mimeType"] UTF8String],
            .uri = [eventData[@"uri"] UTF8String],
            .path = [eventData[@"path"] UTF8String],
            .size = sizeValue,
            .imageWidth = widthValue,
            .imageHeight = heightValue,
            .base64Image = base64ImageValue,
            .base64ImageCompress = base64ImageCompressValue,
        };
        
        std::dynamic_pointer_cast<const InstantpayCameraViewEventEmitter>(self->_eventEmitter)->onPhotoCapturedCallback(photoCapturedEvent);
    }
    else if([actionType  isEqual: @"TEXT_DETECTED"]){
        //React side onTextDetectedCallback fn but here OnTextDetectedCallback
        
        NSString *matchedValueContent = eventData[@"matchedValue"];
        std::string matchedValueContentValue = matchedValueContent != nil && matchedValueContent != (id)kCFNull ? [matchedValueContent UTF8String] : "";
        
        InstantpayCameraViewEventEmitter::OnTextDetectedCallback textDetectedEvent = {
            .detectedText = [eventData[@"detectedText"] UTF8String],
            .blocks = [eventData[@"blocks"] UTF8String],
            .matchedValue = matchedValueContentValue,
        };
        
        std::dynamic_pointer_cast<const InstantpayCameraViewEventEmitter>(self->_eventEmitter)->onTextDetectedCallback(textDetectedEvent);
    }
}

@end






