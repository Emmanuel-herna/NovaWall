package com.novawall.app.wallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlin.math.sin

class NovaWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return NovaEngine()
    }

    inner class NovaEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private var visible = false
        private var width = 0
        private var height = 0
        private var time = 0f

        private val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        private val drawRunnable = object : Runnable {
            override fun run() {
                drawFrame()
                if (visible) {
                    handler.postDelayed(this, 16) // ~60fps
                }
            }
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            this.width = width
            this.height = height
            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            this.visible = visible
            if (visible) {
                handler.post(drawRunnable)
            } else {
                handler.removeCallbacks(drawRunnable)
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            visible = false
            handler.removeCallbacks(drawRunnable)
        }

        private fun drawFrame() {
            val holder = surfaceHolder
            var canvas: Canvas? = null

            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    // Dark background
                    canvas.drawColor(Color.parseColor("#0B0D11"))

                    // Simple animated circles for demo
                    time += 0.05f

                    for (i in 0 until 8) {
                        val x = width / 2f + sin(time + i * 0.8f) * (width * 0.3f)
                        val y = height / 2f + sin(time * 0.7f + i * 1.2f) * (height * 0.25f)
                        val radius = 40f + sin(time + i) * 20f

                        paint.color = when (i % 3) {
                            0 -> Color.parseColor("#7C4DFF")
                            1 -> Color.parseColor("#00E5FF")
                            else -> Color.parseColor("#FF4081")
                        }
                        paint.alpha = 180

                        canvas.drawCircle(x, y, radius, paint)
                    }
                }
            } finally {
                if (canvas != null) {
                    try {
                        holder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        // Surface was destroyed
                    }
                }
            }
        }
    }
}
