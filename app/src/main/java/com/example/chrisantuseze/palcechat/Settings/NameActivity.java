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

public class NameActivity extends AppCompatActivity {
    private EditText etName;
    private Button btnOk;
    private Toolbar mToolbar;

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;

    private ProgressDialog dialog;
    private TextView tvName;
    private AVLoadingIndicatorView mAVL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

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
        tvName.setText("Change Name");

        mAVL = (AVLoadingIndicatorView)findViewById(R.id.avi);
        mAVL.setVisibility(View.INVISIBLE);

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");
        tvName.setTypeface(custom_font2);

        String name_value = getIntent().getStringExtra("name");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String user_id = mCurrentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);


        etName = (EditText)findViewById(R.id.name);

        btnOk = (Button)findViewById(R.id.ok);

        etName.setText(name_value);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAVL.setVisibility(View.VISIBLE);
                mAVL.show();
                StyleableToast.makeText(getApplicationContext(), "Saving changes...",
                        R.style.success).show();
                String name = etName.getText().toString();

                mDatabase.child("name").setValue(name).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                startActivity(new Intent(NameActivity.this, SettingsAccount.class));
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
