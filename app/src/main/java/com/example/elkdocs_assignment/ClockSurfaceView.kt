package com.example.clockanimation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.concurrent.thread

class ClockSurfaceView(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs), SurfaceHolder.Callback {
    private val paint = Paint()
    private var running = false
    private var wavePhase = 0f
    private val waveColors = arrayOf(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW, Color.MAGENTA)

    init {
        holder.addCallback(this)
        paint.isAntiAlias = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        running = true
        thread {
            while (running) {
                val canvas: Canvas? = holder.lockCanvas()
                canvas?.let {
                    drawClock(it)
                    holder.unlockCanvasAndPost(it)
                }
                Thread.sleep(16) // Approximately 60 FPS
                wavePhase += 5f // Adjust the speed of the wave animation as needed
                if (wavePhase >= 360f) {
                    wavePhase = 0f
                }
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        running = false
    }

    private fun drawClock(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)

        val centerX = width / 2
        val centerY = height / 2
        val radius = Math.min(centerX, centerY) - 20

        // Draw clock circle
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), radius.toFloat(), paint)

        // Draw animated waves on the circumference
        drawWaves(canvas, centerX, centerY, radius)

        // Draw clock ticks (without rotation)
        paint.color = Color.BLACK
        paint.strokeWidth = 8f
        for (i in 0 until 60) {
            val angle = Math.PI * i / 30 - Math.PI / 2
            val startX = (centerX + Math.cos(angle) * radius).toFloat()
            val startY = (centerY + Math.sin(angle) * radius).toFloat()
            val stopX = (centerX + Math.cos(angle) * (radius - 20)).toFloat()
            val stopY = (centerY + Math.sin(angle) * (radius - 20)).toFloat()
            if (i % 5 == 0) {
                paint.strokeWidth = 8f
            } else {
                paint.strokeWidth = 4f
            }
            canvas.drawLine(startX, startY, stopX, stopY, paint)
        }

        val calendar = java.util.Calendar.getInstance()
        val second = calendar.get(java.util.Calendar.SECOND)
        val minute = calendar.get(java.util.Calendar.MINUTE)
        val hour = calendar.get(java.util.Calendar.HOUR)
        val milliSecond = calendar.get(java.util.Calendar.MILLISECOND)

        // Draw clock hands
        drawHand(canvas, centerX, centerY, (second + milliSecond / 1000.0) * 6, radius - 30, Color.RED, 4f)
        drawHand(canvas, centerX, centerY, (minute + second / 60.0) * 6, radius - 50, Color.BLACK, 8f)
        drawHand(canvas, centerX, centerY, (hour + minute / 60.0) * 30, radius - 80, Color.BLACK, 12f)
    }

    private fun drawWaves(canvas: Canvas, centerX: Int, centerY: Int, radius: Int) {
        val rectF = RectF(
            (centerX - radius).toFloat(),
            (centerY - radius).toFloat(),
            (centerX + radius).toFloat(),
            (centerY + radius).toFloat()
        )

        for (i in waveColors.indices) {
            paint.color = waveColors[i]
            paint.strokeWidth = 20f + (i * 5f) // Increase stroke width for broader borders
            paint.style = Paint.Style.STROKE

            val startAngle = wavePhase + i * 30 // Spread the waves around the circle
            val sweepAngle = 30f // Width of each wave

            // Use a sine wave to modulate the sweep angle
            val waveModulation = (10 * Math.sin(Math.toRadians(startAngle.toDouble()))).toFloat()
            canvas.drawArc(rectF, startAngle, sweepAngle + waveModulation, false, paint)
        }
    }

    private fun drawHand(canvas: Canvas, cx: Int, cy: Int, angle: Double, length: Int, color: Int, strokeWidth: Float) {
        paint.color = color
        paint.strokeWidth = strokeWidth
        val endX = (cx + Math.cos(Math.toRadians(angle) - Math.PI / 2) * length).toFloat()
        val endY = (cy + Math.sin(Math.toRadians(angle) - Math.PI / 2) * length).toFloat()
        canvas.drawLine(cx.toFloat(), cy.toFloat(), endX, endY, paint)
    }
}
