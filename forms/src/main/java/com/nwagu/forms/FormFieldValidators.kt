package com.nwagu.forms

import android.util.Patterns
import java.util.regex.Pattern

object FormFieldValidators {

	fun Any?.validateNonNullObject(): FormFieldValidationResult {
		(this != null).let {
			return FormFieldValidationResult(ok = it, error = if (it) null else "Should not be empty")
		}
	}

	fun String?.validateNotEmpty(): FormFieldValidationResult {
		(this?.isNotEmpty()).let {
			(it == null || it == false).let {
				return FormFieldValidationResult(ok = !it, error = if (!it) null else "Should not be empty")
			}
		}
	}

	fun List<*>?.validateNotNullOrEmpty(): FormFieldValidationResult {
		(this?.isNullOrEmpty()).let {
			(it == null || it == true).let {
				return FormFieldValidationResult(ok = !it, error = if (!it) null else "Should not be empty")
			}
		}
	}

	fun String?.validateEmailAddress(): FormFieldValidationResult {
		(this != null && Patterns.EMAIL_ADDRESS.matcher(this).matches()).let {
			return FormFieldValidationResult(ok = it, error = if (it) null else "Invalid email")
		}
	}

	/*fun String?.validatePhoneNumber(): FormFieldValidationResult {
		(this != null && Pattern.compile("^[+]?[0-9]{11,13}\$").matcher(this).matches()).let {
			return FormFieldValidationResult(ok = it, error = if (it) null else "Invalid phone number")
		}
	}*/
	
}