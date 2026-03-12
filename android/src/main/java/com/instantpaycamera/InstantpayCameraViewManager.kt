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
            "onCameraStartedCallback" to mapOf("registrationName" to "onCameraStartedCallback"),
            "onCloseCallback" to mapOf("registrationName" to "onCloseCallback"),
            "onErrorCallback" to mapOf("registrationName" to "onErrorCallback"),
            "onSuccessCallback" to mapOf("registrationName" to "onSuccessCallback"),
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

        view?.setPhotoCaptureConfig(
            PhotoCaptureConfigMetadata(
                quality = validCaptureQuality,
                flash = validCameraFlash,
                saveToGallery = normalized.getBoolean("saveToGallery"),
                maxWidth = validMaxWidth,
                maxHeight = validMaxHeight,
            )
        )
    }
}
