//
//  IpayCameraView.swift
//  InstantpayCamera
//
//  Created by Dhananjay kumar on 18/03/26.
//

import Foundation
import UIKit
import AVFoundation

@objc(IpayCameraView)
public class IpayCameraView: UIView {
    
    private let CLASS_TAG = "*IpayCameraView"
    
    /** Top Overlay Layout **/
    private let topOverlayBar = UIView()
    private let topOverlayBarHeight: CGFloat = 50
    private let leftSideContainerOfTopOverlay = UIView()
    private let centerContainerOfTopOverlay = UIView()
    private let rightSideContainerOfTopOverlay = UIView()
    private var topBarStackView: UIStackView? = nil
    /** End Top Overlay Layout **/
    
    /** Bottom Overlay Layout **/
    private let bottomOverlayBar = UIView()
    private let bottomOverlayBarHeight: CGFloat = 80
    private var bottomOverlayEnabled: Bool = false
    private let leftSideContainerOfBottomOverlay = UIView()
    private let centerContainerOfBottomOverlay = UIView()
    private let rightSideContainerOfBottomOverlay = UIView()
    private var bottomBarStackView: UIStackView? = nil
    /**End Bottom Overlay Layout **/
    
    /** Body Overlay Layout **/
    private let bodyContainer: UIView = UIView()
    /** End Body Overlay Layout **/
    
    private let closeButton = UIButton(type: .system)
    private let torchButton = UIButton(type: .system)
    
    @objc public var onSendReactNativeEvent: ((String, [String: Any]) -> Void)?
    
    /*** Camera Setup Config **/
    private let cameraSession = AVCaptureSession()
    private var cameraPreviewLayer: AVCaptureVideoPreviewLayer?
    private var cameraCurrentInput: AVCaptureDeviceInput?
    /***End  Camera Setup Config **/
    
