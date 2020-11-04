package pl.polciuta.qrscanner.config

import android.Manifest
import pl.polciuta.qrscanner.R

// Permissions to ask for at the app start
val appPermissions = arrayOf(Manifest.permission.CAMERA)

val highlightColors = mapOf(
    HighlightColor.RETICLE to R.color.colorFocusRect,
    HighlightColor.BACKGROUND to R.color.barcode_reticle_background
)
