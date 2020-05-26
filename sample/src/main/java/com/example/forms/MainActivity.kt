package com.example.forms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.Observer
import com.example.forms.data.Gender
import com.nwagu.forms.Form
import com.nwagu.forms.FormField
import com.nwagu.forms.FormFieldValidators.validateNonNullObject
import com.nwagu.forms.FormFieldValidators.validateNotEmpty

class MainActivity : AppCompatActivity() {

	lateinit var nameEdit: AppCompatEditText
	lateinit var genderSelect: RadioGroup
	lateinit var submitBtn: AppCompatButton

	val form = Form()

	val name =
		FormField<String>(required = true)
			.apply {
				addValidator { validateNotEmpty() }
				addTo(form)
			}

	val gender =
		FormField<Gender>().apply {
			addValidator { validateNonNullObject() }
			addTo(form)
		}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		initializeViews()
		setupViews()
	}

	private fun initializeViews() {
		nameEdit = findViewById(R.id.nameEdit)
		genderSelect = findViewById(R.id.genderRadioGroup)
		submitBtn = findViewById(R.id.submitBtn)
	}

	private fun setupViews() {

		submitBtn.setOnClickListener {
			if (form.verify())
				submitAction(name.value!!, gender.value!!)
		}

		// Activate submit button when form is verified complete
		form.isComplete.observe(this, Observer {
			submitBtn.isActivated = it
		})

	}

	private fun submitAction(name: String, gender: Gender) {
		// Do submit work
	}

}
