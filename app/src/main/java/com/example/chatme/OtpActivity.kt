package com.example.chatme

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

const val PHONE_NUMBER = "phoneNumber"


@Suppress("DEPRECATION")
class OtpActivity : AppCompatActivity(), View.OnClickListener {

    var phoneNumber : String? = null

    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var mVerificationid : String? = null
    var mResendingToken : PhoneAuthProvider.ForceResendingToken? = null

    lateinit var progressDialog: ProgressDialog




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        initViews()

        startVerify()
    }

    private fun startVerify() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber!!,
                60,
                TimeUnit.SECONDS,
                this,
                callbacks)

        showTimer(60000)

        progressDialog = createProgressDialog("Sending a verification Code", false)
        progressDialog.show()
    }

    private fun showTimer(millis: Long) {
        resendBtn.isEnabled = false
        object : CountDownTimer(millis,1000){
            override fun onTick(millisUntilFinished: Long) {
                counterTv.isVisible =true
                counterTv.text = getString(R.string.second_remain,millisUntilFinished/1000)
            }

            override fun onFinish() {
                resendBtn.isEnabled = true
                counterTv.isVisible = false
            }
        }.start()
    }

    private fun initViews() {
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        verifyTv.text = getString(R.string.verify_number,phoneNumber)
        setSpannableString()

        verificationBtn.setOnClickListener(this)
        resendBtn.setOnClickListener(this)


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("Tag3", "onVerificationCompleted:$credential")

                if (::progressDialog.isInitialized){
                    progressDialog.dismiss()
                }

                val smsCode =credential.smsCode
                if (!smsCode.isNullOrBlank()){
                    sentcodeEt.setText(smsCode)
                }

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("Tag1", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                notifyUserAndRetry("Your Phone Number might be wrong or connection error . Retry again!")

                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("Tag2", "onCodeSent:$verificationId")

                progressDialog.dismiss()
                counterTv.isVisible=false

                // Save verification ID and resending token so we can use them later
                mVerificationid= verificationId
                mResendingToken = token

                // ...
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it. isSuccessful){
                        startActivity(Intent(this,SignUpActivity::class.java))
                        finish()
                    }else{
                        notifyUserAndRetry("Your Phone Number Verification failed . Retry again!")
                    }
                }
    }

    private fun notifyUserAndRetry(s: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(s)
            setPositiveButton("Ok") { _, _ ->
                showLoginActivity()
            }

            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            setCancelable(false)
            create()
            show()
        }
    }


    private fun setSpannableString() {
        val span = SpannableString(getString(R.string.waiting_text,phoneNumber))
        val clickableSpan = object  : ClickableSpan(){
            override fun onClick(widget: View) {
               //send back
                showLoginActivity()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ds .linkColor
            }

        }
        span.setSpan(clickableSpan,span.length - 13 , span.length , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        waitingTv.movementMethod = LinkMovementMethod.getInstance()
        waitingTv.text = span

    }

    private fun showLoginActivity() {
        startActivity(Intent(this,LoginActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    override fun onClick(v: View?) {
        when(v){
            verificationBtn ->{


                val code= sentcodeEt.text.toString()
                if (code.isNotEmpty() && !mVerificationid.isNullOrBlank()){
                    progressDialog = createProgressDialog("Please wait ....",false)
                    progressDialog.show()
                    val credential = PhoneAuthProvider.getCredential(mVerificationid!!,code)
                    signInWithPhoneAuthCredential(credential)
                   // progressDialog.dismiss()
                }

            }
            resendBtn ->{

                if (mResendingToken != null){

                    showTimer(600000)
                    progressDialog = createProgressDialog("Sending the OTP",false)
                    progressDialog.show()

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber!!,
                            60,
                            TimeUnit.SECONDS,
                            this,
                            callbacks,
                            mResendingToken)
                }

            }
        }
    }
}

fun Context.createProgressDialog(message: String, isCancelable : Boolean):ProgressDialog{
    return ProgressDialog(this).apply {
        setCancelable(isCancelable)
        setMessage(message)
        setCanceledOnTouchOutside(isCancelable)
    }
}