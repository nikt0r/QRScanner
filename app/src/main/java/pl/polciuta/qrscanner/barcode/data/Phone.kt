package pl.polciuta.qrscanner.barcode.data

data class Phone(
    val number: String?,
    val type: Type
) {
    enum class Type {
        HOME,
        MOBILE,
        WORK,
        FAX,
        UNKNOWN {
            override fun toString(): String {
                return ""
            }
        }
    }
}