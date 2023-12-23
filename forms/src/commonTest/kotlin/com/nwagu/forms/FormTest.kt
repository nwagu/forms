package com.nwagu.forms

import com.nwagu.forms.FormFieldValidators.validateNotEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FormTest {

    val scope = CoroutineScope(EmptyCoroutineContext)

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
    }
}