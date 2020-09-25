package com.mathgeniusguide.quicknotes.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FacebookAuthProvider
import com.mathgeniusguide.quicknotes.MainActivity
import com.mathgeniusguide.quicknotes.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment: Fragment() {
    private val RC_SIGN_IN = 9001
    private val TAG = "QuickNotes"
    lateinit var act: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        act = activity as MainActivity

        // Initialize Facebook Login button
        act.callbackManager = CallbackManager.Factory.create()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // toolbar invisible and drawer locked
        act.toolbar.visibility = View.GONE
        act.drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        googleBU.setOnClickListener {
            startActivityForResult(
                Auth.GoogleSignInApi.getSignInIntent(act.googleApiClient),
                RC_SIGN_IN
            )
        }

        facebookBU.setReadPermissions("email", "public_profile")
        facebookBU.registerCallback(act.callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                // ...
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        act.firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(act) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    act.login(act.firebaseAuth.currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}