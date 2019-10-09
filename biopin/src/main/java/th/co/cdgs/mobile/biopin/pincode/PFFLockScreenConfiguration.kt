package th.co.cdgs.mobile.biopin.pincode

import android.content.Context
import androidx.annotation.IntDef
import th.co.cdgs.mobile.biopin.R
import java.io.Serializable


class PFFLockScreenConfiguration private constructor(builder: Builder): Serializable {

    companion object {
        const val MODE_CREATE = 0
        const val MODE_AUTH = 1
        const val MODE_CONFIRM = 2
    }

    private var mNextButton = ""
    private var mUseFingerprint = false
    private var mAutoShowFingerprint = false
    private var mTitle = ""
    private var mMode =
        MODE_AUTH
    private var mCodeLength = 4
    private var mClearCodeOnError = false
    private var mErrorVibration = true
    private var mErrorAnimation = true

    private var mFingerprintTitle = ""
    private var mFingerprintSubtitle = ""
    private var mFingerprintDescription = ""
    private var mFingerprintNegativeButtonText = ""

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        MODE_CREATE,
        MODE_AUTH,
        MODE_CONFIRM
    )
    annotation class PFLockScreenMode

    init {
        mNextButton = builder.mNextButton
        mUseFingerprint = builder.mUseFingerprint
        mAutoShowFingerprint = builder.mAutoShowFingerprint
        mTitle = builder.mTitle
        mMode = builder.mMode
        mCodeLength = builder.mCodeLength
        mClearCodeOnError = builder.mClearCodeOnError
        mErrorVibration = builder.mErrorVibration
        mErrorAnimation = builder.mErrorAnimation
        mFingerprintTitle = builder.mFingerprintTitle
        mFingerprintSubtitle = builder.mFingerprintSubtitle
        mFingerprintDescription = builder.mFingerprintDescription
        mFingerprintNegativeButtonText = builder.mFingerprintNegativeButtonText
    }

    fun getNextButton(): String {
        return mNextButton
    }

    fun isUseFingerprint(): Boolean {
        return mUseFingerprint
    }

    fun isAutoShowFingerprint(): Boolean {
        return mAutoShowFingerprint
    }

    fun getTitle(): String {
        return mTitle
    }

    fun getCodeLength(): Int {
        return mCodeLength
    }

    fun isClearCodeOnError(): Boolean {
        return mClearCodeOnError
    }

    fun isErrorVibration(): Boolean {
        return mErrorVibration
    }

    fun isErrorAnimation(): Boolean {
        return mErrorAnimation
    }

    fun getFingerprintTitle(): String {
        return mFingerprintTitle
    }
    fun getFingerprintSubtitle(): String {
        return mFingerprintSubtitle
    }
    fun getFingerprintDescription(): String {
        return mFingerprintDescription
    }
    fun getFingerprintNegativeButtonText(): String {
        return mFingerprintNegativeButtonText
    }

    @PFLockScreenMode
    fun getMode(): Int {
        return this.mMode
    }

    class Builder(context: Context) {

        internal var mNextButton = ""
        internal var mUseFingerprint = false
        internal var mAutoShowFingerprint = false
        internal var mTitle = ""
        internal var mMode = 0
        internal var mCodeLength = 4
        internal var mClearCodeOnError = false
        internal var mErrorVibration = true
        internal var mErrorAnimation = true

        internal var mFingerprintTitle = ""
        internal var mFingerprintSubtitle = ""
        internal var mFingerprintDescription = ""
        internal var mFingerprintNegativeButtonText = ""


        init {
            mTitle = context.resources.getString(R.string.lock_screen_title_pf)
        }

        fun setTitle(title: String): Builder {
            mTitle = title
            return this
        }

        fun setNextButton(nextButton: String): Builder {
            mNextButton = nextButton
            return this
        }

        fun setUseFingerprint(useFingerprint: Boolean): Builder {
            mUseFingerprint = useFingerprint
            return this
        }

        fun setAutoShowFingerprint(autoShowFingerprint: Boolean): Builder {
            mAutoShowFingerprint = autoShowFingerprint
            return this
        }

        fun setMode(@PFLockScreenMode mode: Int): Builder {
            mMode = mode
            return this
        }

        fun setCodeLength(codeLength: Int): Builder {
            this.mCodeLength = codeLength
            return this
        }

        fun setClearCodeOnError(clearCodeOnError: Boolean): Builder {
            mClearCodeOnError = clearCodeOnError
            return this
        }

        fun setErrorVibration(errorVibration: Boolean): Builder {
            mErrorVibration = errorVibration
            return this
        }

        fun setErrorAnimation(errorAnimation: Boolean): Builder {
            mErrorAnimation = errorAnimation
            return this
        }

        fun setFingerprintTitle(fingerprintTitle :String): Builder {
            mFingerprintTitle = fingerprintTitle
            return this
        }
        fun setFingerprintSubtitle(fingerprintSubtitle :String): Builder {
            mFingerprintSubtitle = fingerprintSubtitle
            return this
        }
        fun setFingerprintDescription(fingerprintDescription :String): Builder {
            mFingerprintDescription = fingerprintDescription
            return this
        }
        fun setFingerprintNegativeButtonText(fingerprintNegativeButtonText :String): Builder {
            mFingerprintNegativeButtonText = fingerprintNegativeButtonText
            return this
        }

        fun build(): PFFLockScreenConfiguration {
            return PFFLockScreenConfiguration(
                this
            )
        }


    }
}