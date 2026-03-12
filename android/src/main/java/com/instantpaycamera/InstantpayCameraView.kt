package com.instantpaycamera

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import com.instantpaycamera.databinding.CameraViewBinding
import java.util.concurrent.Executors
import androidx.lifecycle.LifecycleOwner
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.view.TextureView
import android.view.ViewGroup
import android.view.Surface
import android.widget.Button
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.events.Event


class InstantpayCameraView(private val context: ReactContext) : FrameLayout(context){
    /*constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )*/

    private val textureView = TextureView(context)
    private val closeButton = Button(context) //For Close button
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null

    private val SUCCESS_TAG = "SUCCESS"
    private val ERROR_TAG = "ERROR"
    private val CLOSE_TAG = "CLOSE"
    private val CAMERA_STARTED_TAG = "CAMERA_STARTED"

    private val callbackMethodsName = mapOf<String,String>(
        "SUCCESS" to "onSuccessCallback",
        "ERROR" to "onErrorCallback",
        "CLOSE" to "onCloseCallback",
        "CAMERA_STARTED" to "onCameraStartedCallback",
    )

    /** Photo Capture Config Section **/
    private var photoCaptureConfig: PhotoCaptureConfigMetadata? = null

    /** End Photo Capture Config Section **/

    companion object {
        private val CLASS_TAG_NAME = "*InstantpayCameraView"
    }

    init {

        textureView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        addView(textureView)

        //Added Button Layout View
        buttonLayoutView()

        textureView.surfaceTextureListener =
            object : TextureView.SurfaceTextureListener {

                override fun onSurfaceTextureAvailable(
                    surface: SurfaceTexture,
                    width: Int,
                    height: Int
                ) {
                    checkCameraPermission()
                }

                override fun onSurfaceTextureSizeChanged(
                    surface: SurfaceTexture,
                    width: Int,
                    height: Int
                ) {}

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    return true
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
            }
    }

    /**
     * Button Layout View Setup
     */
    private fun buttonLayoutView(){

        closeButton.text = "Close"
        closeButton.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
        }

        addView(closeButton)

        closeButton.setOnClickListener {
            //On CloseButton Fire Action
            stopCamera()

            val output = Arguments.createMap().apply {
                putString("message", "Camera close button is fired!")
            }
            onSendReactNativeEvent(CLOSE_TAG, output)

            //removeFromParent() //Clear View
        }
    }

    /**
     * This method check camera permission is granted or not
     */
    private fun checkCameraPermission() {

        val permission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            val output = Arguments.createMap().apply {
                putString("code", "CAMERA_PERMISSION_NOT_GRANTED")
                putString("errorMessage", "Camera permission not granted")
            }
            onSendReactNativeEvent(ERROR_TAG, output)
            return
        }

        startCamera()
    }

    /**
     * Start Camera and set to view
     */
    private fun startCamera() {

        val activity = context.currentActivity as? LifecycleOwner

        if(activity == null){
            val output = Arguments.createMap().apply {
                putString("code", "NO_ACTIVITY")
                putString("errorMessage", "No activity found to handle camera intent")
            }
            onSendReactNativeEvent(ERROR_TAG, output)
            return
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({

            try {

                cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build()

                val surfaceTexture = textureView.surfaceTexture
                surfaceTexture?.setDefaultBufferSize(textureView.width, textureView.height)

                val surface = Surface(surfaceTexture)

                preview.setSurfaceProvider { request ->
                    request.provideSurface(surface, ContextCompat.getMainExecutor(context)) {}
                }

                cameraProvider?.unbindAll()

                camera = cameraProvider?.bindToLifecycle(
                    activity,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview
                )

                val output = Arguments.createMap().apply {
                    putString("status", "CAMERA_OPENED")
                }
                onSendReactNativeEvent(CAMERA_STARTED_TAG, output)

            } catch (e: Exception) {

                val output = Arguments.createMap().apply {
                    putString("code", "CAMERA_FAILED")
                    putString("errorMessage", "${e.message}")
                    putString("errorCause", "${e.cause}")
                    putString("recoverySuggestion", "${e.stackTrace}")
                }
                onSendReactNativeEvent(ERROR_TAG, output)
                return@addListener
            }

        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Stop Camera
     */
    private fun stopCamera() {
        CommanHelper.logPrint(CLASS_TAG_NAME, "stopCamera method is fired.")
        try {
            cameraProvider?.unbindAll()
            cameraProvider = null
            camera = null
        } catch (e: Exception) {

            val output = Arguments.createMap().apply {
                putString("code", "STOP_CAMERA_FAILED")
                putString("errorMessage", "${e.message}")
                putString("errorCause", "${e.cause}")
                putString("recoverySuggestion", "${e.stackTrace}")
            }
            onSendReactNativeEvent(ERROR_TAG, output)
            return
        }
    }

    /**
     * Clear View from parent
     */
    private fun removeFromParent() {
        val parentView = parent as? ViewGroup
        parentView?.removeView(this)
    }

    /**
     * Stop Camera View when user navigate or change the screen
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopCamera()
    }

    /**
     * Set Configuration of Capture Photo from Camera
     */
    fun setPhotoCaptureConfig(config: PhotoCaptureConfigMetadata?){
        photoCaptureConfig = config

        CommanHelper.logPrint(CLASS_TAG_NAME,"photoCaptureConfig : ${photoCaptureConfig}")
    }

    /**
     * Send Event Data from Native to JS
     */
    private fun onSendReactNativeEvent(type:String, data: WritableMap? = null){

        var event: WritableMap = Arguments.createMap()

        val nameOfEvent = callbackMethodsName[type].toString()

        if(data!=null){
            event = data
        }

        val surfaceId = UIManagerHelper.getSurfaceId(context)
        val viewId = id

        UIManagerHelper.getEventDispatcherForReactTag(context as ReactContext, viewId)
            ?.dispatchEvent(SendReactNativeEvent(
                surfaceId,
                viewId,
                nameOfEvent,
                event
            ))
    }

    /**
     * Send Event Data from Native to JS
     */
    class SendReactNativeEvent(surfaceId: Int, viewId: Int, private var type:String, private var data: WritableMap? = null) : Event<SendReactNativeEvent>(surfaceId, viewId){

        //companion object {
        //	const val EVENT_NAME = "onCancelCallbackEvent"
        //}

        override fun getEventName() = type

        override fun getCoalescingKey(): Short = 0

        override fun getEventData(): WritableMap? = data

        //override fun getEventData(): WritableMap? {
        //	return data
        //}

    }
}
