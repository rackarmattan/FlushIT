package com.example.flushit.errors

class NetworkError : CustomError() {
    override var message = "Ett nätverksfel har uppstått. Prova att slå på internet om du inte redan har det."

    override fun displayError() = message
}