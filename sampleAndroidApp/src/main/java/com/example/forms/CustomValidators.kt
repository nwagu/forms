package com.example.forms

import com.nwagu.forms.FormFieldValidationResult

object CustomValidators {

    fun String?.validatePostalCode(): FormFieldValidationResult {
        (this?.length == 6).let {
            return FormFieldValidationResult(isValid = it, error = if (it) null else "Invalid postal code")
        }
    }
}