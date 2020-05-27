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
        onError: (String?) -> Unit = {}
    ) {

        formField.observe(lifecycleOwner, Observer {
            onValueChanged.invoke(it)
        })

        formField.feedback.observe(lifecycleOwner, Observer {
            onFeedback.invoke(it)
        })

        formField.error.observe(lifecycleOwner, Observer {
            onError.invoke(it)
        })

    }
}