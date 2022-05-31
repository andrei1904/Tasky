package com.example.tasky.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

abstract class ValidatorFragment<VB : ViewBinding> : BaseFragment<VB>() {

    private lateinit var textInputs: Map<String, TextInputLayout>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textInputs = getTextInputsMap()
    }

    protected fun validate(
//        textInputs: Map<String, TextInputLayout>,
        allFields: List<String>,
        completedFields: MutableSet<String>
    ) {
        var isValid: Boolean
        for (field in allFields) {
            isValid = completedFields.contains(field)

            textInputs[field]?.let { safeTextInput -> setValidationError(safeTextInput, isValid) }
        }
    }

    private fun setValidationError(textInputLayout: TextInputLayout, isValid: Boolean) {
        if (isValid) {
            textInputLayout.isErrorEnabled = false
        } else {
            textInputLayout.error = "Add " + textInputLayout.hint
        }
    }

    protected fun addValidationListener(textInputLayout: TextInputLayout, field: String, set: (String, String) -> Unit) {
        val editText = textInputLayout.editText ?: return

        editText.addTextChangedListener { editable ->
            val value = editable.toString()

            if (value.isEmpty()) {
                textInputLayout.error = "Add " + textInputLayout.hint
            } else {
                textInputLayout.isErrorEnabled = false
            }
            set(value, field)
        }
    }

    abstract fun getTextInputsMap() : Map<String, TextInputLayout>
}