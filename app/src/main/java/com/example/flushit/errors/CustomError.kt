package com.example.flushit.errors

/**
 * Abstract class for custom errors.
 */
abstract class CustomError {
    protected abstract var message: String

    /**
     * @return The error that has occurred
     */
    abstract fun displayError() : String
}