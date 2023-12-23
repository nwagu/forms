package com.example.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forms.CustomValidators.validatePostalCode
import com.example.forms.data.Gender
import com.nwagu.forms.Form
import com.nwagu.forms.FormField
import com.nwagu.forms.FormFieldValidators.validateNonNullObject
import com.nwagu.forms.FormFieldValidators.validateNotEmpty

class MainViewModel: ViewModel() {

    val form = Form(viewModelScope)

    val name = FormField<String>(required = true)
        .apply {
            addValidator { validateNotEmpty() }
            addTo(form)
        }

    val postalCode = FormField<String>()
        .apply {
            addValidators(
                { validateNotEmpty() },
                { validatePostalCode() }
            )
            addTo(form)
        }

    val gender = FormField<Gender>()
        .apply {
            addValidator { validateNonNullObject() }
            addTo(form)
        }

    fun submit() {
        if (form.verify()) {
            doAction(name.value!!, postalCode.value!!, gender.value!!)
        }
    }

    private fun doAction(name: String, postalCode: String, gender: Gender) {
        //
    }

}