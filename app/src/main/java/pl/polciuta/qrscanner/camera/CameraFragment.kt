package pl.polciuta.qrscanner.camera

import android.graphics.PixelFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.camera.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.polciuta.qrscanner.R
import pl.polciuta.qrscanner.analyzer.RelativeCornerPoints
import pl.polciuta.qrscanner.animation.Motion
import pl.polciuta.qrscanner.animation.Reticle
import pl.polciuta.qrscanner.barcode.BarcodeUnknown
import pl.polciuta.qrscanner.card.CardFragment
import pl.polciuta.qrscanner.config.highlightColors
import pl.polciuta.qrscanner.databinding.CameraBinding


@androidx.camera.core.ExperimentalGetImage
class CameraFragment : Fragment() {

    private val model by activityViewModels<SharedViewModel>()
    private lateinit var overlaySurfaceHolder: SurfaceHolder
    private lateinit var reticle: Reticle
    private lateinit var snackbar: Snackbar
    private lateinit var reticleClearJob: Job
    private val motion by lazy { Motion(motionLayout) }
    private val colors by lazy {
        highlightColors.mapValues {
            ContextCompat.getColor(requireContext(), it.value)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: CameraBinding = DataBindingUtil.inflate(inflater, R.layout.camera, container, false)
        binding.lifecycleOwner = this
        binding.viewmodel = model
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        overlaySurfaceView.apply {
            setZOrderOnTop(true)
            overlaySurfaceHolder = holder.apply {
                setFormat(PixelFormat.TRANSPARENT)
            }
            setOnTouchListener { _, event ->
                discardCard(event.y)
                performClick()
            }
        }

        initObservers()

        startCamera(view)

    }

    private fun initObservers() {
        addMessageObserver()
        addBarcodeDataObserver()
        addCardHeightObserver()
    }

    private fun addCardHeightObserver() {
        model.cardHeightLiveData.observe(viewLifecycleOwner, {
            reticle.clearOverlay()
            animate(it)
        })
    }

    private fun addMessageObserver() {
        model.messageLiveData.observe(viewLifecycleOwner, {
            showSnack(it)
        })
    }

    private fun addBarcodeDataObserver() {
        model.barcodeLiveData.observe(viewLifecycleOwner, { barcodeData ->
//            Log.d(LOGTAG, "addBarcodeDataObserver: true")
            model.stopImageAnalysis()
            highlightBarcode(barcodeData.cornerPoints)
            when (barcodeData) {
                !is BarcodeUnknown -> loadCardFragment()
                else -> handleUnknownCode(barcodeData)
            }
        })
    }

    private fun animate(cardHeight: Int) {
        model.createBitmap()?.let { screenCap.setImageBitmap(it) }
        if (!motion.isRunning) {
            with(motionLayout) {
                runTransition(measuredWidth, measuredHeight, cardHeight)
            }
        }
    }

    private fun runTransition(measuredWidth: Int, measuredHeight: Int, cardHeight: Int) {
        with(model) {
            val (barcodeX, barcodeY) = getBarcodePosition(measuredWidth, measuredHeight)
            val cardEndY = (measuredHeight - cardHeight) / 2
            motion.runTransition(barcodeX, barcodeY, cardEndY.toFloat()) { restartImageAnalysis() }
        }
    }

    // Discard card when clicked outside of it
    private fun discardCard(touchY: Float) {
        model.cardHeightLiveData.value?.let {
            with(motionLayout) {
                val topY = (measuredHeight - it) / 2
                val bottomY = (measuredHeight + it) / 2
                if (touchY < topY || touchY > bottomY) motion.discard()
            }
        }
    }

    private fun handleUnknownCode(barcodeData: BarcodeUnknown) {
        showUnknownCodeMessage(barcodeData)
        clearReticleAfterDelay()
    }

    private fun showUnknownCodeMessage(barcodeData: BarcodeUnknown) {
        showSnack("$UNKNOWN_CODE_MSG\n${barcodeData.rawValue}", Snackbar.LENGTH_INDEFINITE, R.string.btn_copy) {
            barcodeData.copyRawValue(model)
        }
    }

    private fun highlightBarcode(cornerPoints: RelativeCornerPoints) {
        reticle = Reticle(overlaySurfaceHolder, colors)
        val scaleX = motionLayout.measuredWidth
        val scaleY = motionLayout.measuredHeight
        reticle.drawShape(cornerPoints.upscale(scaleX, scaleY))
    }

    private fun clearReticleAfterDelay() {
        if (::reticleClearJob.isInitialized && reticleClearJob.isActive) reticleClearJob.cancel()

        reticleClearJob = lifecycleScope.launch {
            delay(RETICLE_SHOW_TIME)
            reticle.clearOverlay()
            clearSnack()
        }
    }

    private suspend fun clearSnack() {
        if (::snackbar.isInitialized && snackbar.isShown) {
            delay(SNACK_SHOW_TIME)
            snackbar.dismiss()
        }
    }

    private fun startCamera(view: View) {
        view.post {
//            Log.d(LOGTAG, "startCamera: ${camera_preview.measuredWidth} ${camera_preview.measuredHeight}")
//            Log.d(LOGTAG, "startCamera: ${view.measuredWidth} ${view.measuredHeight}")
            model.startCamera(camera_preview)
        }
    }

    private fun loadCardFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.cardContainer, CardFragment())
            .commit()

//        Log.d(LOGTAG, "loadCardFragment HEIGHT: ${cardContainer.measuredHeight}")
    }

    private fun showSnack(
        message: String,
        duration: Int = Snackbar.LENGTH_SHORT,
        buttonId: Int = NO_BUTTON,
        action: () -> Unit = {}
    ) {
        if (!::snackbar.isInitialized || !snackbar.isShown)
            snackbar = Snackbar.make(coordinator, message, duration).apply {
                if (buttonId != NO_BUTTON) {
                    setAction(buttonId) {
                        action()
                        dismiss()
                    }
                }
                show()
            }
    }


    companion object {
        private const val LOGTAG = "LOG_CameraFragment"
        private const val UNKNOWN_CODE_MSG = "UNKNOWN CODE TYPE - DATA:"
        private const val RETICLE_SHOW_TIME = 1000L
        private const val SNACK_SHOW_TIME = 5000L
        private const val NO_BUTTON = -1
    }
}
