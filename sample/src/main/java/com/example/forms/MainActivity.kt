package com.example.forms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.example.forms.data.Gender
import com.nwagu.forms.Form
import com.nwagu.forms.FormField
import com.nwagu.forms.FormFieldValidators.validateNonNullObject
import com.nwagu.forms.FormFieldValidators.validateNotEmpty
import com.nwagu.forms.utils.Utils.observeFormField

class MainActivity : AppCompatActivity() {

    lateinit var nameEdit: AppCompatEditText
    lateinit var genderSelect: RadioGroup
    lateinit var submitBtn: AppCompatButton

    val form = Form()

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

        attachToFormFields()
        observeFormFields()

        submitBtn.setOnClickListener {
            if (form.verify())
                submitAction(name.value!!, gender.value!!)
        }

        // Activate submit button when form is verified complete
        form.isComplete.observe(this, Observer {
            submitBtn.isActivated = it
        })

    }

    private fun attachToFormFields() {

        nameEdit.doOnTextChanged { text, start, count, after ->
            name.value = text?.toString()
        }

        genderSelect.setOnCheckedChangeListener { group, checkedId ->
            run {
                gender.value = (Gender.valueOf(group.findViewById<RadioButton>(checkedId).tag.toString()))
            }
        }
    }

    private fun observeFormFields() {

        observeFormField(
            formField = name,
            lifecycleOwner = this,
            onError = {
                nameEdit.error = it
            }
        )

        observeFormField(
            formField = gender,
            lifecycleOwner = this,
            onError = {
                findViewById<TextView>(R.id.genderSelectFeedback).let { feedbackView ->
                    feedbackView.text = it
                    feedbackView.visibility = VISIBLE
                }
            }
        )
    }

    private fun submitAction(name: String, gender: Gender) {
        // Do submit work
    }

}
