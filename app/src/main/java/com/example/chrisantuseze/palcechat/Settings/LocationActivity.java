package com.example.chrisantuseze.palcechat.Settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.wang.avi.AVLoadingIndicatorView;

public class LocationActivity extends AppCompatActivity {
    Toolbar mToolbar;
    private EditText etLocation;
    private Button btnOk;

    //untested
    FirebaseUser mCurrentUser;
    DatabaseReference mDatabase;
    ProgressDialog dialog;
    private TextView tvName;
    private AVLoadingIndicatorView mAVL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.main_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        tvName = (TextView)findViewById(R.id.users);
        tvName.setText("Change Location");

        mAVL = (AVLoadingIndicatorView)findViewById(R.id.avi);
        mAVL.setVisibility(View.INVISIBLE);

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");
        tvName.setTypeface(custom_font2);

        String location_value = getIntent().getStringExtra("location");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        //

        etLocation = (EditText)findViewById(R.id.location);

        btnOk = (Button)findViewById(R.id.ok);

        etLocation.setText(location_value);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAVL.setVisibility(View.VISIBLE);
                mAVL.show();
                StyleableToast.makeText(getApplicationContext(), "Saving changes...",
                        R.style.success).show();
                String location = etLocation.getText().toString();

                mDatabase.child("location").setValue(location).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            startActivity(new Intent(LocationActivity.this, SettingsAccount.class));
                            finish();
                        }else{
                            StyleableToast.makeText(getApplicationContext(), "Error occurred saving changes!",
                                    R.style.error).show();
                        }
                    }
                });
                mAVL.hide();
                mAVL.setVisibility(View.INVISIBLE);
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
