package pl.polciuta.qrscanner

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import pl.polciuta.qrscanner.camera.CameraFragment
import pl.polciuta.qrscanner.config.appPermissions
import pl.polciuta.qrscanner.utils.Helpers
import pl.polciuta.qrscanner.utils.alsoIfFalse
import pl.polciuta.qrscanner.utils.alsoIfTrue


@androidx.camera.core.ExperimentalGetImage
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ask for permissions and start camera when all granted
        // Otherwise, close the app with a message
        checkPermissions { loadCameraFragment() }
    }

    private fun loadCameraFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, CameraFragment())
            .commit()
    }

    private fun checkPermissions(runWhenAllGranted: () -> Unit) {
        val startForResultPermissions = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            result.all { it.value }
                .alsoIfTrue { runWhenAllGranted() }
                .alsoIfFalse {
                    Helpers.showToast(this, getString(R.string.msg_all_perms_required))
                    finish()
                }
        }

        startForResultPermissions.launch(appPermissions)
    }

    companion object {
        private const val LOGTAG = "LOG_MainActivity"
    }

}