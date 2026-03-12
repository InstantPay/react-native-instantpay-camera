package com.instantpaycamera

import android.graphics.Color
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.InstantpayCameraViewManagerInterface
import com.facebook.react.viewmanagers.InstantpayCameraViewManagerDelegate

@ReactModule(name = InstantpayCameraViewManager.NAME)
class InstantpayCameraViewManager : SimpleViewManager<InstantpayCameraView>(),
    InstantpayCameraViewManagerInterface<InstantpayCameraView> {
    private val mDelegate: ViewManagerDelegate<InstantpayCameraView>

    init {
        mDelegate = InstantpayCameraViewManagerDelegate(this)
    }

    override fun getDelegate(): ViewManagerDelegate<InstantpayCameraView>? {
        return mDelegate
    }

    override fun getName(): String {
        return NAME
    }

    companion object {
        const val NAME = "InstantpayCameraView"
    }

    public override fun createViewInstance(context: ThemedReactContext): InstantpayCameraView {
        return InstantpayCameraView(context)
    }

    override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any> {
        return mutableMapOf(
            "onCloseCallback" to mapOf("registrationName" to "onCloseCallback"),
            "onErrorCallback" to mapOf("registrationName" to "onErrorCallback"),
            "onSuccessCallback" to mapOf("registrationName" to "onSuccessCallback"),
            "onCameraStartedCallback" to mapOf("registrationName" to "onCameraStartedCallback"),
            "onPhotoCapturedCallback" to mapOf("registrationName" to "onPhotoCapturedCallback"),
        )
    }

    @ReactProp(name = "color")
    override fun setColor(view: InstantpayCameraView?, color: Int?) {
        view?.setBackgroundColor(color ?: Color.TRANSPARENT)
    }

    @ReactProp(name = "photoCaptureConfig")
    override fun setPhotoCaptureConfig(view: InstantpayCameraView?, config: ReadableMap?){
        if (config == null) {
            view?.setPhotoCaptureConfig(null)
            return
        }

        val normalized = Arguments.createMap()

        /*if (!listOf("low","medium","high").contains(quality)) {
            sendError("Invalid captureConfig.quality value")
        }*/

        //Camera Facing
        var validCameraFacing = CameraFacing.BACK
        if (config.hasKey("cameraFacing")) {

            val cameraType = config.getString("cameraFacing")

            val validCameraType = when (cameraType) {
                "FRONT" -> CameraFacing.FRONT
                "BACK" -> CameraFacing.BACK
                else -> CameraFacing.BACK
            }

            validCameraFacing = validCameraType
        }

        // QUALITY
        var validCaptureQuality = CaptureQuality.MEDIUM
        if (config.hasKey("quality")) {

            val quality = config.getString("quality")

            val validQuality = when (quality) {
                //"low", "medium", "high" -> quality
                "LOW" -> CaptureQuality.LOW
                "MEDIUM" -> CaptureQuality.MEDIUM
                "HIGH" -> CaptureQuality.HIGH
                else -> CaptureQuality.MEDIUM
            }
            //normalized.putString("quality", validQuality)
            validCaptureQuality = validQuality
        }

        // FLASH
        var validCameraFlash  = CameraFlash.AUTO
        if (config.hasKey("flash")) {

            val flash = config.getString("flash")

            val validFlash = when (flash) {
                "ON" -> CameraFlash.ON
                "OFF" -> CameraFlash.OFF
                "AUTO" -> CameraFlash.AUTO
                else -> CameraFlash.AUTO
            }

            //normalized.putString("flash", validFlash.name)
            validCameraFlash  = validFlash
        }

        // SAVE TO GALLERY
        if (config.hasKey("saveToGallery")) {
            val saveToGallery = config.getBoolean("saveToGallery")
            normalized.putBoolean("saveToGallery", saveToGallery)

        } else {
            normalized.putBoolean("saveToGallery", false)
        }

        // MAX WIDTH
        var validMaxWidth: Int? = null
        if (config.hasKey("maxWidth")) {
            val maxWidth = config.getInt("maxWidth")
            if (maxWidth > 0) {
                //normalized.putInt("maxWidth", maxWidth)
                validMaxWidth = maxWidth
            }
        }

        // MAX HEIGHT
        var validMaxHeight: Int? = null
        if (config.hasKey("maxHeight")) {
            val maxHeight = config.getInt("maxHeight")
            if (maxHeight > 0) {
                //normalized.putInt("maxHeight", maxHeight)
                validMaxHeight = maxHeight
            }
        }

        // Output in form of base64
        if (config.hasKey("base64ImageOutput")) {
            val base64ImageOutput = config.getBoolean("base64ImageOutput")
            normalized.putBoolean("base64ImageOutput", base64ImageOutput)

        } else {
            normalized.putBoolean("base64ImageOutput", false)
        }

        // Output in form of Compress base64
        if (config.hasKey("compressBase64ImageOutput")) {
            val compressBase64ImageOutput = config.getBoolean("compressBase64ImageOutput")
            normalized.putBoolean("compressBase64ImageOutput", compressBase64ImageOutput)

        } else {
            normalized.putBoolean("compressBase64ImageOutput", false)
        }

        // Play Capture Sound while taking Photo
        if (config.hasKey("captureSound")) {
            val captureSound = config.getBoolean("captureSound")
            normalized.putBoolean("captureSound", captureSound)

        } else {
            normalized.putBoolean("captureSound", true)
        }

        view?.setPhotoCaptureConfig(
            PhotoCaptureConfigMetadata(
                cameraFacing = validCameraFacing,
                quality = validCaptureQuality,
                flash = validCameraFlash,
                saveToGallery = normalized.getBoolean("saveToGallery"),
                maxWidth = validMaxWidth,
                maxHeight = validMaxHeight,
                base64ImageOutput = normalized.getBoolean("base64ImageOutput"),
                compressBase64ImageOutput = normalized.getBoolean("compressBase64ImageOutput"),
                captureSound = normalized.getBoolean("captureSound"),
            )
        )
    }
}
