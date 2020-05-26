package com.nwagu.forms

import androidx.lifecycle.MediatorLiveData

class Form {

    private val formFields = arrayListOf<FormField<*>>()

    val isComplete = MediatorLiveData<Boolean>()

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

    fun removeFormField(formField: FormField<*>) {
        this.formFields.remove(formField)
        isComplete.removeSource(formField)
    }

}