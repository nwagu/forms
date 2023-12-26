package com.nwagu.forms

import com.nwagu.forms.FormFieldValidators.validateEmailAddress
import com.nwagu.forms.FormFieldValidators.validateNotEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FormFieldTest {

    private var scope = CoroutineScope(EmptyCoroutineContext)

    @BeforeTest
    fun setup() {
        scope = CoroutineScope(EmptyCoroutineContext)
    }

    @AfterTest
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun testSimpleFormField() {
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
    fun testOptionalFormField() {
        val formField = FormField<String>(required = false)
        formField.addValidator { validateEmailAddress() }

        formField.value = "ben@ben.com"
        assertTrue(formField.isValid)

        formField.value = ""
        assertTrue(formField.isValid) // because it's optional

        formField.value = null
        assertTrue(formField.isValid) // because it's optional

        formField.value = "ben@ben"
        assertFalse(formField.isValid) // must be valid even if it's optional
    }

    @Test
    fun testFormFieldErrorState() {
        val formField = FormField<String>().apply {
            addValidator { validateEmailAddress() }
            errorReportingEnabled = true
            // add to a form so that validations are run automatically
            addTo(Form(scope))
        }

        var errorMessage: String? = null
        scope.launch {
            formField.error.collect { errorMessage = it }
        }

        runBlocking {
            delay(50)

            formField.value = "ben@ben.com"
            delay(50)
            assertEquals(null, errorMessage)

            formField.value = "b"
            delay(50)
            assertEquals("Invalid email address", errorMessage)

            formField.value = "b@"
            delay(50)
            assertEquals("Invalid email address", formField.error.value)

            formField.value = "b@b"
            delay(50)
            assertEquals("Invalid email address", errorMessage)

            formField.value = "b@be"
            delay(50)
            assertEquals("Invalid email address", errorMessage)

            formField.value = "b@ben"
            delay(50)
            assertEquals("Invalid email address", formField.error.value)

            formField.value = "b@ben."
            delay(50)
            assertEquals("Invalid email address", formField.error.value)

            formField.value = "b@ben.com"
            delay(50)
            assertEquals(null, errorMessage)

            formField.value = "be@ben.com"
            delay(50)
            assertEquals(null, errorMessage)

            formField.value = "ben@ben.com"
            delay(50)
            assertEquals(null, formField.error.value)

            formField.value = "beny@ben.com"
            delay(50)
            assertEquals(null, formField.error.value)
        }
    }

    @Test
    fun testMultipleValidators() {
        val formField = FormField<String>()
        formField.addValidator { validateEmailAddress() }
        formField.addValidator {
            // validator to check that the input is not more than 30 chars long
            if (this.isNullOrEmpty()) {
                FormFieldValidationResult(isValid = false, error = "Should not be empty")
            } else if (this.length <= 30) {
                FormFieldValidationResult(isValid = true, error = null)
            } else {
                FormFieldValidationResult(isValid = false, error = "Too long")
            }
        }

        formField.value = "Hello"
        assertFalse(formField.isValid)

        formField.value = "john@john.com"
        assertTrue(formField.isValid)

        formField.value = "john".repeat(10) + "@john.com"
        assertFalse(formField.isValid)
    }

    @Test
    fun testCustomValidatorError() {
        val formField = FormField<String>().apply {
            errorReportingEnabled = true
            // add to a form so that validations are run automatically
            addTo(Form(scope))
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
            addTo(Form(scope))
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

            formField.value = "averylonglypasswordthatshouldbeverystrongly"
            delay(50)
            assertEquals("Very strong password", formField.feedback.value)

            formField.value = "password"
            delay(50)
            assertEquals("Weak password", formField.feedback.value)

            formField.value = "strongpassword"
            delay(50)
            assertEquals("Strong password", formField.feedback.value)
        }
    }

}