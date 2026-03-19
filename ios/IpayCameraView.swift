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
    private let topOverlayBar = UIView()
    private let topOverlayBarHeight: CGFloat = 50
    private let leftSideContainerOfTopOverlay = UIView()
    private let centerContainerOfTopOverlay = UIView()
    private let rightSideContainerOfTopOverlay = UIView()
    private var topBarStackView: UIStackView? = nil
    private let bottomOverlayBar = UIView()
    private let bottomOverlayBarHeight: CGFloat = 80
    private let closeButton = UIButton(type: .system)
    private let bodyContainer: UIView = UIView()
    
    @objc public var onSendReactNativeEvent: ((String, [String: Any]) -> Void)?
    
    /*** Camera Setup Config **/
    private let cameraSession = AVCaptureSession()
    private var cameraPreviewLayer: AVCaptureVideoPreviewLayer?
    private var cameraCurrentInput: AVCaptureDeviceInput?
    /***End  Camera Setup Config **/
    
    
    ///This is called when the view is created programmatically
    override init(frame: CGRect){
        super.init(frame: frame)
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "IpayCameraView is init")
        setupScreens()
    }
    
    ///This is called when the view is created from: Storyboard , Interface Builder, XIB, Or some system-level decoding
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "IpayCameraView is required init")
        setupScreens()
    }
    
    deinit {
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "IpayCameraView destroyed")
    }
    
    public override func didMoveToSuperview() {
        super.didMoveToSuperview()
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: "IpayCameraView is ready : didMoveToSuperview")
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
            height: topOverlayBarHeight
        )
        
        //Added Top Bar Stack View Dimension
        topBarStackView?.frame = CGRect(
            x: 0,
            y: safeAreaInsets.top,
            width: bounds.width,
            height: topOverlayBarHeight
        )
        
        //Added Close button View Dimension
        closeButton.frame = CGRect(
            x: 0,
            y: 0,
            width: 50,
            height: 50,
        )
        
        //Added Body Container Dimensions
        bodyContainer.frame = CGRect(
            x: 0,
            y: topOverlayBarHeight,
            width: bounds.width,
            height: bounds.height - topOverlayBarHeight - bottomOverlayBarHeight
        )
        
        //Added BottomOverlayLayout Dimensions
        bottomOverlayBar.frame = CGRect(
            x: 0,
            y: bounds.height - bottomOverlayBarHeight,
            width: bounds.width,
            height: bottomOverlayBarHeight
        )
        
        // camera preview only inside bodyContainer
        cameraPreviewLayer?.frame = bodyContainer.bounds
        
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
        
        //Add Bottom Overlay Bar
        //bottomOverlayLayoutView()
    }
    
    /// Top Overlay Bar Layout Setup
    private func topOverlayLayoutView(){
        
        let overlayColor = UIColor.black
        
        topOverlayBar.backgroundColor = overlayColor
        addSubview(topOverlayBar)
        
        topBarStackView = UIStackView(arrangedSubviews: [leftSideContainerOfTopOverlay,centerContainerOfTopOverlay,rightSideContainerOfTopOverlay])
        
        topBarStackView!.axis = .horizontal
        topBarStackView!.distribution = .fillEqually
        
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
        
        //Added Close Button Layout
        closeButtonLayoutView()
    }
    
    ///Center container item for Top Overlay Layout
    private func centerConatinerTopOverlayLayout(){
        
    }
    
    ///Right side container item for Top Overlay Layout
    private func rightSideContainerTopOverlayLayout(){
        
    }
    
    ///Bottom Overlay Bar Layout Setup
    private func bottomOverlayLayoutView(){
        let overlayColor = UIColor.black.withAlphaComponent(0.9)
        
        bottomOverlayBar.backgroundColor = overlayColor
        addSubview(bottomOverlayBar)
        
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
        stopCamera()
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
                startCamera()
            
            case .notDetermined:
                // First time asking; show the system prompt
                AVCaptureDevice.requestAccess(for: .video) { granted in
                    if granted {
                        self.startCamera()
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
    
    ///Set Camera Facing Configuration FRONT, BACK
    @objc var cameraFacing: String = "back" {
        didSet {
            if oldValue != cameraFacing {
                //switchCamera()
                IpayCameraHelper.logPrint(classTag: CLASS_TAG, log: cameraFacing)
            }
        }
    }
    
    ///Start Camera and set to view
    private func startCamera(){
        
        ///A preset value that indicates the quality level or bit rate of the output.
        cameraSession.sessionPreset = .high
        
        ///create preview layer
        cameraPreviewLayer = AVCaptureVideoPreviewLayer(session: cameraSession)
        cameraPreviewLayer?.videoGravity = .resizeAspectFill
        
        ///IMPORTANT: add camera preview to bodyContainer
        bodyContainer.layer.addSublayer(cameraPreviewLayer!)
        
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
        
        cameraSession.addInput(input)
        cameraCurrentInput = input
        
        cameraSession.commitConfiguration()
        
        DispatchQueue.global(qos: .userInitiated).async {
            if !self.cameraSession.isRunning {
                self.cameraSession.startRunning()
            }
        }
    }
    
    ///Stop Camera
    private func stopCamera(){
        IpayCameraHelper.logPrint(classTag: CLASS_TAG, log:"stopCamera method is fired.")
        
        if cameraSession.isRunning {
            cameraSession.stopRunning()
        }
        
        // Remove inputs
        //for input in cameraSession.inputs {
        //    cameraSession.removeInput(input)
        //}

        // Remove outputs
        //for output in cameraSession.outputs {
        //    cameraSession.removeOutput(output)
        //}
    }
    
    ///Helper Method Send Event to RN Side
    @objc private func onSendRNEvent(eventName:String, eventData:[String: Any]) -> Void{
        onSendReactNativeEvent?(eventName, eventData)
    }
}

