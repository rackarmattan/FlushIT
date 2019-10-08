package com.example.flushit.errors

class InvalidPasswordError : CustomError() {
    override var message: String = "Fel lösenord"

    override fun displayError() = message

}