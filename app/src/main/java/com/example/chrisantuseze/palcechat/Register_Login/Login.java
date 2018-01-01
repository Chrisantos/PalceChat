package com.example.chrisantuseze.palcechat.Register_Login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chrisantuseze.palcechat.MainActivity;
import com.example.chrisantuseze.palcechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.wang.avi.AVLoadingIndicatorView;

public class Login extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp, tvName;
    private AVLoadingIndicatorView mAVL;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        etEmail = (EditText)findViewById(R.id.email);
        etPassword = (EditText)findViewById(R.id.password);

        btnLogin = (Button)findViewById(R.id.signin);
        tvSignUp = (TextView)findViewById(R.id.tvsignup);

        tvName = (TextView)findViewById(R.id.palce);
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        tvName.setTypeface(custom_font1);
        btnLogin.setTypeface(custom_font1);
        tvSignUp.setTypeface(custom_font1);

        mAVL = (AVLoadingIndicatorView)findViewById(R.id.avi);

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");
        etEmail.setTypeface(custom_font2);
        etPassword.setTypeface(custom_font2);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    if (checkInternetConnection()){
//                        new StyleableToast.Builder(getApplicationContext())
//                                .text("Signing In, Please wait..").textColor(Color.WHITE)
//                                .backgroundColor(getColor(R.color.colorPrimaryDark)).show();

                        StyleableToast.makeText(getApplicationContext(), "Signing in, please wait..",
                                R.style.success).show();

                       // startAnim();
                        mAVL.setVisibility(View.VISIBLE);
                        mAVL.show();
                        etEmail.setFocusable(false);
                        etPassword.setFocusable(false);
                        btnLogin.setEnabled(false);
                        loginUser(email, password);
                    }else{
//                        new StyleableToast.Builder(getApplicationContext())
//                                .text("No Internet Connection").textColor(Color.WHITE)
//                                .backgroundColor(getColor(R.color.red)).show();
                        StyleableToast.makeText(getApplicationContext(), "No internet connection",
                                R.style.error).show();
                    }

                }else{
//                    new StyleableToast.Builder(getApplicationContext())
//                            .text("Please enter credentials").textColor(Color.WHITE)
//                            .backgroundColor(getColor(R.color.red)).show();
                    StyleableToast.makeText(getApplicationContext(), "Please enter credentials",
                            R.style.error).show();
                }
                mAVL.setVisibility(View.INVISIBLE);
                hideAnim();

            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mAVL.show();
                    String user_id = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    mUserDatabase.child(user_id).child("device_token").setValue(deviceToken)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }else{

                        }
                        }
                    });


                }else{
                    Toast.makeText(Login.this, "Signing in failed!",
                            Toast.LENGTH_SHORT).show();
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

            Toast.makeText(this, " No Internet Connection ", Toast.LENGTH_LONG).show();

            return false;
        }
        return false;
    }
    private void startAnim(){
        mAVL.show();

    }
    private void hideAnim(){
        mAVL.hide();

    }

}
