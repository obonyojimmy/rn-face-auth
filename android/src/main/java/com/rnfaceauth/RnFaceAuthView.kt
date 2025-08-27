package com.rnfaceauth

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout

class RnFaceAuthView(context: Context) : FrameLayout(context), SurfaceHolder.Callback {
    private val surfaceView: SurfaceView = SurfaceView(context)

    init {
        addView(
            surfaceView,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )
        surfaceView.holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraIdList = cameraManager.cameraIdList

            // Try to pick the front camera first
            val frontCameraId = cameraIdList.find { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val facing = characteristics.get(
                    android.hardware.camera2.CameraCharacteristics.LENS_FACING
                )
                facing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_FRONT
            } ?: cameraIdList.first() // fallback to first camera

            cameraManager.openCamera(frontCameraId, object :
                android.hardware.camera2.CameraDevice.StateCallback() {
                override fun onOpened(camera: android.hardware.camera2.CameraDevice) {
                    try {
                        val surface = holder.surface
                        val captureRequestBuilder = camera.createCaptureRequest(
                            android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW
                        )
                        captureRequestBuilder.addTarget(surface)

                        camera.createCaptureSession(
                            listOf(surface),
                            object : android.hardware.camera2.CameraCaptureSession.StateCallback() {
                                override fun onConfigured(session: android.hardware.camera2.CameraCaptureSession) {
                                    session.setRepeatingRequest(
                                        captureRequestBuilder.build(),
                                        null,
                                        null
                                    )
                                }

                                override fun onConfigureFailed(session: android.hardware.camera2.CameraCaptureSession) {
                                    Log.e("FaceAuthView", "Camera session config failed")
                                }
                            },
                            null
                        )
                    } catch (e: Exception) {
                        Log.e("FaceAuthView", "Camera preview failed: ${e.message}")
                    }
                }

                override fun onDisconnected(camera: android.hardware.camera2.CameraDevice) {
                    camera.close()
                }

                override fun onError(camera: android.hardware.camera2.CameraDevice, error: Int) {
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

    override fun surfaceDestroyed(holder: SurfaceHolder) {}
}
