package com.example.flushit.errors

class NoCorrespondingUserError : CustomError() {
    override var message: String = "Det finns ingen användare med den här e-mail adressen"

    override fun displayError() = message
}