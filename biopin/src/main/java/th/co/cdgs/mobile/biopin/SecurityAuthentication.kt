package th.co.cdgs.mobile.biopin

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import th.co.cdgs.mobile.biopin.pincode.PFFLockScreenConfiguration
import th.co.cdgs.mobile.biopin.pincode.PFLockScreenFragment
import th.co.cdgs.mobile.biopin.pincode.security.PFLoginListener
import th.co.cdgs.mobile.biopin.pincode.security.PFResult
import th.co.cdgs.mobile.biopin.pincode.viewmodels.PFPinCodeViewModel
import th.co.cdgs.mobile.biopin.settings.PreferencesSettings
import java.io.Serializable

class SecurityAuthentication private constructor(builder: Builder): Serializable {

    private var activity: AppCompatActivity? = null
    @IdRes
    private var containerView: Int? = null
    private var mLoginListener: PFLoginListener? = null
    private var pinLength = 4
    private var titleCreateCode = ""
    private var createButton = ""
    private var titleCreateConfirm = ""
    private var createConfirmButton = ""
    private var titleAuth = ""
    private var useFingerprint = false
    private var autoFingerprint = false
    private var fingerprintTitle = ""
    private var fingerprintSubtitle = ""
    private var fingerprintDescription = ""
    private var fingerprintNegativeButtonText = ""

    init {
        activity = builder.activity
        containerView = builder.containerView
        mLoginListener = builder.mLoginListener
        pinLength = builder.pinLength
        titleCreateCode = builder.titleCreateCode
        createButton = builder.createButton
        titleCreateConfirm = builder.titleCreateConfirm
        createConfirmButton = builder.createConfirmButton
        titleAuth = builder.titleAuth
        useFingerprint = builder.useFingerprint
        autoFingerprint = builder.autoFingerprint
        fingerprintTitle = builder.fingerprintTitle
        fingerprintSubtitle = builder.fingerprintSubtitle
        fingerprintDescription = builder.fingerprintDescription
        fingerprintNegativeButtonText = builder.fingerprintNegativeButtonText
    }

    fun create(){
        showLockScreenFragment()
    }

    private val mCodeCreateListener =
        object : PFLockScreenFragment.OnPFLockScreenCodeCreateListener {
            override fun onCodeCreated(encodedCode: String) {
                PreferencesSettings.saveToPref(activity!!.applicationContext, encodedCode)
                showLockScreenConfirm()
            }
        }

    private val mCodeConfirmListener =
        object : PFLockScreenFragment.OnPFLockScreenCodeConfirmListener {
            override fun onCodeConfirm(isConfirm: Boolean) {
                if (isConfirm) {
                    showLockScreenAuth()
                } else {
                    showLockScreenCreate()
                }
            }
        }

    private fun showLockScreenFragment() {
        PFPinCodeViewModel().isPinCodeEncryptionKeyExist().observe(
            activity!!,
            Observer<PFResult<Boolean>> { result ->
                if (result == null) {
                    return@Observer
                }
                if (result.mError != null) {
                    return@Observer
                }
                if (result.mResult!!) {
                    showLockScreenAuth()
                } else {
                    showLockScreenCreate()
                }
            }
        )
    }

    private fun showLockScreenCreate() {
        val fragment = PFLockScreenFragment()
        val builder = PFFLockScreenConfiguration.Builder(activity!!.applicationContext)
            .setTitle(titleCreateCode)
            .setNextButton(createButton)
            .setCodeLength(pinLength)
            .setMode(PFFLockScreenConfiguration.MODE_CREATE)
        fragment.setConfiguration(builder.build())
        fragment.setCodeCreateListener(mCodeCreateListener)
        activity!!.supportFragmentManager.beginTransaction().replace(containerView!!, fragment)
            .commit()
    }

    private fun showLockScreenConfirm() {
        val fragment = PFLockScreenFragment()
        val builder = PFFLockScreenConfiguration.Builder(activity!!.applicationContext)
            .setTitle(titleCreateConfirm)
            .setNextButton(createConfirmButton)
            .setCodeLength(pinLength)
            .setMode(PFFLockScreenConfiguration.MODE_CONFIRM)
        fragment.setConfiguration(builder.build())
        fragment.setCodeConfirmListener(mCodeConfirmListener)
        activity!!.supportFragmentManager.beginTransaction().replace(containerView!!, fragment)
            .commit()
    }

