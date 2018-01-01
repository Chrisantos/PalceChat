package com.example.chrisantuseze.palcechat.Register_Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chrisantuseze.palcechat.MainActivity;
import com.example.chrisantuseze.palcechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvName, tvSignIn;
    private AVLoadingIndicatorView mAVL;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        etName = (EditText)findViewById(R.id.name);
        etEmail = (EditText)findViewById(R.id.email);
        etPassword = (EditText)findViewById(R.id.password);

        btnRegister = (Button)findViewById(R.id.signup);
        tvSignIn = (TextView)findViewById(R.id.tvsignin);

        tvName = (TextView)findViewById(R.id.palce);
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        tvName.setTypeface(custom_font1);
        btnRegister.setTypeface(custom_font1);
        tvSignIn.setTypeface(custom_font1);

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");
        etName.setTypeface(custom_font2);
        etEmail.setTypeface(custom_font2);
        etPassword.setTypeface(custom_font2);

        mAVL = (AVLoadingIndicatorView)findViewById(R.id.avi);

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                    if (checkInternetConnection()){
                        mAVL.setVisibility(View.VISIBLE);
                        mAVL.show();
//                        new StyleableToast.Builder(getApplicationContext())
//                                .text("Creating Account, Please wait...").textColor(Color.WHITE)
//                                .backgroundColor(getColor(R.color.colorPrimaryDark)).show();
                        StyleableToast.makeText(getApplicationContext(), "Creating Account, Please " +
                                "wait...", R.style.success).show();
                        etEmail.setFocusable(false);
                        etPassword.setFocusable(false);
                        etName.setFocusable(false);
                        btnRegister.setEnabled(false);
                        registerUser(name, email, password);
                    }else{
//                        new StyleableToast.Builder(getApplicationContext())
//                                .text("No Internet Connection").textColor(Color.WHITE)
//                                .backgroundColor(getColor(R.color.red)).show();
                        StyleableToast.makeText(getApplicationContext(), "No Internet Connection", R.style.error).show();

                    }



                }else{
//                    new StyleableToast.Builder(getApplicationContext())
//                            .text("Please fill in fields").textColor(Color.WHITE)
//                            .backgroundColor(getColor(R.color.red)).show();
                    StyleableToast.makeText(getApplicationContext(), "Please fill in fields", R.style.error).show();

                }

                mAVL.setVisibility(View.INVISIBLE);
            mAVL.hide();
            }
        });
    }

    private void registerUser(final String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
        String error = "";

        try {

     // if (checkInternetConnection()) {
        if (task.isSuccessful()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String uID = currentUser.getUid();

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uID);
            HashMap<String, String> userMap = new HashMap<String, String>();
            userMap.put("name", name);
            userMap.put("phone", "08138203079");
            userMap.put("location", "Alama, Rock Haven Jos");
            userMap.put("email", currentUser.getEmail());
            userMap.put("status", "Hi there, I'm using palce chat");
            userMap.put("image", "default");
            userMap.put("thumb_image", "default");
            userMap.put("background_image", "default");
            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    String user_id = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    mUserDatabase.child(user_id).child("device_token").setValue(deviceToken)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(Register.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                                        .FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else{

                            }
                            }
                        });

                }
                }
            });


                    } else {
            new StyleableToast.Builder(getApplicationContext())
                    .text("Registration failed!").textColor(Color.WHITE)
                    .backgroundColor(getColor(R.color.red)).show();
                    }
//                                    }else{
//                                        Toast.makeText(Register.this, "No internet connection!",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
                throw task.getException();
            }catch (FirebaseAuthWeakPasswordException e){
                error = "Weak Password";
            }catch (FirebaseAuthInvalidCredentialsException e){
                error = "Invalid Email";
            }catch (FirebaseAuthUserCollisionException e){
                error = "Existing account";
            }catch (Exception e){
                error = "";
            }


        }
    });

    }
    public boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connectMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //Check for network connections
        if (connectMgr.getNetworkInfo(0).getState() ==
                NetworkInfo.State.CONNECTED ||
                connectMgr.getNetworkInfo(0).getState() ==
                        NetworkInfo.State.CONNECTING ||
                connectMgr.getNetworkInfo(1).getState() ==
                        NetworkInfo.State.CONNECTING ||
                connectMgr.getNetworkInfo(0).getState() ==
                        NetworkInfo.State.CONNECTED) {
            return true;
        } else if (
                connectMgr.getNetworkInfo(0).getState() ==
                        NetworkInfo.State.DISCONNECTED ||
                        connectMgr.getNetworkInfo(1).getState() ==
                                NetworkInfo.State.DISCONNECTED) {

            new StyleableToast.Builder(getApplicationContext())
                    .text("No Internet Connection").textColor(Color.WHITE)
                    .backgroundColor(Color.RED).show();
            return false;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
            if (item.getItemId() == android.R.id.home){
                onBackPressed();
            }
        return true;
    }
}
