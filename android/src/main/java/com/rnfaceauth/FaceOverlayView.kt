package com.rnfaceauth

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import com.google.mlkit.vision.face.Face

class FaceOverlayView(context: Context) : View(context) {
    private val paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }
    private val path = Path()
    private var faces: List<Face> = emptyList()
    private var scaleX: Float = 1f
    private var scaleY: Float = 1f

    fun updateFaces(newFaces: List<Face>, previewWidth: Int, previewHeight: Int, viewWidth: Int, viewHeight: Int) {
        faces = newFaces
        scaleX = viewWidth.toFloat() / previewWidth.toFloat()
        scaleY = viewHeight.toFloat() / previewHeight.toFloat()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (face in faces) {
            // Draw bounding box
            val box = face.boundingBox
            canvas.drawRect(
                box.left * scaleX,
                box.top * scaleY,
                box.right * scaleX,
                box.bottom * scaleY,
                paint
            )

            // Draw contours if available
            for (contour in face.allContours) {
                path.reset()
                val points = contour.points
                if (points.isNotEmpty()) {
                    path.moveTo(points[0].x * scaleX, points[0].y * scaleY)
                    for (pt in points.drop(1)) {
                        path.lineTo(pt.x * scaleX, pt.y * scaleY)
                    }
                    canvas.drawPath(path, paint)
                }
            }
        }
    }
}
