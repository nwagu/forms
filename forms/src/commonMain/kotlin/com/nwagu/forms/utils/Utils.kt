package com.nwagu.forms.utils

import com.nwagu.forms.FormField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun <T> FormField<T>.observe(
    scope: CoroutineScope,
    onValueChanged: (T?) -> Unit = {},
    onFeedback: (Any?) -> Unit = {},
    onError: (String?) -> Unit = {},
    onFocusRequest: () -> Unit = {}
) {

    scope.launch {
        collect {
            onValueChanged.invoke(it)
        }
    }

    scope.launch {
        error.collect {
            onError.invoke(it)
        }
    }

    scope.launch {
        feedback.collect {
            onFeedback.invoke(it)
        }
    }

    scope.launch {
        focusRequest.collect {
            onFocusRequest.invoke()
        }
    }

}