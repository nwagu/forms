package com.nwagu.forms

data class FormFieldValidationResult(
    val ok: Boolean,
    val feedback: String? = null, // Should this be a list?
    val error: String? = null
)