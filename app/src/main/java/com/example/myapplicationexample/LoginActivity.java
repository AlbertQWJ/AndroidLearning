package com.example.myapplicationexample;

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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    ProgressBar progressBar;

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

//        DatabaseHelper = new DatabaseHelper(this);
    }

    private void initView() {

        // 初始化控件
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        mBtLoginactivityLogin = findViewById(R.id.loginactivity_btn_login);
        mTvLoginactivityRegister = findViewById(R.id.loginactivity_tv_register);
        mEtLoginactivityUsername = findViewById(R.id.loginactivity_et_username);
        mEtLoginactivityPassword = findViewById(R.id.loginactivity_et_password);

        // 设置点击事件监听器
        mBtLoginactivityLogin.setOnClickListener(this);
        mTvLoginactivityRegister.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            // 跳转到注册界面
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
                                        Toast.makeText(LoginActivity.this, "Login Successful.",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
//                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
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
//                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(this, HomeActivity.class));
//                        finish();
//                    } else {
//                        Toast.makeText(this, "密码不正确，或用户不存在", Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    Toast.makeText(this, "Please Enter E-mail or Password", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}