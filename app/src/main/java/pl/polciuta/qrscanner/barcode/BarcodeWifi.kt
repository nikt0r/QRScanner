package pl.polciuta.qrscanner.barcode

import android.app.Application
import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
import pl.polciuta.qrscanner.BR
import pl.polciuta.qrscanner.R
import pl.polciuta.qrscanner.analyzer.RelativeCornerPoints
import pl.polciuta.qrscanner.barcode.data.WifiEncryptionType
import pl.polciuta.qrscanner.camera.SharedViewModel
import pl.polciuta.qrscanner.card.InfoRow
import pl.polciuta.qrscanner.utils.Helpers

@androidx.camera.core.ExperimentalGetImage
class BarcodeWifi(
    val ssid: String?,
    val password: String?,
    val encryptionType: WifiEncryptionType?,
    override val layout: Int = R.layout.card_wifi
) :
    IBarcode {

    override val label = R.string.header_wifi

    override lateinit var cornerPoints: RelativeCornerPoints

    override fun bindData(binding: ViewDataBinding) {
        binding.setVariable(BR.barcodeWifi, this)
    }

    override fun createDisplayList(context: Context): List<InfoRow> {
        val itemList = listOf(
            context.getString(R.string.label_ssid) to ssid,
            context.getString(R.string.label_password) to password,
            context.getString(R.string.label_encrType) to encryptionType.toString()
        )
        return Helpers.createRowList(itemList)
    }

    fun copy(model: SharedViewModel) {
        ssid?.let {
            model.copyText(
                "SSID: $ssid\nPASS: $password\nENCR. TYPE: ${encryptionType.toString()}"
            )
        }
    }

    fun addWifiToConfig(model: SharedViewModel) {
        ssid?.let {
            password?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // API >= 29
                    connectToWifiAPI29(model.app, ssid, password)
                } else {
                    connectToWifiAPI21(model.app)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToWifiAPI29(app: Application, ssid: String, password: String) {
        WifiNetworkSuggestion.Builder().apply {
            setSsid(ssid)
            setIsAppInteractionRequired(true)
            if (encryptionType == WifiEncryptionType.WPA) setWpa2Passphrase(password) // no pass for OPEN
            build().also {
                val wifiManager = app.getSystemService(Context.WIFI_SERVICE) as WifiManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // API >= 29
                    val status = wifiManager.addNetworkSuggestions(listOf(it))
//                    Log.d(LOGTAG, "addWifiToConfig: $status")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun connectToWifiAPI21(app: Application) {
        val wifiConfig = WifiConfiguration()

        wifiConfig.SSID = "$ssid"
        when (encryptionType) {
            WifiEncryptionType.WPA -> wifiConfig.preSharedKey = "$password"
            WifiEncryptionType.WEP -> wifiConfig.wepKeys[0] = "$password"
            else -> Unit
        }

        val wifiManager = app.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.addNetwork(wifiConfig).apply {
            wifiManager.disconnect()
            wifiManager.enableNetwork(this, true)
            wifiManager.reconnect()
        }
    }

    companion object {
        private const val LOGTAG = "LOG_BarcodeWifi"
    }

}