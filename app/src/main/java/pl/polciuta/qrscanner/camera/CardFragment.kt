package pl.polciuta.qrscanner.camera

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.card_calendar_event.*
import pl.polciuta.qrscanner.BR
import pl.polciuta.qrscanner.card.CardAdapter

class CardFragment : Fragment() {

    private val model by activityViewModels<SharedViewModel>()
    private val barcodeData
        get() = model.barcodeLiveData.value

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return barcodeData?.let { barcode ->
            val layout = barcode.layout
            val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, layout, container, false)
            binding.run {
                lifecycleOwner = this@CardFragment
                setVariable(BR.viewmodel, model)
                barcode.bindData(this)
                root
            }
        }
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.post {
//            Log.d(LOGTAG, "onViewCreated: ${view.measuredHeight}")
            model.postCardHeight(view.measuredHeight)
        }

        barcodeData?.let { barcode ->
            val adapter = enableRecyclerAdapter()
            adapter.submitList(
                barcode.createDisplayList(requireContext())
            )
        }

    }

    private fun enableRecyclerAdapter(): CardAdapter {
        return CardAdapter().apply cardAdapter@{
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

            cardRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                hasFixedSize()
                adapter = this@cardAdapter
            }
        }
    }


    companion object {
        private const val LOGTAG = "LOG_CardFragment"
    }

}