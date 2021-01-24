package com.example.forms

import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.forms.data.Gender
import com.nwagu.forms.utils.Utils.observeFormField

class MainActivity : AppCompatActivity() {

    lateinit var nameEdit: AppCompatEditText
    lateinit var postalCodeEdit: AppCompatEditText
    lateinit var genderSelect: RadioGroup
    lateinit var submitBtn: AppCompatButton

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        initializeViews()
        setupViews()
    }

    private fun initializeViews() {
        nameEdit = findViewById(R.id.nameEdit)
        postalCodeEdit = findViewById(R.id.postalCodeEdit)
        genderSelect = findViewById(R.id.genderRadioGroup)
        submitBtn = findViewById(R.id.submitBtn)
    }

    private fun setupViews() {

        attachToFormFields()
        observeFormFields()

        nameEdit.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                viewModel.name.errorReportingActive = true
        }

        postalCodeEdit.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                viewModel.postalCode.errorReportingActive = true
        }

        submitBtn.setOnClickListener {
            viewModel.submit()
        }

        // Activate submit button when form is verified complete
        viewModel.form.isComplete.observe(this, {
            submitBtn.isActivated = it
        })

    }

    private fun attachToFormFields() {

        nameEdit.doOnTextChanged { text, start, count, after ->
            viewModel.name.value = text?.toString()
        }

        postalCodeEdit.doOnTextChanged { text, start, count, after ->
            viewModel.postalCode.value = text?.toString()
        }

        genderSelect.setOnCheckedChangeListener { group, checkedId ->
            run {
                viewModel.gender.value = (Gender.valueOf(group.findViewById<RadioButton>(checkedId).tag.toString()))
            }
        }
    }

    private fun observeFormFields() {

        observeFormField(
            formField = viewModel.name,
            lifecycleOwner = this,
            onFeedback = {
                nameEdit.error = it
            },
            onError = {
                nameEdit.error = it
            },
            onRequestFocus = {
                // bring nameEdit to focus
            }
        )

        observeFormField(
            formField = viewModel.postalCode,
            lifecycleOwner = this,
            onFeedback = {
                postalCodeEdit.error = it
            },
            onError = {
                postalCodeEdit.error = it
            },
            onRequestFocus = {
                // bring postalCodeEdit to focus
            }
        )

        observeFormField(
            formField = viewModel.gender,
            lifecycleOwner = this,
            onError = { errorMessage ->
                findViewById<TextView>(R.id.genderSelectFeedback).let { feedbackView ->
                    feedbackView.text = errorMessage
                    feedbackView.visibility = VISIBLE
                }
            },
            onRequestFocus = {
                // bring gender radio group back to focus
            }
        )
    }

}
