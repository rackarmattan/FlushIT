package com.example.flushit.errors

class OtherError : CustomError() {
    override var message: String = "Något fel har inträffat"

    override fun displayError() = message
}