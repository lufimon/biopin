package th.co.cdgs.mobile.biopin.pincode.security

class PFResult<T> {

    var mError: PFSecurityError? = null
    var mResult: T? = null

    constructor(mError: PFSecurityError){
        this.mError = mError
    }

    constructor(result: T){
        this.mResult = result
    }
}