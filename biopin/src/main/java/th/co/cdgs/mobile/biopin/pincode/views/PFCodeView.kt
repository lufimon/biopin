package th.co.cdgs.mobile.biopin.pincode.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import th.co.cdgs.mobile.biopin.R

class PFCodeView : LinearLayout {

    companion object {
        const val DEFAULT_CODE_LENGTH = 4
    }

    private var mCodeViews: MutableList<CheckBox> = mutableListOf()
    private var mCode = ""
    private var mCodeLength =
        DEFAULT_CODE_LENGTH
    private var mListener: OnPFCodeListener? = null

    constructor(context: Context?) : super(context){
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.view_code_pf_lockscreen, this)
        setUpCodeViews()
    }

    fun setCodeLength(codeLength: Int) {
        mCodeLength = codeLength
        setUpCodeViews()
    }

    private fun setUpCodeViews() {
        removeAllViews()
        mCodeViews.clear()
        mCode = ""
        for (i in 0 until mCodeLength) {
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.view_pf_code_checkbox, null) as CheckBox

            val layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val margin = resources.getDimensionPixelSize(R.dimen.code_fp_margin)
            layoutParams.setMargins(margin, margin, margin, margin)
            view.layoutParams = layoutParams
            view.isChecked = false
            addView(view)
            mCodeViews.add(view)
        }
        if (mListener != null) {
            mListener!!.onCodeNotCompleted("")
        }
    }

    fun input(number: String): Int {
        if (mCode.length == mCodeLength) {
            return mCode.length
        }
        mCodeViews[mCode.length].toggle() //.setChecked(true);
        mCode += number
        if (mCode.length == mCodeLength && mListener != null) {
            mListener!!.onCodeCompleted(mCode)
        }
        return mCode.length
    }

    fun delete(): Int {
        if (mListener != null) {
            mListener!!.onCodeNotCompleted(mCode)
        }
        if (mCode.isEmpty()) {
            return mCode.length
        }
        mCode = mCode.substring(0, mCode.length - 1)
        mCodeViews[mCode.length].toggle()
        return mCode.length
    }

    fun clearCode() {
        if (mListener != null) {
            mListener!!.onCodeNotCompleted(mCode)
        }
        mCode = ""
        for (codeView in mCodeViews) {
            codeView.isChecked = false
        }
    }

    fun getInputCodeLength(): Int {
        return mCode.length
    }

    fun getCode(): String {
        return mCode
    }

    fun setListener(listener: OnPFCodeListener) {
        mListener = listener
    }

    interface OnPFCodeListener {

        fun onCodeCompleted(code: String)

        fun onCodeNotCompleted(code: String)

    }
}