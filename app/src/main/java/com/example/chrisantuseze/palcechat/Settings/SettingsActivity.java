package com.example.chrisantuseze.palcechat.Settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chrisantuseze.palcechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private LinearLayout linearAccount, linearNotif, linearBackground, linearHelp;
    private TextView tvAccount, tvNotif, tvBackground, tvHelp;
    Toolbar mToolbar;
    private TextView tvName;

    private static final int GALLERY_PICK = 1;
    private StorageReference mImageStorage;
    private ProgressDialog dialog;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.main_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = mCurrentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);

        dialog = new ProgressDialog(this);

        tvName = (TextView)findViewById(R.id.users);
        tvName.setText("Settings");

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");
        tvName.setTypeface(custom_font2);

        linearAccount = (LinearLayout)findViewById(R.id.account_linear);
//        linearNotif = (LinearLayout)findViewById(R.id.notifications_linear);
        linearBackground = (LinearLayout)findViewById(R.id.chat_background_linear);
        linearHelp = (LinearLayout)findViewById(R.id.help_linear);

        tvAccount = (TextView)findViewById(R.id.acct);
//      tvNotif = (TextView)findViewById(R.id.notif);
        tvBackground = (TextView)findViewById(R.id.bg);
        tvHelp = (TextView)findViewById(R.id.help);

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        tvAccount.setTypeface(custom_font1);
//      tvNotif.setTypeface(custom_font1);
        tvBackground.setTypeface(custom_font1);
        tvHelp.setTypeface(custom_font1);

        linearAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, SettingsAccount.class));
            }
        });
//        linearNotif.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(SettingsActivity.this, SettingsNotification.class));
//            }
//        });
        linearBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        linearHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, SettingsHelp.class));
            }
        });


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
