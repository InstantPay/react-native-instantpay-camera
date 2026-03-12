package com.instantpaycamera

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
    val quality: CaptureQuality,
    val flash: CameraFlash,
    val saveToGallery: Boolean,
    val maxWidth: Int?,
    val maxHeight: Int?
)
