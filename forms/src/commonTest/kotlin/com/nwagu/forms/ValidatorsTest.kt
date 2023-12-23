package com.nwagu.forms

import com.nwagu.forms.FormFieldValidators.validateEmailAddress
import com.nwagu.forms.FormFieldValidators.validateNonNullObject
import com.nwagu.forms.FormFieldValidators.validateNotEmpty
import com.nwagu.forms.FormFieldValidators.validatePhoneNumber
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidatorsTest {

    @Test
    fun testValidateObjectNotNull() {
        val formField = FormField<Any>()
        formField.addValidator { validateNonNullObject() }

        formField.value = "Hello"
        assertTrue(formField.isValid)

        formField.value = 1
        assertTrue(formField.isValid)

        formField.value = ""
        assertTrue(formField.isValid)

        formField.value = null
        assertFalse(formField.isValid)

        formField.value = Unit
        assertTrue(formField.isValid)

    }

    @Test
    fun testValidateStringNotEmpty() {
        val formField = FormField<String>()
        formField.addValidator { validateNotEmpty() }

        formField.value = "Hello"
        assertTrue(formField.isValid)

        formField.value = ""
        assertFalse(formField.isValid)

        // blanks are not allowed too
        formField.value = " "
        assertFalse(formField.isValid)

        formField.value = "     "
        assertFalse(formField.isValid)

        formField.value = "Hello"
        assertTrue(formField.isValid)

    }

    @Test
    fun testValidateCollectionNotEmpty() {
        val formField = FormField<Collection<Any>>()
        formField.addValidator { validateNotEmpty() }

        formField.value = listOf("Hello")
        assertTrue(formField.isValid)

        formField.value = emptyList()
        assertFalse(formField.isValid)

    }

    @Test
    fun testValidateEmailAddresses() {
        val formField = FormField<String>()
        formField.addValidator { validateEmailAddress() }

        formField.value = "emeka@emeka.com"
        assertTrue(formField.isValid)

        formField.value = "emeka@emeka.co.uk"
        assertTrue(formField.isValid)

        formField.value = "emeka@emeka"
        assertFalse(formField.isValid)

        formField.value = "emeka@emeka.emeka"
        assertTrue(formField.isValid)

        formField.value = "emeka+mary@emeka.com"
        assertTrue(formField.isValid)

        formField.value = "john+mary+johnagain@john.com"
        assertTrue(formField.isValid)

        formField.value = "john+mary@john"
        assertFalse(formField.isValid)

    }

    @Test
    fun testValidatePhoneNumbers() {
        val formField = FormField<String>()
        formField.addValidator { validatePhoneNumber() }

        formField.value = "+1(902)123-4567"
        assertTrue(formField.isValid)

        formField.value = "+234(123)456-7890"
        assertTrue(formField.isValid)

        formField.value = "+33(123)456-7890"
        assertTrue(formField.isValid)

        formField.value = "+234 123 456 7890"
        assertTrue(formField.isValid)

        formField.value = "22 434"
        assertFalse(formField.isValid)

        formField.value = "1234567890"
        assertTrue(formField.isValid)

    }

    @Test
    fun testCustomValidators() {
        val formField = FormField<String>()
        formField.addValidator {
            // validator to check that the input is a 7 character string
            if (this?.length == 7) {
                FormFieldValidationResult(isValid = true, error = null)
            } else {
                FormFieldValidationResult(isValid = false, error = "Should be 7 characters long")
            }
        }

        formField.value = "qwerty!"
        assertTrue(formField.isValid)

        formField.value = "Hello W"
        assertTrue(formField.isValid)

        formField.value = "Hello World"
        assertFalse(formField.isValid)
    }

}