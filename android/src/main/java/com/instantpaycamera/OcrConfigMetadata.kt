package com.instantpaycamera

enum class OCRLanguage {
    EN,
    HI
}

data class OcrConfigMetadata(
    val language: OCRLanguage?,
    val detectAadhaar: Boolean?,
    val detectPan: Boolean?,
)
