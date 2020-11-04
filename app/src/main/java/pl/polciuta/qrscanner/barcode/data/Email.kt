package pl.polciuta.qrscanner.barcode.data

data class Email(
    val email: String?,
    val type: Type
) {
    enum class Type {
        HOME,
        WORK,
        UNKNOWN {
            override fun toString(): String {
                return ""
            }
        }
    }
}