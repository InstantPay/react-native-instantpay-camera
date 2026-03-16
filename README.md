# react-native-instantpay-camera

A React Native camera library enables developers to access and control a device’s camera (front/back) directly from a React Native application. It allows building features like photo capture, video recording, QR/barcode scanning, document scanning, face detection, and real-time image processing.

## Features


| Features      | Android | iOS   |
| :-----------  | :------ | :-----|
| Capture Photo |   ✅    |   ❌   |
| OCR           |   ✅    |   ❌   |  


## Installation


```sh
npm install react-native-instantpay-camera
```

## API

Available Options List:

```typescript
- cameraFacing : Represents the camera lens facing direction
    - Possible List : FRONT, BACK 
    - Default Value : BACK
- torchMode : Represents the torch (flashlight) mode for the camera.
    - Possible List : ON, OFF 
    - Default Value : OFF
- photoCaptureConfig : Photo Capture Configuration
    - quality : The quality of the captured photo.
        - Possible List : LOW , MEDIUM , HIGH
        - Default Value : MEDIUM
    - flash : The flash mode for capturing the photo.
        - Possible List : AUTO , ON , OFF
        - Default Value : AUTO
    - saveToGallery : Whether to save the captured photo to the gallery.
        - Boolean : TRUE/FALSE
        - Default Value : FALSE
    - maxWidth : The maximum width of the captured photo. (Eg: 890,1200 etc)
    - maxHeight : The maximum height of the captured photo. (Eg: 890,1200 etc)
    - base64ImageOutput : Whether to output the captured photo as a base64 string.
        - Boolean : TRUE/FALSE
        - Default Value : FALSE
    - compressBase64ImageOutput : Whether to compress the base64 image output.
        - Boolean : TRUE/FALSE
        - Default Value : FALSE
    - captureSound : Capture Sound enable/disable while taking photo
        - Boolean : TRUE/FALSE
        - Default Value : TRUE
- ocrConfig : OCR Configuration (Detect Text from Image)
    - language : The language to be used for OCR processing.
        - Possible List : EN , HI
        - Default Value : EN
    - detectAadhaar : Whether to enable Aadhaar card Number detection during OCR processing.
        - Boolean : TRUE/FALSE
        - Default Value : FALSE
    - detectPan : Whether to enable PAN card detection during OCR processing.
        - Boolean : TRUE/FALSE
        - Default Value : FALSE
        
```

Available Callback Methods List:

```typescript
- onCloseCallback : Event handler that is called when the camera view is closed, providing close event data.
- onErrorCallback : Event handler that is called when an error occurs, providing error event data.
- onSuccessCallback : Event handler that is called when an operation is successful, providing success event data.
- onCameraStartedCallback : Event handler that is called when the camera has started, providing camera started event data.
- onPhotoCapturedCallback : Event handler that is called when a photo has been captured, providing photo captured event data.
- onTextDetectedCallback : Event handler that is called when text has been detected in the captured image, providing detected text event data.

```

## Usage


```js
import { InstantpayCamera } from "react-native-instantpay-camera";

// ...

<InstantpayCamera
    cameraFacing={{'BACK'}}
    photoCaptureConfig={{
        quality : 'MEDIUM'
    }}
    ocrConfig={{
        language:'EN',
    }}
    onCloseCallback={(event) => {
        console.log(event.nativeEvent)
    }}
    onErrorCallback={(event) => {
        console.log(event.nativeEvent)
    }}
    onSuccessCallback={(event) => {
        console.log(event.nativeEvent)
    }}
/>
```

## License

MIT

---

Created By [Instantpay](https://www.instantpay.in)
