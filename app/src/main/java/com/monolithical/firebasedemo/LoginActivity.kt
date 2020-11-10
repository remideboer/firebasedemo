package com.monolithical.firebasedemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.monolithical.firebasedemo.databinding.ActivityLoginBinding

/**
 * Demo met google login
 * Maak hierna demo met FirebaseUI voor het makkelijk werken met meerdere providers
 * Zo min mogelijk refactored naar functies om de flow helder te houden
 */
class LoginActivity : AppCompatActivity() {

    companion object {
        // je random identifier: wordt gebruikt bij de callback activity result
        private const val RC_SIGN_IN = 1234 // blijf binnen de 16bits....
    }

    // 1 FirebaseAuth object om oa user info vanaf te halen
    private lateinit var auth: FirebaseAuth

    // 4 is een tweede dependency, zelfde package, maar in play-services-auth.....
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set up viewbinding (niet in module gradle vergeten aan te zetten)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 2 get firebase instantie: hier zit of een user in of null
        auth = FirebaseAuth.getInstance()

        // 3: SignIn configuratie (kan ook eerder)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // for the requestIdToken, this is in the values.xml file that
            // is generated from your google-services.json (aka webclientId)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // 4: listener om de google signin flow te starten
        binding.btnGoogleSignin.setOnClickListener {
            // doe het signin ding
            // hebben signin nodig om het google account te selecteren
            // dit start de selectie activiteit en het resultaat vang je op
            // met behulp van de onActivityResult callback
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)

        }
    }

    // 5: check on start of er een ingelogde gebruiker bij Firebase auth object is
    override fun onStart() {
        super.onStart()
        // check of gebruiker is ingelogd, zo ja
        val user = auth.currentUser
        if (user != null) {
            // user in ingelogd ga naar MainActivity
            // anders hier blijven
            // explicit intent nodig: kan natuurlijk naar een apatre functie want na inlog wordt dit ook gebruikt
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // ! finish de LoginActivity anders blijft deze op de stack staan
            finish()
        }
    }

    // 6: als de google signin flow: activity klaar is, komt de account data met een idToken terug
    // 7: deze idToken wordt vervolgens gebruikt om de daadwerkelijke signin te doen
    // en user gegevens terug te krijgen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // controleer of we een requestcode hebben die bij de eerder opgestarte Activity hoort
        // zo ja, dan kunnen we deze afhandelen
        // wat terugkomt is een Task
        if (requestCode == RC_SIGN_IN) {
            Log.d("LoginActivity", "onActivityResult: data ${data}");
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            // trying omdat dingen kunnen falen
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("LoginActivity", "onActivityResult: account token ${account?.idToken}");
                // gebruik id uit account om daadwerkleijk in te loggen
                // zou eventueel null kunnen zijn
                account?.idToken?.let { token ->
                    val cred = GoogleAuthProvider.getCredential(token, null)
                    // gebruik nu Firebaser auth object om in te loggen
                    // en gebruik een oncomplete listener om succesvol resultaat op te vangen
                    // dwz naar MainActivity doorgaan
                    auth.signInWithCredential(cred)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("LoginActivity", "onActivityResult:authentication succes!")
                                val user = auth.currentUser
                                Log.d("LoginActivity", "onActivityResult:user is: $user")
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.d("LoginActivity", "onActivityResult:auth failed!")
                                Toast.makeText(
                                    this,
                                    "Authentication failed: try again",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            } catch (e: ApiException) {
                //
                Log.d("LoginActivity", "onActivityResult: getting credentials failed ${e.message}")
                e.printStackTrace()
                Toast.makeText(this, "Google Signin failed", Toast.LENGTH_LONG).show()
            }

        }
    }


}