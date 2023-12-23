package com.nwagu.forms

import com.nwagu.forms.FormFieldValidators.validateEmailAddress
import com.nwagu.forms.FormFieldValidators.validateNotEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FormFieldTest {

    @Test
    fun testStringValidators() {
        val formField = FormField<String>()
        formField.addValidator { validateNotEmpty() }

        formField.value = "Hello"
        assertTrue(formField.isValid)
        formField.value = ""
        assertFalse(formField.isValid)
        formField.value = null
        assertFalse(formField.isValid)
    }

    @Test
    fun testMultipleValidators() {
        val formField = FormField<String>()
        formField.addValidator { validateNotEmpty() }
        formField.addValidator { validateEmailAddress() }

        // TODO: Complete this test
    }

    @Test
    fun testCustomValidatorError() {
        val formField = FormField<String>().apply {
            errorReportingEnabled = true
            // add to a form so that validations are run automatically
            addTo(Form(scope = CoroutineScope(EmptyCoroutineContext)))
        }

        formField.addValidator {
            // validator to check that the input is a 7 char string
            if (this?.length == 7) {
                FormFieldValidationResult(isValid = true, error = null)
            } else {
                FormFieldValidationResult(isValid = false, error = "Should be 7 characters long")
            }
        }

        runBlocking {
            formField.value = "1234567"
            delay(50)
            assertEquals(null, formField.error.value)

            formField.value = "Hello World"
            delay(50)
            assertEquals("Should be 7 characters long", formField.error.value)

            formField.value = "Hello W"
            delay(50)
            assertEquals(null, formField.error.value)

            formField.value = "Hello!"
            delay(50)
            assertEquals("Should be 7 characters long", formField.error.value)
        }
    }

    @Test
    fun testCustomValidatorFeedback() {
        val formField = FormField<String>().apply {
            // add to a form so that validations are run automatically
            addTo(Form(scope = CoroutineScope(EmptyCoroutineContext)))
        }

        formField.addValidator {
            // simple validator to report password strength
            if (this.isNullOrEmpty()) {
                FormFieldValidationResult(isValid = false, error = "Should not be empty")
            } else if (this.length < 5) {
                FormFieldValidationResult(isValid = true, feedback = "Very weak password")
            } else if (this.length < 10) {
                FormFieldValidationResult(isValid = true, feedback = "Weak password")
            } else if (this.length < 15) {
                FormFieldValidationResult(isValid = true, feedback = "Strong password")
            } else {
                FormFieldValidationResult(isValid = true, feedback = "Very strong password")
            }
        }

        runBlocking {
            formField.value = ""
            delay(50)
            assertEquals(null, formField.feedback.value)

            formField.value = "123"
            delay(50)
            assertEquals("Very weak password", formField.feedback.value)

            formField.value = "jfyfyuvtybtuytrtydtydtydftyftyfytbfu"
            delay(50)
            assertEquals("Very strong password", formField.feedback.value)

            formField.value = "uyfguyvyi"
            delay(50)
            assertEquals("Weak password", formField.feedback.value)

            formField.value = "uyfguyvyijhiuh"
            delay(50)
            assertEquals("Strong password", formField.feedback.value)
        }
    }

}