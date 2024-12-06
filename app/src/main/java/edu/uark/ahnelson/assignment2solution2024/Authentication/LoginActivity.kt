package edu.uark.ahnelson.assignment2solution2024.Authentication


import android.content.Intent
import edu.uark.ahnelson.assignment2solution2024.R
import android.content.ActivityNotFoundException
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.ktx.Firebase
import edu.uark.ahnelson.assignment2solution2024.ToDoListActivity.ToDoListActivity

class LoginActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private var showOneTapUI = true

    private val loginResultHandler = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result: ActivityResult ->
        // handle intent result here
        if(result.resultCode == RESULT_OK){
            Log.d(TAG, "RESULT_OK.")
        }
        if(result.resultCode == RESULT_CANCELED){
            Log.d(TAG, "RESULT_CANCELED.")
        }
        if (result.resultCode == RESULT_FIRST_USER){
            Log.d(TAG, "RESULT_FIRST_USER.")
        }
        try {
            val credential =
                oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            val username = credential.id
            val password = credential.password
            if (idToken != null) {
                // Got an ID token from Google. Use it to authenticate
                // with your backend.
                Log.d(TAG, "Got ID token.")
                logInWithGoogleToken(credential)
            } else if (password != null) {
                // Got a saved username and password. Use them to authenticate
                // with your backend.
                Log.d(TAG, "Got password.")
            }
        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Log.d(TAG, "One-tap dialog was closed.")
                    // Don't re-prompt the user.
                    showOneTapUI = false
                }
                CommonStatusCodes.NETWORK_ERROR ->
                    Log.d(TAG, "One-tap encountered a network error.")
                else ->
                    Log.d(TAG, "Couldn't get credential from result."
                            + e.localizedMessage)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
         setContentView(R.layout.login_activity)
        auth = Firebase.auth
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            loginUserWithUsernamePassword()
        }
        findViewById<Button>(R.id.btnSignUp).setOnClickListener {
            createUserWithUsernamePassword()
        }

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val launchSecondActivityIntent = Intent(this, ToDoListActivity::class.java).apply {
                putExtra("USER_ID", currentUser.uid)
            }
            startActivity(launchSecondActivityIntent)
            finish()
        }
    }

    private fun createOneTapSignIn(){
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.Builder().setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
            .setSupported(true)
            .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    // .setServerClientId(getString(R.string.your_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(false)
            .build()
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    loginResultHandler.launch(
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    )
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    Log.e(TAG, "Couldn't start One Tap UI: " + e.localizedMessage)
                }
            }
            .addOnFailureListener(this) { e -> // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.
                e.localizedMessage?.let { Log.d(TAG, it) }

            }
    }

    private fun loginUserWithUsernamePassword(){
        val email = findViewById<EditText>(R.id.etUsername).text.toString()
        val password = findViewById<EditText>(R.id.etPassword).text.toString()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val launchSecondActivityIntent = Intent(this, ToDoListActivity::class.java).apply {
                        putExtra("USER_ID", user?.uid)
                    }
                    startActivity(launchSecondActivityIntent)
                    finish()
                    Toast.makeText(this,"User signed in!",Toast.LENGTH_LONG).show()
                    Log.d(TAG,"User UUID:${auth.currentUser.toString()}")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun createUserWithUsernamePassword(){
        val email = findViewById<EditText>(R.id.etUsername).text.toString()
        val password = findViewById<EditText>(R.id.etPassword).text.toString()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val launchSecondActivityIntent = Intent(this, ToDoListActivity::class.java).apply {
                        putExtra("USER_ID", user?.uid)
                    }
                    startActivity(launchSecondActivityIntent)
                    finish()
                    Toast.makeText(this,"User signed in!",Toast.LENGTH_LONG).show()
                    Log.d(TAG,"User UUID:${auth.currentUser.toString()}")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun logInWithGoogleToken(credential: SignInCredential){
        val firebaseCredential = GoogleAuthProvider.getCredential(credential.googleIdToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //launchApplication()
                }
            }

    }
}