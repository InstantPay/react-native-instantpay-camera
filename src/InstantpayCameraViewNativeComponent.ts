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
 * @property {('LOW' | 'MEDIUM' | 'HIGH')} quality - The quality of the captured photo.
 * @property {('AUTO' | 'ON' | 'OFF')} flash - The flash mode for capturing the photo.
 * @property {boolean} saveToGallery - Whether to save the captured photo to the gallery.
 * @property {boolean} base64ImageOutput - Whether to output the captured photo as a base64 string.
 * @property {boolean} compressBase64ImageOutput - Whether to compress the base64 image output.
 * @property {boolean} captureSound - Capture Sound enable/disable while taking photo. default:true
 */
interface PhotoCaptureConfig {
    quality?: WithDefault<"LOW" | "MEDIUM" | "HIGH", "MEDIUM">;
    flash?: WithDefault<"AUTO" | "ON" | "OFF", "AUTO">;
    saveToGallery?: boolean;
	maxWidth?: Double;
	maxHeight?: Double;
	base64ImageOutput?: boolean;
	compressBase64ImageOutput?: boolean;
	captureSound?: boolean;
}

/**
 * Camera Lens Facing Type
 * @typedef {('FRONT' | 'BACK')} CameraFacing
 * @description Represents the camera lens facing direction.
 * - 'FRONT': Indicates the front-facing camera (selfie camera).
 * - 'BACK': Indicates the rear-facing camera (main camera).
 */
type CameraFacing = WithDefault<"FRONT" | "BACK", "BACK">;

/**
 * Torch Mode Type
 * @typedef {('ON' | 'OFF')} TorchMode
 * @description Represents the torch (flashlight) mode for the camera.
 * - 'ON': Indicates that the torch is turned on.
 * - 'OFF': Indicates that the torch is turned off.
 */
type TorchMode = WithDefault<"ON" | "OFF", "OFF">;


/**
 * OCR Language Type
 * @typedef {('EN' | 'HI')} OCRLanguage
 * @description Represents the language options for OCR (Optical Character Recognition) processing.
 * - 'EN': Indicates English language for OCR processing.
 * - 'HI': Indicates Hindi language for OCR processing.
 */
type OCRLanguage = WithDefault<"EN" | "HI", "EN">;

/**
 * OCR Configuration Interface
 * @interface OCRConfig
 * @property {string} language - The language to be used for OCR processing (e.g., 'en' for English).
 * @property {boolean} detectAadhaar - Whether to enable Aadhaar card detection during OCR processing.
 * @property {boolean} detectPan - Whether to enable PAN card detection during OCR processing.
 */
interface OCRConfig {
	language?: OCRLanguage;
	detectAadhaar?: boolean;
	detectPan?: boolean;
}

/**
 * Text Detected Callbacks Events Parameters
 * @interface DetectTextEventData
 * @property {string} detectedText - The text that was detected by the OCR process.
 * @description This interface defines the structure of the event data that is passed to the onTextDetectedCallback when text is detected in the captured image. The detectedText property contains the text that was recognized by the OCR process.
 */
interface DetectTextEventData {
	detectedText: string;
}

interface NativeProps extends ViewProps {
  	color?: ColorValue;
	cameraFacing?: CameraFacing;
	torchMode?: TorchMode;
	photoCaptureConfig?: PhotoCaptureConfig;
	ocrConfig?: OCRConfig;
	onCloseCallback?: BubblingEventHandler<Readonly<CloseEventData>>;
	onErrorCallback?: BubblingEventHandler<Readonly<ErrorEventData>>;
    onSuccessCallback?: BubblingEventHandler<Readonly<SuccessEventData>>;
	onCameraStartedCallback?: BubblingEventHandler<Readonly<CameraStartedEventData>>;
	onPhotoCapturedCallback?: BubblingEventHandler<Readonly<PhotoCapturedEventData>>;
	onTextDetectedCallback?: BubblingEventHandler<Readonly<DetectTextEventData>>;
}

export default codegenNativeComponent<NativeProps>('InstantpayCameraView');

