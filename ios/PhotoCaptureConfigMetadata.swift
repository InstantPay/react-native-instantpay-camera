//
//  PhotoCaptureConfigMetadata.swift
//  InstantpayCamera
//
//  Created by Dhananjay kumar on 21/03/26.
//

import Foundation

enum CaptureQuality: String, Codable {
    case HIGH = "HIGH"
    case MEDIUM = "MEDIUM"
    case LOW = "LOW"
}

enum CameraFlash: String, Codable {
    case AUTO = "AUTO"
    case ON = "ON"
    case OFF = "OFF"
}

public struct PhotoCaptureConfigMetadata: Codable {
    var photoConfigurationStatus: Bool = false
    var quality: CaptureQuality = CaptureQuality.MEDIUM
    var flash: CameraFlash = CameraFlash.AUTO
    var saveToGallery: Bool = false
    var maxWidth: Int? = 0
    var maxHeight: Int? = 0
    var base64ImageOutput: Bool? = false
    var compressBase64ImageOutput: Bool? = false
    var captureSound: Bool? = false
    
}
