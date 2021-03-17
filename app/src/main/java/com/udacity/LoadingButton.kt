package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var progressWidth = 0f
    private var progressAngle = 0f
    private var progressAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        if (new == ButtonState.Loading) {
            buttonProgress()
            progressAnimator.start()
            invalidate()
        } else {
            progressAnimator.cancel()
            progressWidth = 0f
            progressAngle = 0f
            invalidate()
        }
    }

    init {
        isClickable = true
        buttonState = ButtonState.Completed
    }

    fun startDownload() {
        buttonState = ButtonState.Loading
    }

    fun finishDownload() {
        buttonState = ButtonState.Completed
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (buttonState == ButtonState.Completed) {
            paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
            canvas?.drawRect(0f, heightSize.toFloat(), widthSize.toFloat(), 0f, paint)

            paint.color = Color.WHITE
            canvas?.drawText(
                resources.getString(R.string.button_complete),
                widthSize.toFloat() / 2, heightSize.toFloat() / 2 + heightSize.toFloat() / 8, paint
            )
        }

        if (buttonState == ButtonState.Loading) {

            paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
            canvas?.drawRect(0f, heightSize.toFloat(), widthSize.toFloat(), 0f, paint)

            paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            canvas?.drawRect(0f, heightSize.toFloat(), progressWidth, 0f, paint)

            paint.color = Color.WHITE
            canvas?.drawText(
                resources.getString(R.string.button_loading), widthSize.toFloat() / 2,
                heightSize.toFloat() / 2 + heightSize.toFloat() / 8, paint
            )

            paint.color = ContextCompat.getColor(context, R.color.colorAccent)
            canvas?.drawArc((widthSize - 150f), (heightSize / 2) - 50f,
                (widthSize - 50f), (heightSize / 2) + 50f,
                0f, progressAngle, true, paint)
        }
    }

    private fun buttonProgress() {
        paint.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        progressAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat()).apply {
            duration = 3000
            addUpdateListener { animator ->
                animator.repeatMode = ValueAnimator.RESTART
                animator.repeatCount = ValueAnimator.INFINITE
                progressWidth = animator.animatedValue as Float
                progressAngle = -1 * progressWidth / widthSize * 360
                invalidate()
            }
            disableViewDuringAnimation(this@LoadingButton, progressAnimator)
        }
    }

    private fun disableViewDuringAnimation(view: View, animator: ValueAnimator) {
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                view.isEnabled = true
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}