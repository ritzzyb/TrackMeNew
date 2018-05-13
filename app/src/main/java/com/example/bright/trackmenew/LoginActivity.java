package com.example.bright.trackmenew;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private int RC_SIGN_IN = 100;
    private Context context;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance ();
        googleSignInOptions = new GoogleSignInOptions.Builder (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken (getString (R.string.default_web_client_id))
                .requestEmail ()
                .build ();
        googleSignInClient = GoogleSignIn.getClient (this, googleSignInOptions);
        context = this;
    }

    @Override
    protected void onStart () {
        super.onStart ();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser ();
        if (firebaseUser != null) {
            Toast.makeText (this, "Welcome " + firebaseAuth.getCurrentUser ().getEmail () + " !", Toast.LENGTH_SHORT).show ();
            Intent intent = new Intent (this, MainActivity.class);
            startActivity (intent);
            finish ();
        } else {
            Toast.makeText (this, "You haven't logged in yet ...", Toast.LENGTH_SHORT).show ();
        }
    }

    public void signIn (View view) {
        Intent intent = googleSignInClient.getSignInIntent ();
        startActivityForResult (intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent (data);
            task.addOnCompleteListener (new OnCompleteListener<GoogleSignInAccount> () {
                @Override
                public void onComplete (@NonNull Task<GoogleSignInAccount> task) {
                    try {
                        firebaseAuthWithGoogle (task.getResult ());
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                }
            });
            task.addOnFailureListener (new OnFailureListener () {
                @Override
                public void onFailure (@NonNull Exception e) {
                    Toast.makeText (context, "Google sign in failed", Toast.LENGTH_SHORT).show ();
                }
            });
        }
    }

    private void firebaseAuthWithGoogle (GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential (acct.getIdToken (), null);
        firebaseAuth.signInWithCredential (credential)
                .addOnCompleteListener (this, new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete (@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful ()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser ();
                            if (firebaseUser != null) {
                                Toast.makeText (context, "Welcome " + firebaseAuth.getCurrentUser ().getEmail () + " !", Toast.LENGTH_SHORT).show ();
                                Intent intent = new Intent (context, MainActivity.class);
                                startActivity (intent);
                                finish ();
                            } else {
                                Toast.makeText (context, "You haven't logged in yet ...", Toast.LENGTH_SHORT).show ();
                            }
                        }
                    }
                });
    }
}
