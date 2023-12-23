package com.nwagu.forms

import kotlin.test.Test
import kotlin.test.assertTrue

class FormTest {

    @Test
    fun testExample() {
        assertTrue(Greeting().greet().contains("Hello"), "Check 'Hello' is mentioned")
    }
}