    private fun showLockScreenAuth() {
        val fragment = PFLockScreenFragment()

        val builder = PFFLockScreenConfiguration.Builder(activity!!.applicationContext)
            .setTitle(titleAuth)
            .setCodeLength(pinLength)
            .setMode(PFFLockScreenConfiguration.MODE_AUTH)
            .setUseFingerprint(useFingerprint)
            .setAutoShowFingerprint(autoFingerprint)
            .setFingerprintTitle(fingerprintTitle)
            .setFingerprintSubtitle(fingerprintSubtitle)
            .setFingerprintDescription(fingerprintDescription)
            .setFingerprintNegativeButtonText(fingerprintNegativeButtonText)

        fragment.setEncodedPinCode(PreferencesSettings.getCode(activity!!.applicationContext))
        if (mLoginListener != null) {
            fragment.setLoginListener(mLoginListener!!)
        }
        fragment.setConfiguration(builder.build())
        activity!!.supportFragmentManager.beginTransaction().replace(containerView!!, fragment)
            .commit()
        fragment.initialFingerprint(activity!!)
    }

    class Builder(activity: AppCompatActivity) {

        internal var activity: AppCompatActivity? = null
        @IdRes
        internal var containerView: Int? = null
        internal var mLoginListener: PFLoginListener? = null
        internal var pinLength = 4
        internal var titleCreateCode = ""
        internal var createButton = ""
        internal var titleCreateConfirm = ""
        internal var createConfirmButton = ""
        internal var titleAuth = ""
        internal var useFingerprint = false
        internal var autoFingerprint = false
        internal var fingerprintTitle = ""
        internal var fingerprintSubtitle = ""
        internal var fingerprintDescription = ""
        internal var fingerprintNegativeButtonText = ""

        init {
            this.activity = activity
            this.titleCreateCode = activity.getString(R.string.title_create_pf)
            this.createButton = activity.getString(R.string.button_create_pf)
            this.titleCreateConfirm = activity.getString(R.string.title_confirm_pf)
            this.createConfirmButton = activity.getString(R.string.button_confirm_pf)
            this.titleAuth = activity.getString(R.string.title_auth_pf)
            this.fingerprintTitle = activity.getString(R.string.biometric_title)
            this.fingerprintSubtitle = activity.getString(R.string.biometric_subtitle)
            this.fingerprintDescription = activity.getString(R.string.biometric_description)
            this.fingerprintNegativeButtonText = activity.getString(R.string.biometric_negative_button_text)
        }

        fun setContainerView(@IdRes containerView: Int): Builder {
            this.containerView = containerView
            return this
        }

        fun setLoginListenerr(mLoginListener: PFLoginListener): Builder {
            this.mLoginListener = mLoginListener
            return this
        }

        fun setPinLength(pinLength: Int): Builder {
            this.pinLength = pinLength
            return this
        }

        fun setTitleCreateCode(titleCreateCode: String): Builder {
            this.titleCreateCode = titleCreateCode
            return this
        }
        fun setCreateButton(createButton: String): Builder {
            this.createButton = createButton
            return this
        }
        fun setTitleCreateConfirm(titleCreateConfirm: String): Builder {
            this.titleCreateConfirm = titleCreateConfirm
            return this
        }
        fun setCreateConfirmButton(createConfirmButton: String): Builder {
            this.createConfirmButton = createConfirmButton
            return this
        }
        fun setTitleAuth(titleAuth: String): Builder {
            this.titleAuth = titleAuth
            return this
        }
        fun setUseFingerprint(useFingerprint: Boolean): Builder {
            this.useFingerprint = useFingerprint
            return this
        }
        fun setAutoFingerprint(autoFingerprint: Boolean): Builder {
            this.autoFingerprint = autoFingerprint
            return this
        }
        fun setFingerprintTitle(fingerprintTitle: String): Builder {
            this.fingerprintTitle = fingerprintTitle
            return this
        }
        fun setFingerprintSubtitle(fingerprintSubtitle: String): Builder {
            this.fingerprintSubtitle = fingerprintSubtitle
            return this
        }
        fun setFingerprintDescription(fingerprintDescription: String): Builder {
            this.fingerprintDescription = fingerprintDescription
            return this
        }
        fun setFingerprintNegativeButtonText(fingerprintNegativeButtonText: String): Builder {
            this.fingerprintNegativeButtonText = fingerprintNegativeButtonText
            return this
        }

        fun build(): SecurityAuthentication {
            return SecurityAuthentication(this)
        }
    }
}