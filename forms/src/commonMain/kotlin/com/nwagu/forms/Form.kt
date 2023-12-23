package com.nwagu.forms

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/*
* A form is a list of [FormFields]
* with methods to manage them (add, remove, verify, etc)
*
* Forms can be used for single text inputs like in a login form,
* or for more complex scenarios like image selection and upload to a service
* */
class Form(private val scope: CoroutineScope) {

    private val formFields = mutableMapOf<FormField<*>, Job>()

    /*
    * True when all the formfields' values are verified ok
    * Observe to activate a submit button, continue button, etc, if true
    * */
    private val _isComplete: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isComplete = _isComplete.asStateFlow()

    /*
    * Use this function when you want to validate the entire form,
    * for instance when the user clicks a submit button
    *
    * Runs validators for all the form fields in this form, until one fails;
    * enables error reporting for the failed field
    *
    * Also triggers a focus request for the failed field,
    * which can be observed to bring the failed field to the user's attention.
    *
    * @return true if all the form fields are valid, false otherwise
    * */
    fun verify(): Boolean {
        for (formField in formFields.keys) {
            if (!formField.isValid) {
                if (!formField.errorReportingEnabled)
                    formField.errorReportingEnabled = true

                formField._focusRequest.value = Unit
                return false
            }
        }
        return true
    }

    private fun softVerify(): Boolean {
        return formFields.keys.all { it.isValid }
    }

    fun addFormField(formField: FormField<*>) {
        this.formFields.put(formField, scope.launch {
            formField.collect {
                _isComplete.value = softVerify()
            }
        })
    }

    /*
    * Add multiple form fields at once
    * */
    fun addFormFields(formFields: List<FormField<*>>) {
        for (field in formFields) {
            this.formFields.put(field, scope.launch {
                field.collect {
                    _isComplete.value = softVerify()
                }
            })
        }
    }

    /*
    * Add multiple form fields at once
    * */
    fun addFormFields(vararg formFields: FormField<*>) {
        for (field in formFields) {
            this.formFields.put(field, scope.launch {
                field.collect {
                    _isComplete.value = softVerify()
                }
            })
        }
    }

    /*
    * Remove a form field
    * */
    fun removeFormField(formField: FormField<*>) {
        this.formFields[formField]?.cancel()
        this.formFields.remove(formField)
    }

}