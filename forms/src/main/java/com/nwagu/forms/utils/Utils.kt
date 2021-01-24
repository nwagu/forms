package com.nwagu.forms.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.nwagu.forms.FormField

object Utils {

    fun <T> observeFormField(
        formField: FormField<T>,
        lifecycleOwner: LifecycleOwner,
        onValueChanged: (T) -> Unit = {},
        onFeedback: (String?) -> Unit = {},
        onError: (String?) -> Unit = {},
        onRequestFocus: () -> Unit = {}
    ) {

        formField.observe(lifecycleOwner, {
            onValueChanged.invoke(it)
        })

        formField.feedback.observe(lifecycleOwner, {
            onFeedback.invoke(it)
        })

        formField.error.observe(lifecycleOwner, {
            onError.invoke(it)
        })

        formField.requestFocus.observe(lifecycleOwner, {
            onRequestFocus.invoke()
        })

    }
}