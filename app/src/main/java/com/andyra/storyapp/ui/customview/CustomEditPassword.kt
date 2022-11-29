package com.andyra.storyapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.andyra.storyapp.R

class CustomEditPassword : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                nullRemover()
                lengthChecker()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun nullRemover() {
        val mBeforeReplace = text.toString()
        val mAfterReplace = mBeforeReplace.replace(" ", "")
        if (mBeforeReplace != mAfterReplace) {
            setText(mAfterReplace)
        }
        setSelection(text!!.length)
    }

    private fun lengthChecker() {
        error = if (text!!.length < 6) {
            context.getString(R.string.invalid_password)
        } else {
            null
        }
    }

}