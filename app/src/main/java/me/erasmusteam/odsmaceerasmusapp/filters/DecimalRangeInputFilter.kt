package me.erasmusteam.odsmaceerasmusapp.filters

import android.text.InputFilter
import android.text.Spanned

class DecimalRangeInputFilter(
    private val min: Double,
    private val max: Double
) : InputFilter {
    override fun filter(
        source: CharSequence, // New text being inserted
        start: Int,
        end: Int,
        dest: Spanned, // Current text in the EditText
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            // Construct the new string by combining current text with the new text
            val newText = dest.substring(0, dstart) + source.subSequence(start, end) + dest.substring(dend)
            // Allow if the newText is empty (i.e. user deleted everything)
            if (newText.isEmpty() || newText == "-" || newText == "." || newText == "-.") {
                return null
            }
            // Parse the value
            val input = newText.toDouble()
            if (input in min..max) {
                return null // Accept the change
            }
        } catch (e: NumberFormatException) {
            // If parsing fails, reject the change
        }
        return "" // Reject change by returning empty string
    }
}
