package com.guet.flexbox.playground.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.guet.flexbox.litho.widget.BlurTransformation

class BlurLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ViewTreeObserver.OnPreDrawListener {

    private val rect = Rect()

    private val canvas = Canvas()

    private var bitmap: Bitmap? = null

    private var canNewFrame = true

    init {
        setWillNotDraw(false)
        viewTreeObserver.addOnPreDrawListener(this)
    }

    private fun createNewFrame(): Bitmap {
        val input = Glide.get(context).bitmapPool[
                width,
                height,
                Bitmap.Config.ARGB_8888
        ]
        canvas.setBitmap(input)
        getGlobalVisibleRect(rect)
        canvas.translate(
                -rect.left.toFloat(),
                -rect.top.toFloat()
        )
        rootView.draw(canvas)
        canvas.translate(
                rect.left.toFloat(),
                rect.top.toFloat()
        )
        canvas.setBitmap(null)
        return input
    }

    override fun onPreDraw(): Boolean {
        if (canNewFrame) {
            canNewFrame = false
            Glide.with(this)
                    .asBitmap()
                    .load(createNewFrame())
                    .transform(BlurTransformation(15f, 0.5f))
                    .into(MyTarget())
        }
        return true
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (this.canvas !== canvas) {
            super.dispatchDraw(canvas)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val bitmap = bitmap
        if (this.canvas !== canvas && bitmap != null) {
            rect.set(0, 0, width, height)
            canvas.drawBitmap(bitmap, null, rect, null)
        }
    }

    private inner class MyTarget : CustomTarget<Bitmap>() {

        override fun onStart() {
            Glide.with(this@BlurLayout).clear(this)
            canNewFrame = true
            this@BlurLayout.bitmap = null
            invalidate()
        }

        override fun onStop() {
            canNewFrame = false
        }

        override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?
        ) {
            val oldBitmap = bitmap
            bitmap = resource
            invalidate()
            val bitmapPool = Glide.get(context).bitmapPool
            if (oldBitmap != null) {
                bitmapPool.put(oldBitmap)
            }
            canNewFrame = true
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            onLoadFailed(null)
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            canNewFrame = true
        }
    }

}
