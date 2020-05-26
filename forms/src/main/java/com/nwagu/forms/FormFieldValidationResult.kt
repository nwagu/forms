package com.nwagu.forms

data class FormFieldValidationResult(
    val ok: Boolean,
    val feedback: String? = null,
    val error: String? = null
)