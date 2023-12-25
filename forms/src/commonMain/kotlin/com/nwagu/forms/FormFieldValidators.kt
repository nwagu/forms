package com.nwagu.forms

/**
 * This object contains some form field validators that are commonly needed in forms.
 *
 * If any of them do not meet your needs (for instance, maybe the email address regex
 * is too permissive), then you should write your own custom validators and use those instead.
 * You can also make a pull request to add to this file any validators you think would be useful to others.
 */
object FormFieldValidators {

    private val emailAddressRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
    private val phoneNumberRegex = Regex("^\\+?[0-9\\s()-]{8,}\$")

	fun Any?.validateNonNullObject(): FormFieldValidationResult {
		(this != null).let {
			return FormFieldValidationResult(isValid = it, error = if (it) null else "Should not be empty")
		}
	}

	fun String?.validateNotEmpty(): FormFieldValidationResult {
		(this?.isNotBlank()).let {
			(it == null || it == false).let {
				return FormFieldValidationResult(isValid = !it, error = if (!it) null else "Should not be empty")
			}
		}
	}

	fun Collection<*>?.validateNotEmpty(): FormFieldValidationResult {
		(this?.isEmpty()).let {
			(it == null || it == true).let {
				return FormFieldValidationResult(isValid = !it, error = if (!it) null else "Should not be empty")
			}
		}
	}

	fun String?.validateEmailAddress(): FormFieldValidationResult {
		(this != null && this.matches(emailAddressRegex)).let {
			return FormFieldValidationResult(isValid = it, error = if (it) null else "Invalid email address")
		}
	}

	fun String?.validatePhoneNumber(): FormFieldValidationResult {
		(this != null && this.matches(phoneNumberRegex)).let {
			return FormFieldValidationResult(isValid = it, error = if (it) null else "Invalid phone number")
		}
	}

}