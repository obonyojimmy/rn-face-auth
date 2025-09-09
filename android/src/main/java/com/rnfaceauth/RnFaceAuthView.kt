package com.rnfaceauth

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.Surface
import android.widget.FrameLayout
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import java.util.concurrent.Executors


class RnFaceAuthView(context: Context) : FrameLayout(context), SurfaceHolder.Callback {
    private val surfaceView: SurfaceView = SurfaceView(context)
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private var previewSize: android.util.Size? = null
    private var sensorOrientation: Int = 0
    private var adjustedRotation: Int = 0
    private var width: Int = 0; 
    private var height: Int = 0
    private var faceDetector: FaceDetector? = null
    private val faceOverlay = FaceOverlayView(context)
    private var isProcessing = false

    //private val executor = Executors.newSingleThreadExecutor()

    init {
        addView(
            surfaceView,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )
        addView(
            faceOverlay,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )
        surfaceView.holder.addCallback(this)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        width = right - left
        height = bottom - top

        // Reconfigure preview to best match this width/height
        //setupCamera(width, height)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraIdList = cameraManager.cameraIdList

            // Try to pick the front camera first
            val frontCameraId = cameraIdList.find { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                facing == CameraCharacteristics.LENS_FACING_FRONT
            } ?: cameraIdList.first() // fallback to first camera

            val characteristics = cameraManager.getCameraCharacteristics(frontCameraId)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0

            // Pick a reasonable preview size (first available or fallback)
            val sizes = map?.getOutputSizes(SurfaceHolder::class.java)
            previewSize = sizes?.filter { it.width >= 640 && it.height >= 480 }
                ?.minByOrNull { it.width * it.height }
                ?: android.util.Size(640, 480)
            //previewSize = map?.getOutputSizes(SurfaceHolder::class.java)?.firstOrNull()
            //    ?: android.util.Size(640, 480)

            // Apply preview size to SurfaceHolder
            holder.setFixedSize(previewSize!!.width, previewSize!!.height)

            // Get device rotation
            val rotation = when ((context as? ReactContext)?.currentActivity?.windowManager?.defaultDisplay?.rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> 0
            }

            // Adjust rotation relative to sensor orientation
            adjustedRotation = (rotation + sensorOrientation) % 360



            cameraManager.openCamera(frontCameraId, object :CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    startPreview(holder)
                }

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    camera.close()
                    Log.e("FaceAuthView", "Camera error: $error")
                }
            }, null)

        } catch (e: CameraAccessException) {
            Log.e("FaceAuthView", "Camera access exception: ${e.message}")
        } catch (e: SecurityException) {
            Log.e("FaceAuthView", "No camera permission: ${e.message}")
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        captureSession?.close()
        cameraDevice?.close()
        imageReader?.close()
        faceDetector?.close()
    }

    private fun startPreview(holder: SurfaceHolder) {
        try {
            val surface = holder.surface

            // Initialize ML Kit Face Detector
            val opts = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()
            // .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            faceDetector = FaceDetection.getClient(opts)

            val handlerThread = HandlerThread("CameraBackground")
            handlerThread.start()
            val backgroundHandler = Handler(handlerThread.looper)

            // Create ImageReader for ML Kit frames using previewSize
            //imageReader = ImageReader.newInstance(640, 480, ImageFormat.YUV_420_888, 2)
            imageReader = ImageReader.newInstance(
                previewSize!!.width,
                previewSize!!.height,
                ImageFormat.YUV_420_888,
                2
            )
            
            val previewSurface = surface
            val readerSurface = imageReader!!.surface

            val captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(previewSurface)
            captureRequestBuilder.addTarget(readerSurface)

            cameraDevice!!.createCaptureSession(
                listOf(previewSurface, readerSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        session.setRepeatingRequest(captureRequestBuilder.build(), null, null)

                        // Process frames for ML Kit
                        imageReader!!.setOnImageAvailableListener({ reader ->
                            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
                            processImage(image)
                            image.close()
                            Log.d("CameraHandler", "Image processed")
                        }, backgroundHandler)
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e("CameraHandler", "Session config failed")
                    }
                },
                null
            )
            
            
        } catch (e: Exception) {
            Log.e("FaceAuthView", "Camera preview failed: ${e.message}")
        }
    }

    private fun processImage(mediaImage: android.media.Image) {
        if (isProcessing) return
        isProcessing = true
        val inputImage = InputImage.fromMediaImage(mediaImage, adjustedRotation)

        //Log.d("MLKit", "Processing image for face detection...")
        faceDetector!!.process(inputImage)
            .addOnSuccessListener { faces ->
                Log.d("MLKit", "Faces detected: ${faces.size}")
                faceOverlay.updateFaces(
                    faces,
                    previewSize!!.width,
                    previewSize!!.height,
                    width,  // view width
                    height  // view height
                )

                if (faces.isNotEmpty()) {
                    val face = faces[0] // first detected face
                    val reactContext = context as ReactContext
                    reactContext
                        .getJSModule(RCTEventEmitter::class.java)
                        .receiveEvent(id, "onSuccess", 
                            com.facebook.react.bridge.Arguments.createMap().apply {
                                putString("faceData", "Face detected at ${face.boundingBox}")
                            })
                } /*else {
                    val reactContext = context as ReactContext
                    reactContext
                        .getJSModule(RCTEventEmitter::class.java)
                        .receiveEvent(id, "onFailure", 
                            com.facebook.react.bridge.Arguments.createMap().apply {
                                putString("error", "No face detected")
                            })
                } */
            }
            .addOnFailureListener { e ->
                //Log.e("FaceAuthView", "Detection error: ${e.message}")
            }
            .addOnCompleteListener {
                isProcessing = false
            }
    }
}
