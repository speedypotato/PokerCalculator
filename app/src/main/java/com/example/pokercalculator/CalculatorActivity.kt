package com.example.pokercalculator

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.marginEnd
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class CalculatorActivity : AppCompatActivity() {
    companion object {
        const val defaultBuyIn = 5000.0
        val colorValueMap = mapOf( R.color.white to 0.0002, R.color.yellow to 0.0004,  R.color.red to 0.001,
            R.color.blue to 0.002, R.color.grey to 0.004, R.color.green to 0.005, R.color.orange to 0.01,
            R.color.black to 0.02, R.color.pink to 0.05, R.color.purple to 0.1, R.color.yellowDG to 0.2,
            R.color.lightBlue to 0.4, R.color.maroon to 1.0)
        val nextColor = mapOf(R.color.white to R.color.yellow, R.color.yellow to R.color.red,
            R.color.red to R.color.blue, R.color.blue to R.color.grey, R.color.grey to R.color.green,
            R.color.green to R.color.orange, R.color.orange to R.color.black,
            R.color.black to R.color.pink, R.color.pink to R.color.purple,
            R.color.purple to R.color.yellowDG, R.color.yellowDG to R.color.lightBlue,
            R.color.lightBlue to R.color.maroon, R.color.maroon to R.color.white)
        const val defaultQty = 0
        const val decimalPlaces = 3
        const val maxLength = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initBuyIn()

        colorMap = LinkedHashMap()
        valQuantMap = LinkedHashMap()
        initRows(colorValueMap)

        updateTotal()
    }

    /**
     * Initiates buy in
     */
    private fun initBuyIn() {
        buyInButton.setOnClickListener {
            val buyInEditText = EditText(this).apply {
                setText(buyInAmt.toString())
                inputType = InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL
            }
            AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.enter_buy_in_amt))
                .setMessage(resources.getString(R.string.buy_in_message))
                .setView(buyInEditText)
                .setPositiveButton(resources.getString(R.string.ok)) { _, _ -> updateValues(buyInEditText.text.toString()) }
                .setNegativeButton(resources.getString(R.string.cancel), null)
                .show()
        }
    }

    /**
     * Creates rows for chips
     */
    private fun initRows(colors: Map<Int, Double>) {
        val tw = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                updateTotal()
            }
        }

        var alt = 0
        for (color in colors) {
            chipLayout.addView(LinearLayout(this).apply {
                if (alt % 2 == 0)
                    setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                alt++
                gravity = Gravity.CENTER
                addView(ImageView(this.context).apply {
                    layoutParams = LinearLayout.LayoutParams(100, 100).apply{
                        weight = 0.5f
                    }
                    setBackgroundColor(ContextCompat.getColor(this.context, color.key))
                    setOnClickListener { updateColor(this) }
                    colorMap[this] = color.key
                })
                val value = EditText(this.context).apply {
                    layoutParams = LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT).apply{
                        weight = 1f
                    }
                    setText((color.value * buyInAmt).toString())
                    inputType = InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL
                    addTextChangedListener(tw)
                    filters += InputFilter.LengthFilter(maxLength)
                }
                val qty = EditText(this.context).apply {
                    layoutParams = LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT).apply{
                        weight = 1f
                    }
                    setText(defaultQty.toString())
                    inputType = InputType.TYPE_CLASS_NUMBER
                    addTextChangedListener(tw)
                    filters += InputFilter.LengthFilter(maxLength)
                }
                valQuantMap[value] = qty
                addView(value)
                addView(qty)
            })
        }
    }

    /**
     * onClick to rotate colors
     */
    private fun updateColor(v: View) {
        v.setBackgroundColor(
            ContextCompat.getColor(this, nextColor[colorMap[v]] ?: R.color.white)
        )
        colorMap[v] = nextColor[colorMap[v]] ?: R.color.white
    }

    /**
     * Updates chip values when a new buy-in is submitted
     */
    private fun updateValues(s: String) {
        if (s.isNotBlank() && s.toDoubleOrNull() is Double) {
            buyInAmt = s.toDouble()
            var cur = R.color.white
            for (fields in valQuantMap) {
                fields.key.setText(((colorValueMap[cur] ?: 0.0002) * buyInAmt).toString())
                cur = nextColor[cur] ?: R.color.white
            }
        }
    }

    /**
     * Recalculates total and formats decimal place
     */
    private fun updateTotal() {
        var total = BigDecimal(0)
        for (fields in valQuantMap) {
            if (fields.key.text.isNotBlank() && fields.value.text.isNotBlank())
                total += fields.key.text.toString().toBigDecimal() * (fields.value.text.toString().toBigDecimal())
        }
        var totalString = "\$" + total.toString()
        val numDecimals = totalString.substring(totalString.indexOf('.') + 1).length
        if (!totalString.contains('.'))
            totalString += ".00"
        else if (numDecimals > decimalPlaces)
            totalString = totalString.substring(0, totalString.indexOf('.') + 1 + decimalPlaces)
        else if (numDecimals < decimalPlaces)
            for (i in numDecimals until decimalPlaces) totalString += "0"
        resultTextView.text = totalString
    }



    private var buyInAmt = defaultBuyIn
    private lateinit var valQuantMap: LinkedHashMap<EditText, EditText>
    private lateinit var colorMap: LinkedHashMap<View, Int>     //added because getting color from View isn't working
}
