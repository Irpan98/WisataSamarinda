package id.itborneo.wisatasamarinda.ui.login

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import id.itborneo.wisatasamarinda.R

class GoogleLogin(private val fragment: LoginFragment) {

    companion object {
        private const val RC_SIGN_IN = 1
    }

    init {
        initFirebase()

    }

    private fun initFirebase() {
        mAuth = FirebaseAuth.getInstance()
    }

    private val TAG = "GoogleLoginClassKu"
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    fun initAuthGmail() {
        Log.d(TAG, "initAuthGmail:" + "")

        // Configure Google Sign In

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(fragment.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: (GoogleSignInAccount) -> Unit
    ) {

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

                Log.d(TAG, "firebaseAuthWithGoogle:" + account?.id)
                firebaseAuthWithGoogle(account, callback)

//                val personName = account?.displayName
//                val personGivenName = account?.givenName
//                val personFamilyName = account?.familyName
//                val personEmail = account?.email
//                val personId = account?.id
//                val personPhoto: Uri? = account?.photoUrl
//
//
////                Log.d(TAG, "acct $personName $personGivenName $personPhoto")

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(
                    fragment.context,
                    "Google Sign In Failed: $e", Toast.LENGTH_LONG
                ).show()
                // ... m
            }
        }
    }

    fun signIn() {
        Log.d(TAG, "signIn:" + "")

        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        fragment.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(
        gsi: GoogleSignInAccount?,
        callback: (GoogleSignInAccount) -> Unit
    ) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + "")

        val credential = GoogleAuthProvider.getCredential(gsi?.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                fragment.requireActivity()
            ) { task ->
                if (task.isSuccessful) {


                    gsi?.let { callback(it) }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)

                }

                // ...
            }
    }
}