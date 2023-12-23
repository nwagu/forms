package com.nwagu.forms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

typealias Validator<T> = T?.() -> FormFieldValidationResult

/*
* A FormField is a MutableStateFlow, so it can be collected and its value updated like any other MutableStateFlow
* But it also has parameters that make it suitable for use in a form,
* such as validators, error stateflow, feedback stateflow, and focus request.
*
* @param required: Whether this field is optional or not.
*   If true, the form field value is required to be non-null or non-empty to be validated
*   If false, the field value is optional and will be valid if it is empty or null
*   Note that if required is false and the value of this field is neither null nor empty,
*       the field will be validated as if it were required
* @param initialValue: The initial value of this field
*   Would typically be null or an empty string unless the field is pre-filled (no pun intended)
* */
class FormField<T>(
    val required: Boolean = true,
    initialValue: T? = null
): MutableStateFlow<T?> by MutableStateFlow(initialValue) {

    /*
    * Validators are extension functions (on the type of the form field) that return a FormFieldValidationResult
    * Validators are run in the order they are added to the form field
    * If a validator fails, the remaining validators are not run
    * If you make use of the feedback property, it might be best practice to add only one validator
    */
    private val validators = arrayListOf<Validator<T?>>()

    /*
    * This emits a Unit value when the field is to be brought to the user's attention
    * It is triggered by the form when the field fails validation
    */
    internal val _focusRequest: MutableStateFlow<Unit> = MutableStateFlow(Unit)
    val focusRequest: StateFlow<Unit> = _focusRequest.asStateFlow()

    private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _feedback: MutableStateFlow<Any?> = MutableStateFlow(null)
    val feedback: StateFlow<Any?> = _feedback.asStateFlow()

    /*
    * This is used to achieve a good UX with 'out of focus' error reporting
    * Set this field to true, for example after the edit text loses focus,
    * so that the error messages for the form field can start being emitted
    * */
    var errorReportingEnabled = false
        set(value) {
            field = value
            if (value && _error.value != tentativeError) {
                _error.value = tentativeError
            } else if (!value) {
                _error.value = null
            }
        }

    private var tentativeError: String? = null
        private set(value) {
            field = value
            if (errorReportingEnabled) _error.value = value
        }

    val isValid: Boolean
        get()  { return verify() }

    private fun verify(): Boolean {
        // If field is optional and (null or empty), return true
        if (!required && (value == null || value.toString().isEmpty())) {
            applyValidationResult(FormFieldValidationResult(isValid = true))
            return true
        }

        for (validator in validators) {
            val result = validator.invoke(value)
            applyValidationResult(result)
            if (!result.isValid) return false
        }

        return true
    }

    private fun applyValidationResult(result: FormFieldValidationResult) {
        _feedback.value = result.feedback
        tentativeError = result.error
    }

    fun addValidator(validator: Validator<T?>) {
        this.validators.add(validator)
    }

    fun addValidators(vararg validators: Validator<T?>) {
        this.validators.addAll(validators)
    }

    fun addTo(form: Form) {
        form.addFormField(this)
    }
}
