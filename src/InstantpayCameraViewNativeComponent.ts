/* import {
	codegenNativeComponent,
	type ColorValue,
	type ViewProps,
} from 'react-native'; */
import { 
	type ColorValue,
	type ViewProps,
} from 'react-native';

import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

import type { BubblingEventHandler, WithDefault } from 'react-native/Libraries/Types/CodegenTypes'

/**
 * Fix : Could not find a declaration file for module 'react-native/Libraries/Types/CodegenTypes'.
 * npm install --save-dev @types/react-native
 */

//BubblingEventHandler<EventType> → for events that bubble up through the view hierarchy.

//DirectEventHandler<EventType> → for events that don’t bubble (fired only on the target).


/**
 * Error Callbacks Events Parameters
 */
interface ErrorEventData {
	code: string;
    errorMessage : string;
    errorCause? : string;
    recoverySuggestion? : string;
}

/**
 * Close Callbacks Events Parameters
 */
interface CloseEventData {
    message: string;
}

/**
 * Success Callbacks Events Parameters
 */
interface SuccessEventData {
    message: string;
}

/**
 * Camera Started Callbacks Events Parameters
 */
interface CameraStartedEventData {
    status: boolean;
}

/**
 * Photo Capture Configuration Interface
 * @interface PhotoCaptureConfig
 * @property {('LOW' | 'MEDIUM' | 'HIGH')} quality - The quality of the captured photo.
 * @property {('AUTO' | 'ON' | 'OFF')} flash - The flash mode for capturing the photo.
 * @property {boolean} saveToGallery - Whether to save the captured photo to the gallery.
 */
interface PhotoCaptureConfig {
    quality?: WithDefault<"LOW" | "MEDIUM" | "HIGH", "MEDIUM">;
    flash?: WithDefault<"AUTO" | "ON" | "OFF", "AUTO">;
    saveToGallery?: boolean;
}

interface NativeProps extends ViewProps {
  	color?: ColorValue;
	photoCaptureConfig?: PhotoCaptureConfig;
	onCloseCallback?: BubblingEventHandler<Readonly<CloseEventData>>;
	onErrorCallback?: BubblingEventHandler<Readonly<ErrorEventData>>;
    onSuccessCallback?: BubblingEventHandler<Readonly<SuccessEventData>>;
	onCameraStartedCallback?: BubblingEventHandler<Readonly<CameraStartedEventData>>;
}

export default codegenNativeComponent<NativeProps>('InstantpayCameraView');

