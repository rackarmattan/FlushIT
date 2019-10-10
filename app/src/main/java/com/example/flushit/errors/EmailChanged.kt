package com.example.flushit.errors

class EmailChanged : CustomError() {
    override var message: String = "Din e-mail ändrades"

    override fun displayError(): String = message
}