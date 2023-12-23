package com.nwagu.forms

data class FormFieldValidationResult(
    val isValid: Boolean,
    val error: String? = null,
    val feedback: Any? = null
)