package com.example.myapplicationexample;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultLauncherKt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplicationexample.databinding.FragmentSettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private PreferenceManager preferenceManager;

    FirebaseAuth auth;
    Button logout_button;
    TextView textViewUserName;
    RoundedImageView roundedImageView;
    //TextView TextName;
    FirebaseUser user;
    Button btn_scan;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_settings,container,false);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        preferenceManager = new PreferenceManager(getContext().getApplicationContext());
        loadUserDetails();
        //getToken();

        //Get UserName and Signout Status
        auth = FirebaseAuth.getInstance();

        //QR Scanner
        btn_scan = view.findViewById(R.id.btn_scan);
        btn_scan.setOnClickListener(v -> {

            scanCode();
            //barcodeLauncher.launch(new ScanOptions());

        });

        logout_button = view.findViewById(R.id.settings_logout);
        textViewUserName = view.findViewById(R.id.settings_username);
        roundedImageView = view.findViewById(R.id.imageProfile);

        user = auth.getCurrentUser();
        if (user==null){
            Intent intent=new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        else {
            textViewUserName.setText(user.getEmail());
//            roundedImageView.setImageURI(user.getPhotoUrl());
        }

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                UserSignOut();

                Intent intent=new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    //Gain the Login Status and Add System Token to Cloud Firestore for Ensure the User Status
    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(unused -> showToast("Token Updated Successfully"))
                .addOnFailureListener(e -> showToast("Unable To Update Token"));
    }

    //Delete the Token in Cloud Firestore to ensure the Offline Status of Users
    private void UserSignOut() {
        showToast("Signing Out...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
//                    startActivity(new Intent(getContext().getApplicationContext(), LoginActivity.class));
//                    getActivity().finish();
                })
                .addOnFailureListener(e -> showToast("Unable To Sign Out"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * 扫码方法
     */
    private void scanCode() {

        ScanOptions options = new ScanOptions();
        //IntentIntegrator integrator = new IntentIntegrator(getActivity());
        // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
        //options.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        // Set prompt text
        options.setPrompt("Volume up to flash on");
        // Set beep
        options.setBeepEnabled(true); // 扫到码后播放提示音
        // Set capture activity
        options.setCaptureActivity(CaptureAct.class);
        //Locked orientation
        options.setOrientationLocked(true);
        options.setCameraId(0);  // 使用默认的相机
        options.setBarcodeImageEnabled(true);
        // Initiae scan
        //integrator.initiateScan();
        // Launch
        barcodeLauncher.launch(options);

    }

    // Register the launcher and result handler
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(getContext().getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "Scanned: " + result.getContents(), Toast.LENGTH_SHORT).show();
                    ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    //创建ClipData对象
                    //第一个参数只是一个标记，随便传入。
                    //第二个参数是要复制到剪贴版的内容
                    ClipData clip = ClipData.newPlainText("copy text", result.getContents());
                    //传入clipdata对象.
                    manager.setPrimaryClip(clip);
                    Toast.makeText(getContext().getApplicationContext(), "Content Copied to Clipboard", Toast.LENGTH_LONG).show();
                }
            });

//    /**
//     * 扫码结果事件
//     * @param requestCode
//     * @param resultCode
//     * @param data
//     */
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if(result != null) {
//            if(result.getContents() == null) {
//                Toast.makeText(getActivity(), "扫码取消！", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(getActivity(), "扫描成功，条码值: " + result.getContents(), Toast.LENGTH_LONG).show();
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }

}