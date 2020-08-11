package com.nwagu.forms

import androidx.lifecycle.MediatorLiveData

/*
* A form is basically a list of [FormFields]
* with methods to manage them (add, remove, verify, etc)
*
* Forms can be used for single text inputs like in a login form,
* or for more complex scenarios like image selection and upload to a service
* */
class Form {

    private val formFields = arrayListOf<FormField<*>>()

    /*
    * True when all the formfields' values are verified ok
    * */
    val isComplete = MediatorLiveData<Boolean>()

    /*
    * Runs validators for all the formfields in this form
    * */
    fun verify(): Boolean {
        return formFields.all { it.verify() }
    }

    private fun softVerify(): Boolean {
        return formFields.all { it.ok }
    }

    fun addFormField(formField: FormField<*>) {
        this.formFields.add(formField)

        isComplete.addSource(formField) {
            if (it != null) {
                formField.verify()
                isComplete.value = softVerify()
            }
        }
    }

    /*
    * Add multiple formfields at once
    * */
    fun addFormFields(formFields: List<FormField<*>>) {
        this.formFields.addAll(formFields)

        for (field in formFields) {
            isComplete.addSource(field) {
                if (it != null) {
                    field.verify()
                    isComplete.value = softVerify()
                }
            }
        }
    }

    /*
    * Add multiple formfields at once
    * */
    fun addFormFields(vararg formFields: FormField<*>) {
        this.formFields.addAll(formFields)

        for (field in formFields) {
            isComplete.addSource(field) {
                if (it == null) {
                    field.verify()
                    isComplete.value = softVerify()
                }
            }
        }
    }

    /*
    * Remove a formfield
    * */
    fun removeFormField(formField: FormField<*>) {
        this.formFields.remove(formField)
        isComplete.removeSource(formField)
    }

}