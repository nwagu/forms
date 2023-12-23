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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import com.example.forms.data.Gender
import com.nwagu.forms.utils.observe
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var nameEdit: AppCompatEditText
    private lateinit var postalCodeEdit: AppCompatEditText
    private lateinit var genderSelect: RadioGroup
    private lateinit var submitBtn: AppCompatButton

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
                viewModel.name.errorReportingEnabled = true
        }

        postalCodeEdit.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus)
                viewModel.postalCode.errorReportingEnabled = true
        }

        submitBtn.setOnClickListener {
            viewModel.submit()
        }

        // Activate submit button when form is verified complete
        lifecycleScope.launch {
            viewModel.form.isComplete.collect {
                submitBtn.isActivated = it
            }
        }

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

        viewModel.name.observe(
            lifecycleScope,
            onFeedback = {
                //
            },
            onError = {
                nameEdit.error = it
            },
            onFocusRequest = {
                // bring nameEdit to focus
            }
        )

        viewModel.postalCode.observe(
            lifecycleScope,
            onFeedback = {
                //
            },
            onError = {
                postalCodeEdit.error = it
            },
            onFocusRequest = {
                // bring postalCodeEdit to focus
            }
        )

        viewModel.gender.observe(
            lifecycleScope,
            onError = { errorMessage ->
                findViewById<TextView>(R.id.genderSelectFeedback).let { feedbackView ->
                    feedbackView.text = errorMessage
                    feedbackView.visibility = VISIBLE
                }
            },
            onFocusRequest = {
                // bring gender radio group back to focus
            }
        )
    }

}
