package com.instantpaycamera

enum class CameraFacing {
    FRONT,
    BACK
}

enum class CaptureQuality {
    LOW,
    MEDIUM,
    HIGH
}

enum class CameraFlash {
    AUTO,
    ON,
    OFF,
}

data class PhotoCaptureConfigMetadata(
    val cameraFacing: CameraFacing,
    val quality: CaptureQuality,
    val flash: CameraFlash,
    val saveToGallery: Boolean,
    val maxWidth: Int?,
    val maxHeight: Int?,
    val base64ImageOutput: Boolean?,
    val compressBase64ImageOutput: Boolean?,
    val captureSound: Boolean?,
)
