package com.example.chrisantuseze.palcechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroup extends AppCompatActivity {

    private EditText etGroupName;
    private CircleImageView groupPic;
    private FloatingActionButton fab;
    private Toolbar mToolbar;

    private DatabaseReference mGroupDatabase;
    String mCurrentUser;

    private TextView textView;
    private TextView tvName;

    static Typeface custom_font2;
    private AVLoadingIndicatorView mAVL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.main_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("All_Groups");


        tvName = (TextView)findViewById(R.id.users);
        tvName.setText("New Group");

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");
        tvName.setTypeface(custom_font2);

        mAVL = (AVLoadingIndicatorView)findViewById(R.id.avi);
        mAVL.setVisibility(View.INVISIBLE);

        etGroupName = (EditText)findViewById(R.id.etGrpName);
        textView = (TextView)findViewById(R.id.tv);
        groupPic = (CircleImageView)findViewById(R.id.circleView);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        etGroupName.setTypeface(custom_font1);
        textView.setTypeface(custom_font1);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String group_name = etGroupName.getText().toString();
                fab.setEnabled(false);
                etGroupName.setFocusable(false);
                mAVL.setVisibility(View.VISIBLE);
                mAVL.show();
                StyleableToast.makeText(getApplicationContext(), "Creating a new group...",
                        R.style.success).show();

                final Map map = new HashMap();
                map.put(group_name, "");
                mGroupDatabase.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            mAVL.hide();
                            mAVL.setVisibility(View.INVISIBLE);
                            StyleableToast.makeText(getApplicationContext(),
                                    "Group created successfully", R.style.success).show();
                            Intent intent = new Intent(CreateGroup.this, GroupChatRoom.class);
                            intent.putExtra("group_name", group_name);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
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
