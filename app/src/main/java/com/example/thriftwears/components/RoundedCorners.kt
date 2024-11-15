package com.example.thriftwears.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.squareup.picasso.Transformation


class RoundedCorners(private val radius: Float) : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val paint = Paint().apply { isAntiAlias = true }
        val output = Bitmap.createBitmap(source.width, source.height, source.config)
        val canvas = Canvas(output)
        val rect = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
        canvas.drawRoundRect(rect, radius, radius, paint)
        source.recycle()
        return output
    }

    override fun key(): String = "rounded_corners_$radius"
}