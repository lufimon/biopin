package th.co.cdgs.mobile.biopin.pincode

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import org.w3c.dom.Text
import th.co.cdgs.mobile.biopin.fingerprint.BiometricManager
import th.co.cdgs.mobile.biopin.fingerprint.listener.BiometricCallback
import th.co.cdgs.mobile.biopin.fingerprint.utils.BiometricUtils.isFingerprintAvailable
import th.co.cdgs.mobile.biopin.fingerprint.utils.BiometricUtils.isHardwareSupported
import th.co.cdgs.mobile.biopin.pincode.security.PFResult
import th.co.cdgs.mobile.biopin.pincode.viewmodels.PFPinCodeViewModel
import th.co.cdgs.mobile.biopin.pincode.views.PFCodeView
import th.co.cdgs.mobile.biopin.R
import th.co.cdgs.mobile.biopin.pincode.security.PFLoginListener

class PFLockScreenFragment : Fragment() {

    private var mFingerprintButton: View? = null
    private var mDeleteButton: View? = null
    private var mForgetButton: View? = null
    private var mCodeView: PFCodeView? = null

    private var mUseFingerPrint = true
    private var mFingerprintHardwareDetected = false
    private var mIsCreateMode = false
    private var mIsConfirmMode = false

    private var mCodeCreateListener: OnPFLockScreenCodeCreateListener? = null
    private var mCodeConfirmListener: OnPFLockScreenCodeConfirmListener? = null
    private var mLoginListener: PFLoginListener? = null
    private var mCode = ""
    private var mEncodedPinCode = ""
    private var passCodeFailedCount = 0
    private var passCodeFailedMax = 3

    private var mConfiguration: PFFLockScreenConfiguration? = null
    private var mRootView: View? = null

    private val mPFPinCodeViewModel =
        PFPinCodeViewModel()

    private var biometricManager: BiometricManager? = null

    private val mOnKeyClickListener = View.OnClickListener { v ->
        if (v is TextView) {
            val string = v.text.toString()
            if (string.length != 1) {
                return@OnClickListener
            }
            val codeLength = mCodeView!!.input(string)
            configureLeftButton(codeLength)
        }
    }

    private val mOnDeleteButtonClickListener = View.OnClickListener {
        val codeLength = mCodeView!!.delete()
        configureLeftButton(codeLength)
    }

    private val mOnDeleteButtonOnLongClickListener = View.OnLongClickListener {
        mCodeView!!.clearCode()
        configureLeftButton(0)
        true
    }

    private val mOnFingerprintClickListener = View.OnClickListener {
        getFingerprintAuthenticate(context)
    }

    private val mOnForgetClickListener = View.OnClickListener {
        deleteEncodeKey()
        mLoginListener?.onForgetPassCode()
    }


    private val mCodeListener = object : PFCodeView.OnPFCodeListener {
        override fun onCodeCompleted(code: String) {
            if (mIsCreateMode || mIsConfirmMode) {
                mCode = code
                onNextMode()
                return
            }
            mCode = code
            mPFPinCodeViewModel.checkPin(context!!, mEncodedPinCode, mCode).observe(
                this@PFLockScreenFragment,
                Observer<PFResult<Boolean>> { result ->
                    if (result == null) {
                        return@Observer
                    }
                    if (result.mError != null) {
                        return@Observer
                    }
                    val isCorrect = result.mResult
                    if (isCorrect!!) {
                        passCodeFailedCount = 0
                        mForgetButton!!.visibility = View.GONE
                        mLoginListener?.onCodeInputSuccessful()
                    } else {
                        passCodeFailedCount +=1
                        if(passCodeFailedCount == passCodeFailedMax){
                            mForgetButton!!.visibility = View.VISIBLE
                        }
                        mLoginListener?.onPinLoginFailed()
                        errorAction()
                    }
                    if (!(isCorrect) && mConfiguration!!.isClearCodeOnError()) {
                        mCodeView!!.clearCode()
                    }
                })

        }

        override fun onCodeNotCompleted(code: String) {
            if (mIsCreateMode || mIsConfirmMode) {
                return
            }
        }
    }

