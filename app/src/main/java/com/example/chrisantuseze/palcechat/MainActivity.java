package com.example.chrisantuseze.palcechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.Settings.SettingsAccount;
import com.example.chrisantuseze.palcechat.Settings.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    FirebaseUser currentUser;
    private Toolbar mToolbar, mToolbarBottom;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference mDatabase;

    private TextView tvName;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.main_custom_bar, null);
        actionBar.setCustomView(action_bar_view);


//        mToolbarBottom = (Toolbar)findViewById(R.id.main_page_toolbar_bottom);
//        setSupportActionBar(mToolbarBottom);
//        ActionBar action_bar = getSupportActionBar();
//        action_bar.setDisplayShowCustomEnabled(true);
//        action_bar.setTitle("");
//
//        LayoutInflater inflata = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflata.inflate(R.layout.main_bottom_custom_bar, null);
//        action_bar.setCustomView(view);

        tvName = (TextView)findViewById(R.id.users);
        //tvName.setText("Palce Chat");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        tvName.setTypeface(custom_font1);

        profileImage = (CircleImageView)findViewById(R.id.profile_pic);
        profileImage.setVisibility(View.VISIBLE);

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");
        //tvName.setTypeface(custom_font2);

        mViewPager = (ViewPager)findViewById(R.id.view_pager);
        mTabLayout = (TabLayout)findViewById(R.id.mainTabs);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


//        if (!checkInternetConnection()){
//            AVLoadingIndicatorView AVL = (AVLoadingIndicatorView)findViewById(R.id.avi);
//            AVL.setVisibility(View.INVISIBLE);
//            AVL.hide();
//        }


        if (currentUser != null){

            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            mUserRef.keepSynced(true);

            String user_id = currentUser.getUid();

//            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    final String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
//
//                    Picasso.with(MainActivity.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
//                            .placeholder(R.drawable.user_avatar1)
//                            .into(profileImage, new Callback() {
//                                @Override
//                                public void onSuccess() {
//
//                                }
//
//                                @Override
//                                public void onError() {
//
//                                    Picasso.with(MainActivity.this).load(thumb_image).placeholder(R.drawable.user_avatar1)
//                                            .into(profileImage);
//
//                                }
//                            });
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(user_id);
            mDatabase.keepSynced(true);

        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsAccount.class));
            }
        });



    }
    private void selectPage(int pageIndex){
        mTabLayout.setScrollPosition(pageIndex,0f,true);
        mViewPager.setCurrentItem(pageIndex);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (currentUser == null){
            sendToStart();
        }else {
            mUserRef.child("online").setValue("0");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentUser != null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendToStart() {
        startActivity(new Intent(MainActivity.this, Splash.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();
        switch (id){

            case R.id.account:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;

            case R.id.logout:

                FirebaseAuth.getInstance().signOut();
                sendToStart();
                break;
            default:
                return false;
        }
        return true;

    }
}
