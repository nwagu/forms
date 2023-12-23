package com.nwagu.forms

object FormFieldValidators {

    private val emailAddressRegex = Regex(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    private val phoneNumberRegex = Regex("^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}\$")

	fun Any?.validateNonNullObject(): FormFieldValidationResult {
		(this != null).let {
			return FormFieldValidationResult(isValid = it, error = if (it) null else "Should not be empty")
		}
	}

	fun String?.validateNotEmpty(): FormFieldValidationResult {
		(this?.isNotEmpty()).let {
			(it == null || it == false).let {
				return FormFieldValidationResult(isValid = !it, error = if (!it) null else "Should not be empty")
			}
		}
	}

	fun List<*>?.validateNotNullOrEmpty(): FormFieldValidationResult {
		(this?.isNullOrEmpty()).let {
			(it == null || it == true).let {
				return FormFieldValidationResult(isValid = !it, error = if (!it) null else "Should not be empty")
			}
		}
	}

	fun String?.validateEmailAddress(): FormFieldValidationResult {
		(this != null && this.matches(emailAddressRegex)).let {
			return FormFieldValidationResult(isValid = it, error = if (it) null else "Invalid email")
		}
	}

	fun String?.validatePhoneNumber(): FormFieldValidationResult {
		(this != null && this.matches(phoneNumberRegex)).let {
			return FormFieldValidationResult(isValid = it, error = if (it) null else "Invalid phone number")
		}
	}

}