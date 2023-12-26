[![Maven Central](https://img.shields.io/maven-central/v/com.nwagu.forms/forms.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.nwagu.forms%22%20AND%20a:%22forms%22)

## A form management library for Android

This simple library aims to add structure to the creation and management of forms on Android.
From simple login forms to more complex forms that contain varying object types.

## Usage

Add the dependency

```groovy
dependencies {
    // ...
    implementation "com.nwagu.forms:forms:$latestVersion"
}
```

See [sample activity](sampleAndroidApp/src/main/java/com/example/forms/MainActivity.kt) for usage

Create your form fields and add them to an instance of Form.

```kotlin
val form = Form(viewModelScope)

val name = FormField<String>(required = true)
        .apply {
            addValidator { validateNotEmpty() }
            addTo(form)
        }

val gender = FormField<Gender>()
        .apply {
            addValidator { validateNonNullObject() }
            addTo(form)
        }
```

Update form
```kotlin
nameEdit.doOnTextChanged { text, start, count, after ->
    name.value = text?.toString()
}
```

Set error reporting to begin on focus changed
```kotlin
nameEdit.setOnFocusChangeListener { v, hasFocus ->
    if (!hasFocus)
        name.errorReportingEnabled = true
}
```

Form fields expose observable feedback and error fields:
```kotlin
name.observe(
    lifecycleScope,
    onFeedback = {
        // display helpful feedback
    },
    onError = {
        nameEdit.error = it
    },
    onFocusRequest = {
        // bring nameEdit to focus
    }
)
```

#### Tips
1. You can define your custom validation functions. A few generic validators have been added to [FormFieldValidators](forms/src/commonMain/kotlin/com/nwagu/forms/FormFieldValidators.kt).
2. Observe a form field's `focusRequest` to bring the view to focus when the form field has an error.

## Note about multiplatform status
You can use this library now on Android or the JVM. I am currently trying it out on the other target platforms, especially iOS. 
That's why it's still in alpha. 
If you would like to use the older, stable, LiveData-based version of this library just for Android, please use version: `1.0.3`.
