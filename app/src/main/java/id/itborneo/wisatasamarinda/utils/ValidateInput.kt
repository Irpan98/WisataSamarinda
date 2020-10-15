package id.itborneo.wisatasamarinda.utils

import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout

object ValidateInput {
    //return true if null
    fun isEditNull(
        editText: EditText,
        warningText: String,
        editTextInputLayout: TextInputLayout? = null
    ): Boolean {

        return if (editText.text.isEmpty()) {
            if (editTextInputLayout == null) {
                editText.error = warningText

            } else {
                editTextInputLayout.error = warningText
            }
            true
        } else {
            editTextInputLayout?.error = null
            editText.error = null
            false

        }


    }
}