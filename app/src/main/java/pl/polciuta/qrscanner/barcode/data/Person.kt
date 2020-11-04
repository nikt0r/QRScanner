package pl.polciuta.qrscanner.barcode.data

data class Person(
    val firstName: String?,
    val lastName: String?,
    val middleName: String?,
    val prefix: String?,
    val suffix: String?,
    val pronunciation: String?,
    val formattedName: String?
)