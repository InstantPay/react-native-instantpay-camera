package com.instantpaycamera

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.media.MediaActionSound
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Base64
import android.util.Log
import android.util.Size
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
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.events.Event
import java.io.ByteArrayOutputStream
import java.io.File


class InstantpayCameraView(private val context: ReactContext) : FrameLayout(context){
    /*constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )*/

    private val textureView = TextureView(context)
    private val topOverlayBar = LinearLayout(context)
    private val topOverlaySpaceBetween = View(context)
    private val rightSideContainerOfTopOverlay = LinearLayout(context)
    private val bottomOverlayBar = LinearLayout(context)
    private val closeButton = AppCompatImageButton(context) //For Close button
    private val cameraFlashButton = AppCompatImageButton(context) //Camera Flash Button
    private val cameraCaptureButton = AppCompatImageButton(context) //Camera Capture Button
    private val switchCameraButton = AppCompatImageButton(context) //Camera Switch Button
    private val torchButton = AppCompatImageButton(context) //Torch Button
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var cameraSelector = CameraSelector.LENS_FACING_BACK

    private var flashMode : CameraFlash  = CameraFlash.AUTO

    private val flashOverlay = View(context) //Flash Effect on Capture Button
    private var torchMode : TorchMode = TorchMode.OFF //Default Torch Button Value

    private val SUCCESS_TAG = "SUCCESS"
    private val ERROR_TAG = "ERROR"
    private val CLOSE_TAG = "CLOSE"
    private val CAMERA_STARTED_TAG = "CAMERA_STARTED"
    private val CAPTURE_PHOTO_TAG = "CAPTURE_PHOTO"

    private val callbackMethodsName = mapOf<String,String>(
        "SUCCESS" to "onSuccessCallback",
        "ERROR" to "onErrorCallback",
        "CLOSE" to "onCloseCallback",
        "CAMERA_STARTED" to "onCameraStartedCallback",
        "CAPTURE_PHOTO" to "onPhotoCapturedCallback",
    )

    /** Photo Capture Config Section **/
    private var photoCaptureConfig: PhotoCaptureConfigMetadata? = null
    private var isCapturingPhoto = false
    private var imageCapture: ImageCapture? = null

    /** End Photo Capture Config Section **/


    companion object {
        private val CLASS_TAG_NAME = "*InstantpayCameraView"
    }

    init {

        //Add Main Layout View
        mainLayoutView()

        //Add Top Overlay Bar
        topOverlayLayoutView()

        //Add Bottom Overlay Bar
        bottomOverlayLayoutView()

        // Overlay that will flash when capture the photo
        flashOverlay.setBackgroundColor(Color.WHITE)
        flashOverlay.alpha = 0f
        flashOverlay.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        addView(flashOverlay)
        flashOverlay.bringToFront()

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
     * Main Layout Setup
     */
    private fun mainLayoutView(){

        textureView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        //Add Main TextureView to show camera view
        addView(textureView)
    }

    /**
     * Top Overlay Bar Layout Setup
     */
    private fun topOverlayLayoutView(){

        topOverlayBar.setBackgroundColor(Color.parseColor("#90000000"))
        topOverlayBar.orientation = LinearLayout.HORIZONTAL
        topOverlayBar.gravity = Gravity.CENTER_VERTICAL

        val topParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            100
        )

        topParams.gravity = Gravity.TOP

        addView(topOverlayBar,topParams)

        //Added Close Button Layout View on Left Side
        closeButtonLayoutView()

        //Added Spacer Between Left and Right Side Container
        spaceBetweenOnTopOverlayLayout()

        //Add Right Side Container
        rightSideContainerTopOverlayLayout()
    }

    /**
     * Space Between Layout on Top Overlay Layout
     */
    private fun spaceBetweenOnTopOverlayLayout(){
        val spaceParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        spaceParams.weight = 1f

        topOverlayBar.addView(topOverlaySpaceBetween, spaceParams)
    }

