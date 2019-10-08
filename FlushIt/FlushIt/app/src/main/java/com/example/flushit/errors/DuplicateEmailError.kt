package com.example.flushit.errors

class DuplicateEmailError : CustomError() {

    override var message: String = "Denna e-mail är redan registrerad"

    override fun displayError(): String {
        return message
    }
}