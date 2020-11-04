package pl.polciuta.qrscanner.barcode.data

data class StreetAddress(
    val addressLines: List<String>,
    val type: Type
) {

    enum class Type {
        HOME,
        WORK,
        UNKNOWN
    }
}