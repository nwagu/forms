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

class FormTest {

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
    fun testEmptyForm() {
        val form = Form(scope)
        assertTrue(form.verify())
    }

    @Test
    fun testSimpleForm() {
        // Create a simple form with one text field
        val form = Form(scope)
        val formField = FormField<String>()
        formField.addValidator { validateNotEmpty() }
        form.addFormField(formField)

        // Verify that the form is invalid when the field is empty
        assertFalse(form.verify())

        // Add some value to the field and verify that the form is valid
        formField.value = "Some value"
        assertTrue(form.verify())
    }

    @Test
    fun testIsComplete() {
        // Create a simple form with one text field
        val form = Form(scope)
        val formField = FormField<String>()
        formField.addValidator { validateNotEmpty() }
        form.addFormField(formField)

        // Verify that the form is invalid when the field is empty
        assertFalse(form.isComplete.value)

        // Add some value to the field and test that formCompleted is updated
        runBlocking {
            formField.value = "Some value"
            delay(50)
            assertTrue(form.isComplete.value)

            formField.value = null
            delay(50)
            assertFalse(form.isComplete.value)

            formField.value = ""
            delay(50)
            assertFalse(form.isComplete.value)

            formField.value = "Some other value"
            delay(50)
            assertTrue(form.isComplete.value)
        }

    }

    @Test
    fun testFormFieldManagement() {
        val form = Form(scope)

        // valid field
        val field1 = FormField<String>(initialValue = "hello").apply {
            addValidator { validateNotEmpty() }
            addTo(form)
        }

        // invalid because the initial value is null
        val field2 = FormField<String>().apply {
            addValidator { validateNotEmpty() }
            addTo(form)
        }

        // valid because it's not required
        val field3 = FormField<String>(required = false).apply {
            addValidator { validateNotEmpty() }
            addTo(form)
        }

        // invalid because the initial value is empty
        val field4 = FormField<String>(initialValue = "").apply {
            addValidator { validateNotEmpty() }
            addTo(form)
        }

        form.addFormFields(field1, field2, field3, field4)
        assertFalse(form.verify())

        // remove invalid fields and verify that form is now valid
        form.removeFormField(field2)
        form.removeFormField(field4)
        assertTrue(form.verify())

        // add only invalid fields and verify that form is invalid
        form.removeFormFields(field1, field3)
        form.addFormField(field2)
        form.addFormField(field4)
        assertFalse(form.verify())

        // remove all fields and verify that the empty form is valid
        form.removeFormFields(field2, field4)
        assertTrue(form.verify())
    }

    @Test
    fun testFocusRequest() {
        val form = Form(scope)

        // valid field
        val field1 = FormField<String>(initialValue = "hello").apply {
            addValidator { validateNotEmpty() }
            addTo(form)
        }

        // invalid
        val field2 = FormField<String>(initialValue = "notanemail").apply {
            addValidator { validateEmailAddress() }
            addTo(form)
        }

        // another valid field
        val field3 = FormField<String>(required = false).apply {
            addValidator { validateNotEmpty() }
            addTo(form)
        }

        var focusRequestCount = 0
        scope.launch {
            field2.focusRequest.collect { focusRequestCount++ }
        }

        runBlocking {
            delay(50)
            repeat(3) {
                val temp = focusRequestCount
                form.verify()
                delay(50)
                assertEquals(temp + 1, focusRequestCount)
            }
        }
    }
}