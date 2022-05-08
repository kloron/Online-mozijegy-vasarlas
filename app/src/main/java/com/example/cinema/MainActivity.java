package com.example.cinema;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;
    private static final int RC_SIGN_IN = 123;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    int tag = 1;
    EditText userNameET;
    EditText passwordET;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    Button b,c, d, e;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();

        userNameET = findViewById(R.id.editTextUserName);
        passwordET =findViewById(R.id.editTextPassword);

        preferences= getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        getSupportLoaderManager().restartLoader(0, null, this);
        Log.i(LOG_TAG, "onCreate");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(LOG_TAG, "Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(LOG_TAG, "signInWithCredential:success");
                            goShopping();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    public void login(View view) { ;

        String userName = userNameET.getText().toString();
        String password = passwordET.getText().toString();
        d=(Button)findViewById(R.id.loginButton);

        mAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(this, task -> {
            d.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Animation animation= AnimationUtils.loadAnimation(MainActivity.this,R.anim.slide_in_row);

                    d.startAnimation(animation);
                    return;

                }
            });
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "User loged in successfully");

                goShopping();
            } else {
                Log.d(LOG_TAG, "User log in fail");
                Toast.makeText(MainActivity.this, "User log in fail: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void register(View view) {
        e=(Button)findViewById(R.id.registerButton);
        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation= AnimationUtils.loadAnimation(MainActivity.this,R.anim.slide_in_row);

                e.startAnimation(animation);

            }
        });
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);

        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.i(LOG_TAG, "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName",userNameET.getText().toString() );
        editor.putString("password", passwordET.getText().toString());
        editor.apply();


        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "onResume");
    }

    public void loginAsGuest(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "Anonym user loged in successfully");
                b=(Button)findViewById(R.id.guestLoginButton);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation animation= AnimationUtils.loadAnimation(MainActivity.this,R.anim.bounce);

                        b.startAnimation(animation);

                    }
                });
                goShopping();
            } else {
                Log.d(LOG_TAG, "Anonym user log in fail");
                Toast.makeText(MainActivity.this, "User log in fail: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loginWithGoogle(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        c=(Button)findViewById(R.id.googleSignInButton);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation= AnimationUtils.loadAnimation(MainActivity.this,R.anim.bounce);

                c.startAnimation(animation);

            }
        });
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void goShopping(){
        Intent intent = new Intent(this, ShopListActivity.class);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new RandomLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Button anonym = findViewById(R.id.guestLoginButton);
        anonym.setText(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    public void openCamera(View view) {
        checkUserPermission();
    }
    void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        takePicture();
    }
    private void takePicture() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, tag);
    }
}