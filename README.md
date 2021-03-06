[![Bintray](https://img.shields.io/badge/dynamic/json.svg?label=latest&query=name&style=plastic-square&url=https%3A%2F%2Fapi.bintray.com%2Fpackages%2Fnwagu%2FForms%2Fcom.nwagu.forms%2Fversions%2F_latest)](https://bintray.com/nwagu/Forms/com.nwagu.forms)
[![Maven Central](https://img.shields.io/maven-central/v/com.nwagu.forms/forms.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.nwagu.forms%22%20AND%20a:%22forms%22)

## A form management library for Android

This simple library aims to add structure to the creation and management of forms on Android.
From simple login forms to more complex forms that contain varying object types.

The FormField class extends Android LiveData class, so databinding might be preferred for updating and observing them.

## Usage

Add the dependency

```groovy
dependencies {
    // ...
    implementation "com.nwagu.forms:forms:$latestVersion"
}
```

See [sample activity](sample/src/main/java/com/example/forms/MainActivity.kt) for usage

Create your form fields and add them to an instance of Form.

```kotlin
val form = Form()

val nameFormField = FormField<String>(required = true)
        .apply {
            addValidator { validateNotEmpty() }
            addTo(form)
        }

val genderFormField = FormField<Gender>()
        .apply {
            addValidator { validateNonNullObject() }
            addTo(form)
        }
```

Update form
```kotlin
nameEdit.doOnTextChanged { text, start, count, after ->
    nameFormField.value = text?.toString()
}
```

Set error reporting to begin on focus changed
```kotlin
nameEdit.setOnFocusChangeListener { v, hasFocus ->
    if (!hasFocus)
        formField.errorReportingActive = true
}
```

Form fields expose observable feedback and error fields:
```kotlin
observeFormField(
    formField = name,
    lifecycleOwner = this,
    onError = { err
        nameEdit.error = it
    },
    onFeedback = {
        // display helpful feedback
    },
    onRequestFocus = {
        // bring nameEdit to focus
    }
)
```

#### Tips
1. You can define your custom validation functions. A few generic validators have been added to [FormFieldValidators](forms/src/main/java/com/nwagu/forms/FormFieldValidators.kt).
2. You can set a default value for a non-required form field.
3. Observe form field requestFocus parameter and, on change, scroll to bring the view to focus