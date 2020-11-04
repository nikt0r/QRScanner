package pl.polciuta.qrscanner.animation

import androidx.constraintlayout.motion.widget.MotionLayout
import pl.polciuta.qrscanner.R
import pl.polciuta.qrscanner.utils.BitmapUtils

class Motion(private val motionLayout: MotionLayout) {

    var isRunning = false

    fun runTransition(barcodeStartX: Float, barcodeStartY: Float, cardEndY: Float, onFinished: () -> Unit) {

        if (isRunning) return

        val transitionListener = object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, fromState: Int, toState: Int) = Unit
            override fun onTransitionChange(p0: MotionLayout?, fromState: Int, toState: Int, progress: Float) = Unit
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) = Unit
            override fun onTransitionCompleted(p0: MotionLayout?, stateReached: Int) {
                if (stateReached == R.id.phase2end) {
                    isRunning = false
                    onFinished()
                }
            }
        }

        isRunning = true

        motionLayout.apply {

            transitionToState(R.id.phase1start)

            setTransitionListener(transitionListener)

            getConstraintSet(R.id.screenCapInitSet).apply {
                setTranslation(R.id.screenCap, barcodeStartX, barcodeStartY)
                updateState(R.id.phase1start, this)
            }

            getConstraintSet(R.id.phase1end).apply {
                val sizePx = BitmapUtils.convertDpToPx(48f, context) + cardEndY
                setTranslationY(R.id.screenCap, sizePx)
                setTranslationY(R.id.cardContainer, cardEndY)
                updateState(R.id.phase1end, this)
            }
            transitionToState(R.id.phase1end)
        }
    }

}
