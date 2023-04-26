package com.example.myapplicationexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    RoundedImageView imgPro;
    TextView textAddImage;
    FrameLayout layoutImage;

    private String realCode;

    private String encodedImage;
    private PreferenceManager preferenceManager;

//    private DatabaseHelper mDatabaseHelper;
    private Button mBtRegisteractivityRegister;
    private ImageView mIvRegisteractivityBack;
    private EditText mEtRegisteractivityUserId;
    private EditText mEtRegisteractivityUsername;
    private EditText mEtRegisteractivityPassword1;
    private EditText mEtRegisteractivityPassword2;
    private EditText mEtRegisteractivityPhonecodes;
    private ImageView mIvRegisteractivityShowcode;

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
        setContentView(R.layout.activity_register);

        initView();

//        mDatabaseHelper = new DatabaseHelper(this);

        //将验证码用图片的形式显示出来
        mIvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();

        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
        }

    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUp(){
//        mEtRegisteractivityUsername = findViewById(R.id.registeractivity_et_username);
//        mEtRegisteractivityUserId = findViewById(R.id.registeractivity_et_userid);
//        mEtRegisteractivityPassword1 = findViewById(R.id.registeractivity_et_password1);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, mEtRegisteractivityUserId.getText().toString());
        user.put(Constants.KEY_EMAIL, mEtRegisteractivityUsername.getText().toString());
        user.put(Constants.KEY_PASSWORD, mEtRegisteractivityPassword1.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {

                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME, mEtRegisteractivityUserId.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(exception -> {

                    progressBar.setVisibility(View.GONE);
                    showToast(exception.getMessage());

                });
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int preViewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap preViewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, preViewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        preViewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imgPro = findViewById(R.id.imageProfile);
                            imgPro.setImageBitmap(bitmap);
                            textAddImage = findViewById(R.id.textAddImage);
                            textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);

                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

//    private Boolean isValidSignUpDetails(){
//        if (encodedImage == null){
//            showToast("Select Profile Image");
//            return false;
//        }else {
//            return true;
//        }
//    }

    private void initView(){

        // 初始化控件
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        mBtRegisteractivityRegister = findViewById(R.id.registeractivity_btn_register);
        mIvRegisteractivityBack = findViewById(R.id.registeractivity_iv_back);
        mEtRegisteractivityUsername = findViewById(R.id.registeractivity_et_username);
        mEtRegisteractivityUserId = findViewById(R.id.registeractivity_et_userid);
        mEtRegisteractivityPassword1 = findViewById(R.id.registeractivity_et_password1);
        mEtRegisteractivityPassword2 = findViewById(R.id.registeractivity_et_password2);
        mEtRegisteractivityPhonecodes = findViewById(R.id.registeractivity_et_phoneCodes);
        mIvRegisteractivityShowcode = findViewById(R.id.registeractivity_iv_showCode);
        layoutImage = findViewById(R.id.layoutImage);

        // 设置点击事件监听器
        mIvRegisteractivityBack.setOnClickListener(this);
        mIvRegisteractivityShowcode.setOnClickListener(this);
        mBtRegisteractivityRegister.setOnClickListener(this);
        layoutImage.setOnClickListener(this);
    }

    public void onClick(View view) {

        //layoutImage = findViewById(R.id.layoutImage);
//        layoutImage.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            pickImage.launch(intent);
//        });

        switch (view.getId()) {
            case R.id.layoutImage:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
                break;
            case R.id.registeractivity_iv_back: //返回登录页面
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.registeractivity_iv_showCode:    //改变随机验证码的生成
                mIvRegisteractivityShowcode.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
                progressBar.setVisibility(View.GONE);
                break;
            case R.id.registeractivity_btn_register:    //注册按钮
                progressBar.setVisibility(View.VISIBLE);
                //获取用户输入的Profile, 用户名、密码、验证码
                String username = mEtRegisteractivityUsername.getText().toString().trim();
                String password = mEtRegisteractivityPassword1.getText().toString().trim();
                String password2 = mEtRegisteractivityPassword2.getText().toString().trim();
                String phoneCode = mEtRegisteractivityPhonecodes.getText().toString().toLowerCase();
                //注册验证
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(phoneCode) && !(encodedImage == null) ) {
                    if (password.equals(password2)){
                        if (phoneCode.equals(realCode)) {
                            //将用户名和密码加入到数据库中
//                            mDatabaseHelper.add(username, password);
//                            startActivity(new Intent(this, HomeActivity.class));
//                            finish();
                            mAuth.createUserWithEmailAndPassword(username, password)
                                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            progressBar.setVisibility(View.GONE);
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                //Log.d(TAG, "createUserWithEmail:success");
                                                //FirebaseUser user = mAuth.getCurrentUser();
                                                //updateUI(user);
                                                signUp();
                                                Toast.makeText(RegisterActivity.this, "Registration Successful.",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                Toast.makeText(RegisterActivity.this, "Authentication Failed or Account Existed.",
                                                        Toast.LENGTH_SHORT).show();
                                                //updateUI(null);
                                            }
                                        }
                                    });
                            // Toast.makeText(this,  "Registration Successful", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Verification Code Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Two Inconsistent Passwords", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Information Not Perfect", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

