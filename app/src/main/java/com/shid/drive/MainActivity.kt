package com.shid.drive

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import dmax.dialog.SpotsDialog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.functions.Functions
import retrofit2.Retrofit
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var listener: FirebaseAuth.AuthStateListener
    private lateinit var dialog: android.app.AlertDialog
    private val compositeDisposable = CompositeDisposable()
    private lateinit var cloudFunctions: ICloudFunctions

    private lateinit var userRef: DatabaseReference
    private var providers: List<AuthUI.IdpConfig>? = null

    companion object {
        private val APP_REQUEST_CODE = 7171
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener { listener }

    }

    override fun onStop() {
        if (listener != null){
            firebaseAuth.removeAuthStateListener { listener }

        }
        compositeDisposable.clear()
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        providers = Arrays.asList<AuthUI.IdpConfig>(AuthUI.IdpConfig.PhoneBuilder().build())

        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE)//check Common
        firebaseAuth = FirebaseAuth.getInstance()
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        cloudFunctions = RetrofitCloudClient.getInstance().create(ICloudFunctions::class.java)
        listener = FirebaseAuth.AuthStateListener{
            firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null){
                checkUserFromFirebase(user!!)
            }else{
                phoneLogin()
            }
        }
    }

    private fun phoneLogin() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers!!).build(),APP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_REQUEST_CODE){
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
            } else{
                Toast.makeText(this,"Failed to sign in",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun checkUserFromFirebase(user: FirebaseUser) {

    }
}