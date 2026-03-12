export enum ErrorCodes {
    CAMERA_PERMISSION_DENIED = 'Camera permission denied',
    CAMERA_PERMISSION_NOT_GRANTED = 'Camera permission not granted',
    CAMERA_UNAVAILABLE = 'Camera unavailable',
    UNKNOWN_ERROR = 'Unknown error',
    NO_ACTIVITY = 'No activity found to handle camera intent',
    CAMERA_FAILED = 'Failed to open camera',
    IMAGE_PROCESSING_FAILED = 'Failed to process captured image',
    USER_CANCELLED = 'User cancelled the camera',
    STOP_CAMERA_FAILED = 'Failed to stop camera',
    FAILED_CAPTURE = 'Failed to capture photo',
}