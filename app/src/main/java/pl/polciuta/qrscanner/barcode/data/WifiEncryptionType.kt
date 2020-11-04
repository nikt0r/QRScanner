package pl.polciuta.qrscanner.barcode.data

enum class WifiEncryptionType {
    OPEN {
        override fun toString(): String {
            return "NONE"
        }
    },
    WPA {
        override fun toString(): String {
            return "WPA/WPA2"
        }
    },
    WEP {
        override fun toString(): String {
            return "WEP"
        }
    },
    UNKNOWN {
        override fun toString(): String {
            return "Unknown"
        }
    }
}