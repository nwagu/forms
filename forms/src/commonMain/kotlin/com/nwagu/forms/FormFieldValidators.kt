package com.nwagu.forms

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