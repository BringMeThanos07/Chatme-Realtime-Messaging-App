package com.example.chatme

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.properties.Delegates


class LoginActivity : AppCompatActivity() {

    private lateinit var phoneNumber:String
    private lateinit var countryCode:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //add Hint Request for phone number

        //requestHint()

        phoneNumberEt.addTextChangedListener {
            nextBtn.isEnabled = !(it.isNullOrEmpty() || it.length < 10) //no. length checker

        }

        nextBtn.setOnClickListener{
            checkNumber()
        }
    }



    private fun checkNumber() {
        countryCode = ccp.selectedCountryCodeWithPlus
        phoneNumber = countryCode + phoneNumberEt.text.toString()

        notifyUser()
    }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verifying the phone number : $phoneNumber\n" +
            "Is this OK, or would you like to edit the Number?")
            setPositiveButton("Ok"){ _,_ ->
                showOtpActivity()
            }
            setNegativeButton("Edit"){dialog,which ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showOtpActivity() {
        startActivity(Intent(this,OtpActivity::class.java).putExtra(PHONE_NUMBER,phoneNumber))
        finish()
    }
}