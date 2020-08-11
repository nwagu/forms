package com.nwagu.forms

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

typealias Validator<R> = R?.() -> FormFieldValidationResult

/*
* A FormField is a LiveData
*
* @param <T> The type of data held by this instance
* */
class FormField<T>(
    val required: Boolean = true,
    private val defaultValue: T? = null
): LiveData<T>(defaultValue) {

    var ok: Boolean = !required
    val feedback: MutableLiveData<String> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()

    /*
    * Changes when this form field requires attention
    * */
    val requestFocus: MutableLiveData<Nothing> = MutableLiveData()

    init {
        if (required && defaultValue != null)
            throw RuntimeException("FormField with a non-null default value must be optional!")
    }

    public override fun setValue(value: T?) {
        super.setValue(value)
    }

    fun setNullValue() {
        value = null
    }

    public override fun postValue(value: T?) {
        super.postValue(value)
    }

    private val validators = arrayListOf<Validator<T>>()

    fun verify(): Boolean {

        // If field is optional and empty, intercept and set ok
        if (!required && (value == null || value.toString().isEmpty())) {
            applyValidationResult(FormFieldValidationResult(ok = true))
            return true
        }

        validators.all {
            val result = it.invoke(value)
            applyValidationResult(result)
            result.ok
        }

        return ok
    }

    private fun applyValidationResult(result: FormFieldValidationResult) {
        this.ok = result.ok
        this.feedback.value = result.feedback
        this.error.value = result.error
    }

    fun setValueUnsafe(value: Any?) {
        setValue(value as T?)
    }

    fun addValidator(validator: Validator<T>) {

        if (defaultValue != null && !validator.invoke(defaultValue).ok)
            throw RuntimeException("The validator cannot validate the default value for this form field!")

        this.validators.add(validator)
    }

    fun addValidators(vararg validators: Validator<T>) {

        if (defaultValue != null && validators.all { !it.invoke(defaultValue).ok })
            throw RuntimeException("One of the validators cannot validate the default value for this form field!")

        this.validators.addAll(validators)
    }

    fun addTo(form: Form) {
        form.addFormField(this)
    }
}