    /** Photo Capture Config Section **/
    private var photoCaptureConfig: PhotoCaptureConfigMetadata = PhotoCaptureConfigMetadata()
    private var cameraFlashButton = UIButton(type: .system)
    private var cameraCaptureButton = UIButton(type: .system)
    /**End Photo Capture Config Section **/
    
    
    ///View created (memory allocated), UI not on screen yet
    override init(frame: CGRect){
        super.init(frame: frame)
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "IpayCameraView is init")
        
        
    }
    
    ///This is called when the view is created from: Storyboard , Interface Builder, XIB, Or some system-level decoding
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "IpayCameraView is required init")

    }
    
    ///View destroyed (no references left, memory freed)
    deinit {
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "IpayCameraView destroyed")
    }
    
    ///View added to screen (safe to start camera / UI work)
    public override func didMoveToSuperview() {
        super.didMoveToSuperview()
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "IpayCameraView is ready : didMoveToSuperview")
        if superview != nil {
            setupScreens()
        }
       
    }
    
    ///View about to be removed from screen (stop camera here)
    public override func willMove(toSuperview newSuperview: UIView?) {
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "willMove is called")
        if newSuperview == nil {
            stopCamera()   // cleanup
        }
    }
    
    
    /// This Method is useful Whenever your view needs to layout (position/resize) its children
    ///Whenever my view size changes → fix all child layouts
    public override func layoutSubviews() {
        super.layoutSubviews()
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "IpayCameraView called layoutSubviews")
        
        //let overlayHeight = bounds.height * 0.25  // 👈 adjust this
        
        //Added TopOverlayLayout Dimensions
        topOverlayBar.frame = CGRect(
            x: 0,
            y: 0,
            width: bounds.width,
            height: topOverlayBarHeight + safeAreaInsets.top
        )
        
        //Added Top Bar Stack View Dimension
        topBarStackView?.frame = CGRect(
            x: 0,
            y: safeAreaInsets.top,
            width: bounds.width,
            height: topOverlayBarHeight,
        )
        
        //Added Close button View Dimension
        closeButton.frame = CGRect(
            x: 0,
            y: 0,
            width: 50,
            height: 50,
        )
        
        if bottomOverlayEnabled {
                
            //Added Body Container Dimensions
            bodyContainer.frame = CGRect(
                x: 0,
                y: topOverlayBar.bounds.height + safeAreaInsets.top,
                width: bounds.width,
                height: bounds.height - topOverlayBar.bounds.height - bottomOverlayBarHeight
            )
        }
        else{
            
            //Added Body Container Dimensions
            bodyContainer.frame = CGRect(
                x: 0,
                y: topOverlayBar.bounds.height,
                width: bounds.width,
                height: bounds.height - topOverlayBar.bounds.height
            )
        }
        
        //Added BottomOverlayLayout Dimensions
        bottomOverlayBar.frame = CGRect(
            x: 0,
            y: bounds.height - bottomOverlayBarHeight - safeAreaInsets.bottom,
            width: bounds.width,
            height: bottomOverlayBarHeight + safeAreaInsets.bottom,
        )
        
        //Added Bottom Bar Stack View Dimension
        bottomBarStackView?.frame = CGRect(
            x: 0,
            y: safeAreaInsets.bottom,
            width: bounds.width,
            height: bottomOverlayBarHeight,
        )
        
        // camera preview only inside bodyContainer
        cameraPreviewLayer?.frame = bodyContainer.bounds
        
        var currentXForTopRightLayoutItems = 50
        
        //Added Torch Button Dimensions
        if torchMode != "NOT_SET" {
            let size: CGFloat = 50
            let padding: CGFloat = 8
            torchButton.frame = CGRect(
                x: rightSideContainerOfTopOverlay.bounds.width - size - padding,
                y: 0,
                width: 50,
                height: 50,
            )
            
            currentXForTopRightLayoutItems += 25
        }
        
        /*if photoCaptureConfig.photoConfigurationStatus {
            //Added Camera Flash Button Dimension
            cameraFlashButton.frame = CGRect(
                x: Int(rightSideContainerOfTopOverlay.bounds.width) - currentXForTopRightLayoutItems,
                y: 0,
                width: 50,
                height: 50
            )
        }*/
        
        //Added Capture Buttom Dimension
        cameraCaptureButton.frame.size = CGSize(width: 70, height: 70)

        //Center Capture Buttom
        cameraCaptureButton.center = CGPoint(
            x: centerContainerOfBottomOverlay.bounds.midX,
            y: centerContainerOfBottomOverlay.bounds.midY
        )
        
        
    }
    
    ///Main Setup Screen Manage Layout Patterns
    private func setupScreens(){
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "Starting setupScreens")
        
        //Check Camera Permission
        checkCameraPermission()
        
        //Add Top Overlay Bar
        topOverlayLayoutView()
        
        //Add Main or Center Layout View
        mainLayoutView()
        
        //Added Torch Button Action
        torchButton.addAction(UIAction {[weak self] _ in
            self?.onTorchPressed()
        }, for: .touchUpInside)
    }
    
    /// Top Overlay Bar Layout Setup
    private func topOverlayLayoutView(){
        
        let overlayColor = UIColor.black
        
        topOverlayBar.backgroundColor = overlayColor
        addSubview(topOverlayBar)
        
        topBarStackView = UIStackView(arrangedSubviews: [leftSideContainerOfTopOverlay,centerContainerOfTopOverlay,rightSideContainerOfTopOverlay])
        
        topBarStackView!.axis = .horizontal
        topBarStackView!.distribution = .fillEqually
        topBarStackView!.layer.borderColor = UIColor.yellow.cgColor
        topBarStackView!.layer.borderWidth = 5.0
        
        topOverlayBar.addSubview(topBarStackView!)
        
        //Add Left Side Container
        leftSideContainerTopOverlayLayout()
        
        //Add Center Container
        centerConatinerTopOverlayLayout()
        
        //Add Right Side Container
        rightSideContainerTopOverlayLayout()
    }
    
    ///Left side container item for Top Overlay Layout
    private func leftSideContainerTopOverlayLayout(){
        
        leftSideContainerOfTopOverlay.layer.borderColor = UIColor.green.cgColor
        leftSideContainerOfTopOverlay.layer.borderWidth = 2.0
        
        //Added Close Button Layout
        closeButtonLayoutView()
    }
    
    ///Center container item for Top Overlay Layout
    private func centerConatinerTopOverlayLayout(){
        centerContainerOfTopOverlay.layer.borderColor = UIColor.red.cgColor
        centerContainerOfTopOverlay.layer.borderWidth = 2.0
    }
    
    ///Right side container item for Top Overlay Layout
    private func rightSideContainerTopOverlayLayout(){
        rightSideContainerOfTopOverlay.layer.borderColor = UIColor.blue.cgColor
        rightSideContainerOfTopOverlay.layer.borderWidth = 2.0
    }
    
    ///Bottom Overlay Bar Layout Setup
    private func bottomOverlayLayoutView(){
        
        bottomOverlayEnabled = true
        
        let overlayColor = UIColor.black
        
        bottomOverlayBar.backgroundColor = overlayColor
        addSubview(bottomOverlayBar)
        
        bottomBarStackView = UIStackView(arrangedSubviews: [leftSideContainerOfBottomOverlay, centerContainerOfBottomOverlay, rightSideContainerOfBottomOverlay])
        
        bottomBarStackView!.axis = .horizontal
        bottomBarStackView!.distribution = .fillEqually
        
        bottomOverlayBar.addSubview(bottomBarStackView!)
        
        //Add Left Side Container
        leftSideContainerBottomOverlay()
        
        //Add Center Container
        centerConatinerBottomOverlay()
        
        //Add Right Side Container
        rightSideContainerBottomOverlay()
        
    }
    
    ///Left side container item for Bottom Overlay Layout
    private func leftSideContainerBottomOverlay(){
        
        
    }
    
    ///Center container item for Bottom Overlay Layout
    private func centerConatinerBottomOverlay(){
        
    }
    
    ///Right side container item for Bottom Overlay Layout
    private func rightSideContainerBottomOverlay(){
        
        
    }
    
    ///Close Button Layout View Setup
    private func closeButtonLayoutView(){
        
        let config = UIImage.SymbolConfiguration(pointSize: 20, weight: .medium)
        let image = UIImage(systemName: "xmark.circle", withConfiguration: config)!
        closeButton.setImage(image, for: .normal)
        
        closeButton.imageView?.contentMode = .scaleAspectFit
        
        closeButton.tintColor = .white
        closeButton.backgroundColor = UIColor.clear
        
        //Increase touch area (better UX)
        //closeButton.contentEdgeInsets = UIEdgeInsets(top: 50, left: 10, bottom: 50, right: 50)
        
        closeButton.backgroundColor = UIColor.black.withAlphaComponent(0.3)
        closeButton.layer.cornerRadius = closeButton.frame.width / 2
        closeButton.clipsToBounds = true
        
        leftSideContainerOfTopOverlay.addSubview(closeButton)
        
        //closeButton.addTarget(self, action: #selector(onClosePressed), for: .touchUpInside)
        closeButton.addAction(UIAction { [weak self] _ in
                    self?.onClosePressed()
                }, for: .touchUpInside)
    }
    
    ///Close Button Action Method
    private func onClosePressed(){
        
        //Add haptic feedback
        let impact = UIImpactFeedbackGenerator(style: .light)
        impact.impactOccurred()
        
        animateTap(closeButton)
        
        onSendRNEvent(eventName: "CLOSE", eventData: ["message": "Camera close button is fired!"])
        
        // Remove from parent view
        //self.removeFromSuperview()
        
        // Stop Camera and cameraSession data
        //stopCamera()
    }
    
    ///This Layout Handle Center or Body Layout of Screen
    private func mainLayoutView(){
        
        bodyContainer.backgroundColor = .gray
        bodyContainer.clipsToBounds = true
        
        addSubview(bodyContainer)
    }
    
    ///Button Tap animation Method
    ///Tap → button shrinks
    ///Release → bounce back
    private func animateTap(_ view: UIView) {
        UIView.animate(withDuration: 0.1,
                       animations: {
            view.transform = CGAffineTransform(scaleX: 0.85, y: 0.85)
        }) { _ in
            UIView.animate(withDuration: 0.2,
                           delay: 0,
                           usingSpringWithDamping: 0.5,
                           initialSpringVelocity: 3,
                           options: [.allowUserInteraction],
                           animations: {
                view.transform = .identity
            }, completion: nil)
        }
    }
    
    
    ///Button Ripple effect Animation Method
    private func addRippleEffect(to view: UIView) {
        let ripple = UIView(frame: view.bounds)
        ripple.backgroundColor = UIColor.white.withAlphaComponent(0.2)
        ripple.layer.cornerRadius = view.bounds.width / 2
        ripple.transform = CGAffineTransform(scaleX: 0.1, y: 0.1)
        ripple.alpha = 1

        view.addSubview(ripple)

        UIView.animate(withDuration: 0.4, animations: {
            ripple.transform = CGAffineTransform(scaleX: 2.5, y: 2.5)
            ripple.alpha = 0
        }) { _ in
            ripple.removeFromSuperview()
        }
    }
    
    ///This method check camera permission is granted or not
    private func checkCameraPermission(){
        
        let status = AVCaptureDevice.authorizationStatus(for: .video)
        
        switch status {
            case .authorized:
                setupCamera()
                startCameraSession()
            
            case .notDetermined:
                // First time asking; show the system prompt
                AVCaptureDevice.requestAccess(for: .video) { granted in
                    if granted {
                        self.setupCamera()
                        self.startCameraSession()
                    }
                }

            case .denied, .restricted:
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                    self.onSendRNEvent(
                        eventName: "ERRORED",
                        eventData: [
                            "code": "CAMERA_PERMISSION_NOT_GRANTED",
                            "errorMessage": "Camera permission not granted",
                            "errorCause": "Camera permission not granted",
                            "recoverySuggestion": ""
                        ])
                }

            @unknown default:
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                    self.onSendRNEvent(
                        eventName: "ERRORED",
                        eventData: [
                            "code": "CAMERA_PERMISSION_NOT_GRANTED",
                            "errorMessage": "Camera permission not granted",
                            "errorCause": "Camera permission not granted",
                            "recoverySuggestion": ""
                        ])
                }
        }
    }
    
    ///Start Camera and set to view
    private func setupCamera(){
        
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "Camera starting...")
        
        ///A preset value that indicates the quality level or bit rate of the output.
        cameraSession.sessionPreset = .high
        
        // Preview layer (only once)
        if cameraPreviewLayer == nil {
            ///create preview layer
            let layer = AVCaptureVideoPreviewLayer(session: cameraSession)
            layer.videoGravity = .resizeAspectFill
            
            ///IMPORTANT: add camera preview to bodyContainer
            bodyContainer.layer.addSublayer(layer)
            
            cameraPreviewLayer = layer
        }
        
        cameraSession.beginConfiguration()
        
        // remove old input
        if let cameraCurrentInput = cameraCurrentInput {
            cameraSession.removeInput(cameraCurrentInput)
        }
        
        // get device
        guard let cameraDevice = AVCaptureDevice.default(
            .builtInWideAngleCamera,
            for: .video,
            position: cameraFacing == "front" ? .front : .back),
              let input = try? AVCaptureDeviceInput(device: cameraDevice),
              cameraSession.canAddInput(input) else {
            cameraSession.commitConfiguration()
            return
        }
        
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "Selected Camera: \(cameraFacing)")
        
        cameraSession.addInput(input)
        cameraCurrentInput = input
        
        cameraSession.commitConfiguration()
    }
    
    ///Start Camera Session
    private func startCameraSession() {
        DispatchQueue.global(qos: .userInitiated).async {
            if !self.cameraSession.isRunning {
                self.cameraSession.startRunning()
                self.onSendRNEvent(eventName: "CAMERA_STARTED", eventData: ["status": "CAMERA_OPENED"])
                
                DispatchQueue.main.async {
                    self.applyTorchHardware()   // 👈 APPLY AGAIN
                }
            }
        }
    }
    
    ///Stop Camera and clear sessions
    private func stopCamera(){
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log:"stopCamera method is fired.")
        
        if cameraSession.isRunning {
            cameraSession.stopRunning()
        }
        
        cameraSession.removeInput(cameraCurrentInput!)
    }
    
    ///Switch Camera Facing Front Camera  -> Back Camera -> Front Camera
    private func switchCamera() {
        setupCamera()
    }
    
    ///Set Camera Facing Configuration FRONT, BACK
    @objc var cameraFacing: String = "back" {
        didSet {
            if oldValue != cameraFacing {
                IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: cameraFacing)
                guard cameraSession.isRunning else { return }
                switchCamera()
            }
        }
    }
    
    ///Set Torch Mode Configuration ON, OFF, NOT_SET
    @objc var torchMode: String = "NOT_SET" {
        didSet {
            if oldValue != torchMode {
                IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "Torch Mode Configuration : \(torchMode)")
                
                if cameraFacing == "back"{
                    updateTorchIcon()
                    
                    torchButton.backgroundColor = UIColor.black.withAlphaComponent(0.4)
                    torchButton.layer.cornerRadius = 16
                    torchButton.clipsToBounds = true
                    
                    rightSideContainerOfTopOverlay.addSubview(torchButton)
                }
            }
        }
    }
    
    ///Update Torch Icon
    private func updateTorchIcon() {
        let imageName: String

        if #available(iOS 15.0, *) {
            imageName = torchMode == "ON" ? "flashlight.on.fill" : "flashlight.off.fill"
        } else {
            imageName = torchMode == "ON" ? "bolt.fill" : "bolt.slash.fill"
        }

        torchButton.setImage(UIImage(systemName: imageName), for: .normal)
        torchButton.tintColor = torchMode == "ON" ? UIColor.systemYellow : UIColor.white
    }
    
    ///Torch Button Actionable
    private func onTorchPressed(){
        
        //Add haptic feedback
        let impact = UIImpactFeedbackGenerator(style: .light)
        impact.impactOccurred()
        
        animateTap(torchButton)
        
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "current torchMode : \(torchMode)")
        
        torchMode = torchMode == "ON" ? "OFF" : "ON"
        
        applyTorchHardware()
        
        updateTorchIcon()
    }
    
    /// Torch On, Off functionality
    private func applyTorchHardware() {
        guard let device = cameraCurrentInput?.device,
              device.hasTorch else { return }

        do {
            try device.lockForConfiguration()
            device.torchMode = torchMode == "ON" ? .on : .off
            device.unlockForConfiguration()
        } catch {
            onSendRNEvent(eventName: "ERRORED", eventData: [
                "code": "UNKNOWN_TORCH_ERROR",
                "errorMessage": "An unknown torch control error occurred",
                "errorCause": error.localizedDescription,
                "recoverySuggestion": ""
            ])
        }
    }
    
    ///Set Configuration of Capture Photo from Camera
    @objc public func updatePhotoCaptureOptions(_ config: [String: Any]){
        
        if((config["enableConfig"]) != nil && (config["enableConfig"] as! Bool)){
            photoCaptureConfig.photoConfigurationStatus = true
            
            if((config["quality"]) != nil){
                photoCaptureConfig.quality = CaptureQuality(rawValue: (config["quality"] as! String))!
            }
            
            if(config["flash"] != nil){
                photoCaptureConfig.flash = CameraFlash(rawValue: (config["flash"] as! String))!
            }
            
            if(config["saveToGallery"] != nil){
                photoCaptureConfig.saveToGallery = (config["saveToGallery"] as! Bool)
            }
            
            if(config["maxWidth"] != nil){
                photoCaptureConfig.maxWidth = Int((config["maxWidth"] as! CGFloat))
            }
            
            if(config["maxHeight"] != nil){
                photoCaptureConfig.maxHeight = Int((config["maxHeight"] as! CGFloat))
            }
            
            if(config["base64ImageOutput"] != nil && (config["base64ImageOutput"] as! Bool)){
                photoCaptureConfig.base64ImageOutput = (config["base64ImageOutput"] as! Bool)
            }
            
            if(config["compressBase64ImageOutput"] != nil && (config["compressBase64ImageOutput"] as! Bool)){
                photoCaptureConfig.compressBase64ImageOutput = (config["compressBase64ImageOutput"] as! Bool)
            }
            
            if(config["captureSound"] != nil && (config["captureSound"] as! Bool)){
                photoCaptureConfig.captureSound = (config["captureSound"] as! Bool)
            }
            
            IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "photoCaptureConfig : \(photoCaptureConfig)")
            
            ///Add Flash icon
            cameraFlashButtonLayoutView()
            
            ///Add Bottom Overlay Bar
            bottomOverlayLayoutView()
            
            ///Add Capture Button Layout View
            cameraCaptureButtonLayoutView()
        }
        
    }
    
    /// Camera Flash Button Layout
    private func cameraFlashButtonLayoutView(){
        
        rightSideContainerOfTopOverlay.addSubview(cameraFlashButton)
        
        cameraFlashButton.tintColor = .white
        cameraFlashButton.backgroundColor = UIColor.black.withAlphaComponent(0.4)
        cameraFlashButton.layer.cornerRadius = 18
        cameraFlashButton.clipsToBounds = true
        
        // icon size behavior
        cameraFlashButton.imageView?.contentMode = .scaleAspectFit
        
        cameraFlashButton.addAction(UIAction { [weak self] _ in
           self?.onCameraFlashPressed()
        }, for: .touchUpInside)
        
        updateCameraFlashIcon()
    }
    
    ///Update Camera Flash Button Icon
    private func updateCameraFlashIcon() {
        let mode = photoCaptureConfig.flash

        let imageName: String
        switch mode {
        case .ON:
            imageName = "bolt.fill"
            cameraFlashButton.tintColor = .systemYellow

        case .OFF:
            imageName = "bolt.slash.fill"
            cameraFlashButton.tintColor = .white

        case .AUTO:
            imageName = "bolt.badge.a.fill"
            cameraFlashButton.tintColor = .white
        }

        cameraFlashButton.setImage(UIImage(systemName: imageName), for: .normal)
    }
    
    ///Camera Flash Actionable : AUTO -> ON -> OFF -> AUTO
    private func onCameraFlashPressed(){
        
        switch photoCaptureConfig.flash {
            case .OFF:
                photoCaptureConfig.flash = CameraFlash.AUTO
            case .ON:
                photoCaptureConfig.flash = CameraFlash.OFF
            case .AUTO:
                photoCaptureConfig.flash = CameraFlash.ON
        }
        
        updateCameraFlashIcon()
        
        /*guard let device = cameraCurrentInput?.device,
              device.hasTorch else { return}
        
        do {
            
            try device.lockForConfiguration()
            
            switch photoCaptureConfig.flash {
                case .ON:
                   device.torchMode = .on
                case .OFF:
                    device.torchMode = .off
                case .AUTO:
                    device.torchMode = .auto
            }
            
            device.unlockForConfiguration()
        }
        catch {
            onSendRNEvent(eventName: "ERRORED", eventData: [
                "code": "UNKNOWN_FLASH_ERROR",
                "errorMessage": "An unknown flash control error occurred",
                "errorCause": error.localizedDescription,
                "recoverySuggestion": ""
            ])
        }*/
        
    }
    
    ///Camera Capture Button Layout View Setup
    private func cameraCaptureButtonLayoutView(){
        
        cameraCaptureButton.backgroundColor = .white
        cameraCaptureButton.layer.cornerRadius = 35
        cameraCaptureButton.clipsToBounds = true

        // inner circle effect
        cameraCaptureButton.layer.borderWidth = 4
        cameraCaptureButton.layer.borderColor = UIColor.black.cgColor
        
        // Add outer ring effect
        cameraCaptureButton.layer.borderWidth = 8
        cameraCaptureButton.layer.borderColor = UIColor.black.withAlphaComponent(0.8).cgColor
        
        cameraCaptureButton.layer.shadowColor = UIColor.black.cgColor
        cameraCaptureButton.layer.shadowOpacity = 0.3
        cameraCaptureButton.layer.shadowRadius = 4
        
        centerContainerOfBottomOverlay.addSubview(cameraCaptureButton)
        
        cameraCaptureButton.addAction(UIAction {[weak self] _ in
            self?.capturePhoto()
        }, for: .touchUpInside)
    }
    
    ///Capture Photo from Camera
    private func capturePhoto(){
        
    }
    
    ///Set Configuration of OCR (Detect Text from image)
    @objc public func updateOcrConfiguration(_ config: [String: Any]){
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "ocrConfig : \(config)")
    }
    
    ///Helper Method Send Event to RN Side
    @objc private func onSendRNEvent(eventName:String, eventData:[String: Any]) -> Void{
        DispatchQueue.main.async{
            self.onSendReactNativeEvent?(eventName, eventData)
        }
    }
}

