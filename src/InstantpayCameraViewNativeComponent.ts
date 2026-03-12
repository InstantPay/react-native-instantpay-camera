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

import type { BubblingEventHandler, Double, WithDefault } from 'react-native/Libraries/Types/CodegenTypes'

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
 * Photo Captured Callbacks Events Parameters
 */
interface PhotoCapturedEventData {
	name?: string;
	mimeType?: string;
	uri?: string;
	path?: string;
	size?: Double;
	imageWidth?: Double;
	imageHeight?: Double;
	base64Image?: string;
	base64ImageCompress?: string;
}

/**
 * Photo Capture Configuration Interface
 * @interface PhotoCaptureConfig
 * @property {('FRONT' | 'BACK')} cameraFacing - The camera to use for capturing the photo (front or back).
 * @property {('LOW' | 'MEDIUM' | 'HIGH')} quality - The quality of the captured photo.
 * @property {('AUTO' | 'ON' | 'OFF')} flash - The flash mode for capturing the photo.
 * @property {boolean} saveToGallery - Whether to save the captured photo to the gallery.
 * @property {boolean} base64ImageOutput - Whether to output the captured photo as a base64 string.
 * @property {boolean} compressBase64ImageOutput - Whether to compress the base64 image output.
 * @property {boolean} captureSound - Capture Sound enable/disable while taking photo. default:true
 */
interface PhotoCaptureConfig {
	cameraFacing?: WithDefault<"FRONT" | "BACK", "BACK">;
    quality?: WithDefault<"LOW" | "MEDIUM" | "HIGH", "MEDIUM">;
    flash?: WithDefault<"AUTO" | "ON" | "OFF", "AUTO">;
    saveToGallery?: boolean;
	base64ImageOutput?: boolean;
	compressBase64ImageOutput?: boolean;
	captureSound?: boolean;
}

interface NativeProps extends ViewProps {
  	color?: ColorValue;
	photoCaptureConfig?: PhotoCaptureConfig;
	onCloseCallback?: BubblingEventHandler<Readonly<CloseEventData>>;
	onErrorCallback?: BubblingEventHandler<Readonly<ErrorEventData>>;
    onSuccessCallback?: BubblingEventHandler<Readonly<SuccessEventData>>;
	onCameraStartedCallback?: BubblingEventHandler<Readonly<CameraStartedEventData>>;
	onPhotoCapturedCallback?: BubblingEventHandler<Readonly<PhotoCapturedEventData>>;
}

export default codegenNativeComponent<NativeProps>('InstantpayCameraView');