    /**
     * Right side container item for Top Overlay Layout
     */
    private fun rightSideContainerTopOverlayLayout(){

        rightSideContainerOfTopOverlay.orientation = LinearLayout.HORIZONTAL

        rightSideContainerOfTopOverlay.setPadding(10,0,20,0)

        topOverlayBar.addView(rightSideContainerOfTopOverlay)
    }

    /**
     * Bottom Overlay Bar Layout Setup
     */
    private fun bottomOverlayLayoutView(){

        bottomOverlayBar.setBackgroundColor(Color.parseColor("#90000000"))
        bottomOverlayBar.gravity = Gravity.CENTER
        bottomOverlayBar.orientation = LinearLayout.HORIZONTAL

        val bottomParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            250
        )

        bottomParams.gravity = Gravity.BOTTOM
        addView(bottomOverlayBar, bottomParams)
    }

    /**
     * Button Layout View Setup
     */
    private fun closeButtonLayoutView(){

        closeButton.setImageResource(
            R.drawable.outline_cancel_24
        )

        //closeButton.text = "Close"

        //closeButton.cornerRadius = 100

        //closeButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#66000000"))

        closeButton.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            setMargins(20, 0, 0, 0)
        }

        closeButton.setBackgroundColor(Color.TRANSPARENT)

        topOverlayBar.addView(closeButton)

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
     * Camera Flash Button Layout View Setup
     */
    private fun cameraFlashButtonLayoutView(){

        if (photoCaptureConfig?.flash == CameraFlash.ON){
            cameraFlashButton.setImageResource(R.drawable.flash_on)
        }
        else if(photoCaptureConfig?.flash == CameraFlash.OFF){
            cameraFlashButton.setImageResource(R.drawable.flash_off)
        }
        else{
            cameraFlashButton.setImageResource(R.drawable.flash_auto)
        }

        /*cameraFlashButton.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            setMargins(0, 0, 0, 0)
        }*/

        cameraFlashButton.setBackgroundColor(Color.TRANSPARENT)

        rightSideContainerOfTopOverlay.addView(cameraFlashButton)

        cameraFlashButton.setOnClickListener {

            // Press animation
            cameraFlashButton.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(80)
                .withEndAction {

                    toggleFlash()

                    // Restore button
                    cameraFlashButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                        .start()
                }
                .start()
        }
    }

    /**
     * Toggle Camera Flash AUTO -> ON -> OFF -> AUTO
     */
    private fun toggleFlash(){

        flashMode = when(flashMode){
            CameraFlash.AUTO -> CameraFlash.ON
            CameraFlash.ON -> CameraFlash.OFF
            CameraFlash.OFF -> CameraFlash.AUTO
        }

        when (flashMode) {
            CameraFlash.ON -> imageCapture?.flashMode = ImageCapture.FLASH_MODE_ON
            CameraFlash.OFF -> imageCapture?.flashMode = ImageCapture.FLASH_MODE_OFF
            CameraFlash.AUTO -> imageCapture?.flashMode = ImageCapture.FLASH_MODE_AUTO
        }

        updateFlashIcon()
    }

    /**
     * Update Flash Icon while Change the Flash Mode
     */
    private fun updateFlashIcon() {
        val icon = when (flashMode) {
            CameraFlash.ON -> R.drawable.flash_on
            CameraFlash.OFF -> R.drawable.flash_off
            CameraFlash.AUTO -> R.drawable.flash_auto
        }

        cameraFlashButton.setImageResource(icon)
    }

    /**
     * Camera Switch Button Layout View Setup
     */
    private fun cameraSwitchButtonLayoutView(){
        switchCameraButton.setImageResource(R.drawable.outline_cameraswitch_24)

        switchCameraButton.layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.END
            setMargins(32, 32, 32, 60)
        }

        switchCameraButton.setBackgroundColor(Color.TRANSPARENT)

        addView(switchCameraButton)

        switchCameraButton.setOnClickListener {
            switchCamera()
        }
    }

    /**
     * Change Camera Facing Front Camera / Back Camera
     */
    private fun switchCamera(){

        cameraSelector =
            if(cameraSelector == CameraSelector.LENS_FACING_BACK)
                CameraSelector.LENS_FACING_FRONT
            else
                CameraSelector.LENS_FACING_BACK

        restartCamera()
    }

    /**
     * Camera Capture Button Layout View Setup
     */
    private fun cameraCaptureButtonLayoutView(){

        cameraCaptureButton.setImageResource(R.drawable.capture_button)

        cameraCaptureButton.layoutParams = LayoutParams(
            180,
            180
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            bottomMargin = 50
        }

        cameraCaptureButton.setBackgroundColor(Color.TRANSPARENT)

        cameraCaptureButton.setOnClickListener {

            if (isCapturingPhoto) return@setOnClickListener

            isCapturingPhoto = true

            // Press animation
            cameraCaptureButton.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(80)
                .withEndAction {

                    showCaptureFlash()

                    if(photoCaptureConfig !=null && photoCaptureConfig?.captureSound == true){

                    }
                    else{
                        val shutter = MediaActionSound()
                        shutter.play(MediaActionSound.SHUTTER_CLICK)
                    }

                    capturePhoto()

                    // Restore button
                    cameraCaptureButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                        .start()
                }
                .start()
        }

        addView(cameraCaptureButton)
    }

    /**
     * Add a white flash overlay when capturing the image
     */
    private fun showCaptureFlash() {

        flashOverlay.bringToFront()
        flashOverlay.alpha = 0f
        flashOverlay.visibility = View.VISIBLE

        flashOverlay.animate()
            .alpha(0.9f)
            .setDuration(80)
            .withEndAction {
                flashOverlay.animate()
                    .alpha(0f)
                    .setDuration(180)
                    .start()
            }
            .start()
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

                val useCases = mutableListOf<UseCase>()
                useCases.add(preview)

                val currentCameraLens = CameraSelector.Builder()
                    .requireLensFacing(cameraSelector)
                    .build()

                //Configuration Set to Capture Photo
                if(photoCaptureConfig!=null){

                    CommanHelper.logPrint(CLASS_TAG_NAME,"photoCaptureConfig is set to do capture photo")

                    val jpegQuality = when(photoCaptureConfig?.quality) {
                        CaptureQuality.LOW -> 60
                        CaptureQuality.MEDIUM -> 80
                        CaptureQuality.HIGH -> 95
                        else -> 80
                    }

                    val captureMode =
                        if (photoCaptureConfig?.quality == CaptureQuality.LOW)
                            ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
                        else
                            ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY

                    var resolutionSelector: ResolutionSelector? = null
                    if(photoCaptureConfig?.maxWidth !=null
                        && photoCaptureConfig?.maxWidth!! > 0
                        && photoCaptureConfig?.maxHeight !=null
                        && photoCaptureConfig?.maxHeight!! > 0
                    ){
                        resolutionSelector = ResolutionSelector.Builder()
                            .setResolutionStrategy(
                                ResolutionStrategy(
                                    Size(photoCaptureConfig?.maxWidth!!, photoCaptureConfig?.maxHeight!!),
                                    ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER //ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                                )
                            )
                            .build()
                    }

                    val imageCaptureBuilder = ImageCapture.Builder()
                        .setCaptureMode(captureMode)
                        .setJpegQuality(jpegQuality)

                    //Set Capture Image Resolution
                    if(resolutionSelector!=null){
                        imageCaptureBuilder.setResolutionSelector(resolutionSelector)
                    }

                    //Set Flash operation
                    when(flashMode){
                        CameraFlash.AUTO -> imageCaptureBuilder.setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                        CameraFlash.ON -> imageCaptureBuilder.setFlashMode(ImageCapture.FLASH_MODE_ON)
                        CameraFlash.OFF -> imageCaptureBuilder.setFlashMode(ImageCapture.FLASH_MODE_OFF)
                    }

                    //Flash Off while Using Front Camera
                    if(cameraSelector == CameraSelector.LENS_FACING_FRONT){
                        imageCaptureBuilder.setFlashMode(ImageCapture.FLASH_MODE_OFF)
                    }

                    imageCapture = imageCaptureBuilder.build()

                    useCases.add(imageCapture!!)
                }

                camera = cameraProvider?.bindToLifecycle(
                    activity,
                    currentCameraLens,
                    *useCases.toTypedArray()
                )

                if(torchMode == TorchMode.ON){
                    camera?.cameraControl?.enableTorch(true)
                }
                else{
                    camera?.cameraControl?.enableTorch(false)
                }

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
     * Restart Camera
     */
    private fun restartCamera(){
        cameraProvider?.unbindAll()
        startCamera()
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
     * Set Camera Facing : FRONT or BACK
     */
    fun setCameraFacing(cameraFacing : CameraFacing){
        cameraSelector =
            if(cameraFacing == CameraFacing.FRONT)
                CameraSelector.LENS_FACING_FRONT
            else
                CameraSelector.LENS_FACING_BACK
    }

    /**
     * Set Configuration of Capture Photo from Camera
     */
    fun setPhotoCaptureConfig(config: PhotoCaptureConfigMetadata?){
        photoCaptureConfig = config
        CommanHelper.logPrint(CLASS_TAG_NAME,"photoCaptureConfig : ${photoCaptureConfig}")

        flashMode = photoCaptureConfig?.flash!!

        //Add Camera Flash Button Layout View
        cameraFlashButtonLayoutView()

        //Add Capture Button Layout
        cameraCaptureButtonLayoutView()

        //Add Camera Switch Button Layout
        cameraSwitchButtonLayoutView()
    }

    /**
     * Capture Photo from Camera
     */
    private fun capturePhoto(){
        val imageCapture = imageCapture ?: return

        if (photoCaptureConfig!=null && photoCaptureConfig?.saveToGallery == true){
            val name = "IMG_${System.currentTimeMillis()}"

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/InstantpayCamera")
                }
            }

            val outputOptions = ImageCapture.OutputFileOptions
                .Builder(
                    context.contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                .build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val savedUri = outputFileResults.savedUri ?: return

                        val cursor = context.contentResolver.query(
                            savedUri,
                            null,
                            null,
                            null,
                            null
                        )

                        var size: Long = 0
                        var width = 0
                        var height = 0
                        var mimeType = ""

                        cursor?.use {

                            val sizeIndex = it.getColumnIndex(MediaStore.Images.Media.SIZE)
                            val widthIndex = it.getColumnIndex(MediaStore.Images.Media.WIDTH)
                            val heightIndex = it.getColumnIndex(MediaStore.Images.Media.HEIGHT)
                            val mimeIndex = it.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)

                            if (it.moveToFirst()) {
                                size = it.getLong(sizeIndex)
                                width = it.getInt(widthIndex)
                                height = it.getInt(heightIndex)
                                mimeType = it.getString(mimeIndex)
                            }
                        }

                        //Raw Image to Base64
                        val inputStream = context.contentResolver.openInputStream(savedUri)
                        val bytes = inputStream?.readBytes()
                        val base64Image = Base64.encodeToString(bytes, Base64.NO_WRAP)

                        CommanHelper.logPrint(CLASS_TAG_NAME, "Saved URI: ${savedUri}")

                        var base64ImageCompress = ""
                        if(inputStream!=null){
                            //val bitmap = BitmapFactory.decodeStream(inputStream)
                            val bitmap = context.contentResolver
                                .openInputStream(savedUri)
                                ?.use { stream ->
                                    BitmapFactory.decodeStream(stream)
                                }

                            CommanHelper.logPrint(CLASS_TAG_NAME, "width : ${bitmap?.width} , height : ${bitmap?.height}")

                            val maxWidth = photoCaptureConfig?.maxWidth ?: bitmap?.width
                            val maxHeight = photoCaptureConfig?.maxHeight ?: bitmap?.height

                            val resized = CommanHelper.resizeBitmap(bitmap as Bitmap, maxWidth!!, maxHeight!!)
                            val stream = ByteArrayOutputStream()

                            val jpegQuality = when(photoCaptureConfig?.quality) {
                                CaptureQuality.LOW -> 60
                                CaptureQuality.MEDIUM -> 80
                                CaptureQuality.HIGH -> 95
                                else -> 80
                            }

                            resized.compress(
                                Bitmap.CompressFormat.JPEG,
                                jpegQuality,
                                stream
                            )

                            base64ImageCompress = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
                        }

                        val output = Arguments.createMap().apply {
                            putString("name",name)
                            putString("uri",savedUri.toString())
                            putString("path", savedUri.path)
                            putDouble("size", size.toDouble())
                            putDouble("imageWidth",width.toDouble())
                            putDouble("imageHeight",height.toDouble())
                            putString("mimeType", mimeType)
                        }

                        if(photoCaptureConfig?.base64ImageOutput == true){
                            output.putString("base64Image", base64Image)
                        }

                        if(photoCaptureConfig?.compressBase64ImageOutput == true){
                            output.putString("base64ImageCompress", base64ImageCompress)
                        }

                        onSendReactNativeEvent(CAPTURE_PHOTO_TAG, output)

                        isCapturingPhoto = false
                    }

                    override fun onError(exception: ImageCaptureException) {
                        isCapturingPhoto = false

                        val output = Arguments.createMap()
                        output.putString("code","FAILED_CAPTURE")
                        output.putString("errorMessage", exception.message)
                        output.putString("errorCause", exception.cause.toString())
                        output.putString("recoverySuggestion", exception.stackTrace.toString())

                        onSendReactNativeEvent(ERROR_TAG, output)
                    }
                }
            )
        }
        else{ //Capture Photo without saving to gallery

            val photoFile = File(
                context.cacheDir,
                "IMG_${System.currentTimeMillis()}.jpg"
            )

            val outputOptions = ImageCapture
                .OutputFileOptions
                .Builder(photoFile)
                .build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object: ImageCapture.OnImageSavedCallback{
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                        val uri = Uri.fromFile(photoFile)

                        val output = Arguments.createMap().apply {
                            putString("uri", uri.toString())
                            putString("path", photoFile.absolutePath)
                        }

                        onSendReactNativeEvent(CAPTURE_PHOTO_TAG, output)

                        isCapturingPhoto = false
                    }

                    override fun onError(exception: ImageCaptureException) {
                        isCapturingPhoto = false

                        val output = Arguments.createMap()
                        output.putString("code","FAILED_CAPTURE")
                        output.putString("errorMessage", exception.message)
                        output.putString("errorCause", exception.cause.toString())
                        output.putString("recoverySuggestion", exception.stackTrace.toString())

                        onSendReactNativeEvent(ERROR_TAG, output)
                    }
                }
            )
        }
    }

    /**
     * Represents the torch (flashlight) mode for the camera. ON/OFF
     */
    fun setTorchMode(mode: TorchMode){
        torchMode = mode

        //Add Torch Layout View
        torchButtonLayoutView()
    }

    /**
     * Torch Button Layout View Setup
     */
    private fun torchButtonLayoutView(){

        if(torchMode == TorchMode.ON){
            torchButton.setImageResource(R.drawable.ic_torch_on)
            //torchButton.setPadding(0,10,0,0)
        }
        else{
            torchButton.setImageResource(R.drawable.ic_torch_off)
        }

        torchButton.setBackgroundColor(Color.TRANSPARENT)

        rightSideContainerOfTopOverlay.addView(torchButton)

        torchButton.setOnClickListener {

            torchButton.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(80)
                .withEndAction {

                    toggleTorchMode()

                    torchButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                        .start()
                }
                .start()

        }
    }

    /**
     * Switch torch mode ON -> OFF -> ON
     */
    private fun toggleTorchMode(){

        torchMode = when(torchMode){
            TorchMode.ON -> TorchMode.OFF
            TorchMode.OFF -> TorchMode.ON
        }

        when (torchMode) {
            TorchMode.ON -> camera?.cameraControl?.enableTorch(true)
            TorchMode.OFF -> camera?.cameraControl?.enableTorch(false)
        }

        val icon = when (torchMode) {
            TorchMode.ON -> R.drawable.ic_torch_on
            TorchMode.OFF -> R.drawable.ic_torch_off
        }

        torchButton.setImageResource(icon)
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

        UIManagerHelper.getEventDispatcherForReactTag(context, viewId)
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
