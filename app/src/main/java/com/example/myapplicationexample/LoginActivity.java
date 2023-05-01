package com.example.myapplicationexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    //Google sign up
    FloatingActionButton google_signup;
    FirebaseDatabase database;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    private PreferenceManager preferenceManager;

//    private DatabaseHelper DatabaseHelper;
    private TextView mTvLoginactivityRegister;
    private EditText mEtLoginactivityUsername;
    private EditText mEtLoginactivityPassword;
    private Button mBtLoginactivityLogin;

    @Override
    //Keep Login Status
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        preferenceManager = new PreferenceManager(getApplicationContext());

//        DatabaseHelper = new DatabaseHelper(this);
    }
//    Just For Test
//    private void addDataToFirestore(){
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("first_name", "Weijie");
//        data.put("last_name", "QIU");
//        database.collection("users")
//                .add(data)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getApplicationContext(), "Data Inserted", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(exception -> {
//                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }

    private void initView() {

        // Initializing controls
        progressBar = findViewById(R.id.progressBar);


        mBtLoginactivityLogin = findViewById(R.id.loginactivity_btn_login);
        mTvLoginactivityRegister = findViewById(R.id.loginactivity_tv_register);
        mEtLoginactivityUsername = findViewById(R.id.loginactivity_et_username);
        mEtLoginactivityPassword = findViewById(R.id.loginactivity_et_password);

        mAuth = FirebaseAuth.getInstance();
        google_signup=findViewById(R.id.loginactivity_google);
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Create Account");
        //ProgressDialog.setMessage("Creating");

        GoogleSignInOptions
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this, gso);

        // Setting the click event listener
        mBtLoginactivityLogin.setOnClickListener(this);
        mTvLoginactivityRegister.setOnClickListener(this);

        google_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignIn();
            }
        });
    }

    int RC_SIGN_IN = 40;

    private void SignIn() {

        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
        //addDataToFirestore();

    }

    private  void SignInFirestore() {

        mEtLoginactivityUsername = findViewById(R.id.loginactivity_et_username);
        mEtLoginactivityPassword = findViewById(R.id.loginactivity_et_password);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, mEtLoginactivityUsername.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, mEtLoginactivityPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null
                        && task.getResult().getDocuments().size() >0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                        startActivity(intent);
                    }else {
                        progressBar.setVisibility(View.GONE);
                        showToast("Unable to Sign In");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuth(account.getIdToken());

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }

    private void firebaseAuth(String idToken) {

       AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

       mAuth.signInWithCredential(credential)
               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {

                       if (task.isSuccessful()){

                           FirebaseUser user = mAuth.getCurrentUser();

                           Users users = new Users();
                           users.setUserId(user.getUid());
                           users.setName(user.getDisplayName());
                           users.setProfile(user.getPhotoUrl().toString());

                           database.getReference().child("Users").child(user.getUid()).setValue(users);

                           Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                           startActivity(intent);
                           finish();

                       }
                       else {

                           Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();

                       }

                   }
               });

    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode==100){
//            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                task.getResult(ApiException.class);
//                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(intent);
//                finish();
//            } catch (ApiException e) {
//                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    public void onClick(View view) {
        switch (view.getId()) {
            // Jump to the registration screen
            case R.id.loginactivity_tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;

            case R.id.loginactivity_btn_login:
                progressBar.setVisibility(View.VISIBLE);
                String name = mEtLoginactivityUsername.getText().toString().trim();
                String password = mEtLoginactivityPassword.getText().toString().trim();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {

                    mAuth.signInWithEmailAndPassword(name, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithEmail:success");
//                                        FirebaseUser user = mAuth.getCurrentUser();
//                                        updateUI(user);
                                        //addDataToFirestore();
                                        SignInFirestore();
                                        Toast.makeText(LoginActivity.this, "Login Successful.",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
//                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "Incorrect Password, or Account does not exist.",
                                                Toast.LENGTH_SHORT).show();
//                                        updateUI(null);
                                    }
                                }
                            });
//                    ArrayList<User> data = DatabaseHelper.getAllData();
//                    boolean match = false;
//                    for (int i = 0; i < data.size(); i++) {
//                        User user = data.get(i);
//                        if (name.equals(user.getName()) && password.equals(user.getPassword())) {
//                            match = true;
//                            break;
//                        } else {
//                            match = false;
//                        }
//                    }
//                    if (match) {
//                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(this, HomeActivity.class));
//                        finish();
//                    } else {
//                        Toast.makeText(this, "Password is incorrect, or user does not exist", Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Please Enter E-mail or Password", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}