    private fun onNextMode(){
        when{
            mIsCreateMode -> {
                mPFPinCodeViewModel.encodePin(context!!, mCode).observe(
                        this@PFLockScreenFragment,
                        Observer<PFResult<String>> { result ->
                            if (result == null) {
                                return@Observer
                            }
                            if (result.mError != null) {
                                Log.d(TAG, "Can not encode pin code")
                                deleteEncodeKey()
                                return@Observer
                            }
                            val encodedCode = result.mResult
                            if (mCodeCreateListener != null) {
                                mCodeCreateListener!!.onCodeCreated(encodedCode!!)
                            }
                        }
                )
            }
            mIsConfirmMode -> {
                mPFPinCodeViewModel.confirmPin(context!!, mCode).observe(
                        this@PFLockScreenFragment,
                        Observer<PFResult<Boolean>> { result ->
                            if (result == null) {
                                return@Observer
                            }
                            if (result.mError != null) {
                                Log.d(TAG, "Can not confirm pin code")
                                deleteEncodeKey()
                                return@Observer
                            }
                            val confirmCode = result.mResult
                            if (mCodeConfirmListener != null) {
                                mCodeConfirmListener!!.onCodeConfirm(confirmCode!!)
                            }
                        }
                )
            }
        }
    }

    private val biometricCallback = object :
        BiometricCallback {
        override fun onSdkVersionNotSupported() {
            Log.i(TAG, getString(R.string.biometric_error_sdk_not_supported))
        }

        override fun onBiometricAuthenticationNotSupported() {
            Log.i(TAG, getString(R.string.biometric_error_hardware_not_supported))
        }

        override fun onBiometricAuthenticationNotAvailable() {
            Log.i(TAG, getString(R.string.biometric_error_fingerprint_not_available))
        }

        override fun onBiometricAuthenticationPermissionNotGranted() {
            Log.i(TAG, getString(R.string.biometric_error_permission_not_granted))
        }

        override fun onBiometricAuthenticationInternalError(error: String) {
            Log.e(TAG, error)
        }

        override fun onAuthenticationFailed() {
            mLoginListener?.onFingerprintLoginFailed()
        }

        override fun onAuthenticationCancelled() {
            Log.i(TAG, getString(R.string.biometric_cancelled))
        }

        override fun onAuthenticationSuccessful() {
            mLoginListener?.onFingerprintSuccessful()
        }

        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
            Log.i(TAG, helpString.toString())
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            mLoginListener?.onFingerprintError(errorCode, errString.toString())
        }
    }

    private val animationListener = object : Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {

        }

        override fun onAnimationEnd(p0: Animation?) {
            mCodeView!!.clearAnimation()
        }

        override fun onAnimationStart(p0: Animation?) {

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(INSTANCE_STATE_CONFIG, mConfiguration)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_lock_screen_pf, container,
            false
        )

        if (mConfiguration == null) {
            mConfiguration = savedInstanceState!!.getSerializable(
                INSTANCE_STATE_CONFIG
            ) as PFFLockScreenConfiguration?
        }

        mFingerprintButton = view.findViewById(R.id.button_left)
        mDeleteButton = view.findViewById(R.id.button_right)
        mForgetButton = view.findViewById(R.id.title_text_view_forget)
        (mForgetButton as TextView).paintFlags = (mForgetButton as TextView).paintFlags or Paint.UNDERLINE_TEXT_FLAG

        mDeleteButton!!.setOnClickListener(mOnDeleteButtonClickListener)
        mDeleteButton!!.setOnLongClickListener(mOnDeleteButtonOnLongClickListener)
        mFingerprintButton!!.setOnClickListener(mOnFingerprintClickListener)
        mForgetButton!!.setOnClickListener(mOnForgetClickListener)


        mCodeView = view.findViewById(R.id.code_view)
        initKeyViews(view)

        mCodeView!!.setListener(mCodeListener)
        mFingerprintHardwareDetected = isFingerprintApiAvailable(context)
        mRootView = view
        applyConfiguration(mConfiguration)

        return view
    }

    fun setConfiguration(configuration: PFFLockScreenConfiguration) {
        this.mConfiguration = configuration
        applyConfiguration(configuration)
    }

    private fun applyConfiguration(configuration: PFFLockScreenConfiguration?) {
        if (mRootView == null || configuration == null) {
            return
        }
        val titleView = mRootView!!.findViewById<TextView>(R.id.title_text_view)
        titleView.text = configuration.getTitle()
        if (isEmpty(mCodeView!!.getCode())) {
            mDeleteButton!!.visibility = View.GONE
        } else {
            mDeleteButton!!.visibility = View.VISIBLE
        }

        mUseFingerPrint = configuration.isUseFingerprint()
        if (mUseFingerPrint && mFingerprintHardwareDetected) {
            mFingerprintButton!!.visibility = View.VISIBLE
        } else {
            mFingerprintButton!!.visibility = View.GONE
        }

        mIsCreateMode = mConfiguration!!.getMode() == PFFLockScreenConfiguration.MODE_CREATE
        mIsConfirmMode = mConfiguration!!.getMode() == PFFLockScreenConfiguration.MODE_CONFIRM

        if (mIsCreateMode || mIsConfirmMode) {
            mDeleteButton!!.visibility = View.GONE
            mFingerprintButton!!.visibility = View.GONE
        }
        mCodeView!!.setCodeLength(mConfiguration!!.getCodeLength())
    }

    private fun initKeyViews(parent: View) {
        parent.findViewById<AppCompatTextView>(R.id.button_0)
            .setOnClickListener(mOnKeyClickListener)
        parent.findViewById<AppCompatTextView>(R.id.button_1)
            .setOnClickListener(mOnKeyClickListener)
        parent.findViewById<AppCompatTextView>(R.id.button_2)
            .setOnClickListener(mOnKeyClickListener)
        parent.findViewById<AppCompatTextView>(R.id.button_3)
            .setOnClickListener(mOnKeyClickListener)
        parent.findViewById<AppCompatTextView>(R.id.button_4)
            .setOnClickListener(mOnKeyClickListener)
        parent.findViewById<AppCompatTextView>(R.id.button_5)
            .setOnClickListener(mOnKeyClickListener)
        parent.findViewById<AppCompatTextView>(R.id.button_6)
            .setOnClickListener(mOnKeyClickListener)
        parent.findViewById<AppCompatTextView>(R.id.button_7)
            .setOnClickListener(mOnKeyClickListener)
        parent.findViewById<AppCompatTextView>(R.id.button_8)
            .setOnClickListener(mOnKeyClickListener)
        parent.findViewById<AppCompatTextView>(R.id.button_9)
            .setOnClickListener(mOnKeyClickListener)
    }

    private fun configureLeftButton(codeLength: Int) {
        if (mIsCreateMode || mIsConfirmMode) {
            if (codeLength > 0) {
                mDeleteButton!!.visibility = View.VISIBLE
            } else {
                mDeleteButton!!.visibility = View.GONE
            }
            return
        }

        if (codeLength > 0) {
            mDeleteButton!!.visibility = View.VISIBLE
        } else {
            mDeleteButton!!.visibility = View.GONE
        }

        if (mUseFingerPrint && mFingerprintHardwareDetected) {
            mFingerprintButton!!.visibility = View.VISIBLE
        } else {
            mFingerprintButton!!.visibility = View.GONE
        }

    }

    private fun isFingerprintApiAvailable(context: Context?): Boolean {
        return isHardwareSupported(context!!)
    }

    private fun isFingerprintsExists(context: Context?): Boolean {
        return isFingerprintAvailable(context!!)
    }


    private fun showNoFingerprintDialog(context: Context?) {
        AlertDialog.Builder(context)
            .setTitle(R.string.no_fingerprints_title_pf)
            .setMessage(R.string.no_fingerprints_message_pf)
            .setCancelable(true)
            .setNegativeButton(R.string.cancel_pf, null)
            .setPositiveButton(R.string.settings_pf) { _, _ ->
                startActivity(
                    Intent(Settings.ACTION_SECURITY_SETTINGS)
                )
            }.create().show()
    }

    private fun showNotSupportFingerprintDialog(context: Context?) {
        AlertDialog.Builder(context)
            .setTitle(R.string.not_support_fingerprint_title)
            .setMessage(R.string.not_support_fingerprint_message)
            .setCancelable(true)
            .setPositiveButton(R.string.done_pf) { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }


    private fun deleteEncodeKey() {
        mPFPinCodeViewModel.delete().observe(
            this,
            Observer<PFResult<Boolean>> { result ->
                if (result == null) {
                    return@Observer
                }
                if (result.mError != null) {
                    Log.d(TAG, "Can not delete the alias")
                    return@Observer
                }
            }
        )
    }

    private fun errorAction() {
        if (mConfiguration!!.isErrorVibration()) {
            val v = context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                v.vibrate(500)
            }
        }

        if (mConfiguration!!.isErrorAnimation()) {
            val animShake = AnimationUtils.loadAnimation(context, R.anim.shake_pf)
            animShake.setAnimationListener(animationListener)
            mCodeView!!.startAnimation(animShake)
        }
    }

    fun setCodeCreateListener(listener: OnPFLockScreenCodeCreateListener) {
        mCodeCreateListener = listener
    }

    fun setCodeConfirmListener(listener: OnPFLockScreenCodeConfirmListener) {
        mCodeConfirmListener = listener
    }

    fun setLoginListener(listener: PFLoginListener) {
        mLoginListener = listener
    }

    fun setEncodedPinCode(encodedPinCode: String) {
        mEncodedPinCode = encodedPinCode
    }


    interface OnPFLockScreenCodeCreateListener {
        fun onCodeCreated(encodedCode: String)
    }

    interface OnPFLockScreenCodeConfirmListener {
        fun onCodeConfirm(isConfirm: Boolean)
    }

    companion object {
        private val TAG = PFLockScreenFragment::class.java.name
        private const val INSTANCE_STATE_CONFIG =
            "th.co.cdgs.mobile.biomatric.instance_state_config"
    }

    fun initialFingerprint(activity: FragmentActivity) {
        biometricManager = BiometricManager.BiometricBuilder(activity)
            .setTitle(mConfiguration!!.getFingerprintTitle())
            .setSubtitle(mConfiguration!!.getFingerprintSubtitle())
            .setDescription(mConfiguration!!.getFingerprintDescription())
            .setNegativeButtonText(mConfiguration!!.getFingerprintNegativeButtonText())
            .build()

        if (!mIsCreateMode && !mIsConfirmMode && mUseFingerPrint && mConfiguration!!.isAutoShowFingerprint() &&
            isFingerprintApiAvailable(activity) && isFingerprintsExists(activity)
        ) {
            getFingerprintAuthenticate(activity)
        } else if (!mIsCreateMode && !mIsConfirmMode && mUseFingerPrint && mConfiguration!!.isAutoShowFingerprint() &&
            !isFingerprintsExists(activity) && !isFingerprintApiAvailable(activity)
        ) {
            showNotSupportFingerprintDialog(activity)
        } else if (!mIsCreateMode && !mIsConfirmMode && mUseFingerPrint && mConfiguration!!.isAutoShowFingerprint() && !isFingerprintsExists(
                activity
            )
        ) {
            showNoFingerprintDialog(activity)
        }
    }

    private fun getFingerprintAuthenticate(context: Context?) {
        if (!mIsCreateMode && !mIsConfirmMode && mUseFingerPrint && isFingerprintApiAvailable(
                context
            ) && isFingerprintsExists(
                context
            )
        ) {
            biometricManager!!.authenticate(biometricCallback)
        } else if (!mIsCreateMode && !mIsConfirmMode && mUseFingerPrint && !isFingerprintsExists(
                context
            ) && !isFingerprintApiAvailable(
                context
            )
        ) {
            showNotSupportFingerprintDialog(context)
        } else if (!mIsCreateMode && !mIsConfirmMode && mUseFingerPrint && isFingerprintApiAvailable(
                context
            ) && !isFingerprintsExists(
                context
            )
        ) {
            showNoFingerprintDialog(context)
        }
    }
}