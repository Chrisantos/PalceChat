package com.example.chrisantuseze.palcechat;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvName, tvStatus, tvLocation, tvPhoneNumber, tvEmail, tvAddTag;
    private ImageView imgProfile;
    private Button btnAcceptRequest, btnDeclineRequest, btnUnfriend;
    private ImageButton imgBtnSendRequest;

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mFriendRequest, requestTo;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    private DatabaseReference mRootRef;

    private FirebaseAuth mAuth;

    private FirebaseUser currentUser;
    private DatabaseReference mUserRef;

    private ProgressDialog dialog;
    private String mCurrent_state;

    private String mDisplayName;
    private String mLocation;
    private String mImage;
    private String mStatus;
    private String mPhoneNumber;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("user_key");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mDatabase.keepSynced(true);
        mFriendRequest = FirebaseDatabase.getInstance().getReference().child("friend_request");

        requestTo = FirebaseDatabase.getInstance().getReference().child("request_to");

        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mDatabase.keepSynced(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        tvName = (TextView) findViewById(R.id.name);
        tvStatus = (TextView) findViewById(R.id.status);
        tvLocation = (TextView) findViewById(R.id.location);
        btnAcceptRequest = (Button) findViewById(R.id.accept_request);
        btnDeclineRequest = (Button) findViewById(R.id.decline_request);
        btnUnfriend = (Button) findViewById(R.id.unfriend);
        imgProfile = (ImageView) findViewById(R.id.profile_image);

        imgBtnSendRequest = (ImageButton) findViewById(R.id.send_request);
        tvAddTag = (TextView) findViewById(R.id.tvrequest);

        tvPhoneNumber = (TextView) findViewById(R.id.phone_number);
        tvEmail = (TextView) findViewById(R.id.email);

        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Sansation-Bold.ttf");
        tvName.setTypeface(custom_font1);
        btnAcceptRequest.setTypeface(custom_font1);
        btnDeclineRequest.setTypeface(custom_font1);
        btnUnfriend.setTypeface(custom_font1);

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");
        tvStatus.setTypeface(custom_font2);
        tvLocation.setTypeface(custom_font2);
        tvAddTag.setTypeface(custom_font2);
        tvPhoneNumber.setTypeface(custom_font2);
        tvEmail.setTypeface(custom_font2);

        if (btnDeclineRequest.getVisibility() == View.VISIBLE) {
            btnDeclineRequest.setVisibility(View.INVISIBLE);
            btnDeclineRequest.setEnabled(false);
        }
        mCurrent_state = "not_friends";

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading user data");
        dialog.setMessage("Please wait while user data is loaded.");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDisplayName = dataSnapshot.child("name").getValue().toString();
                mLocation = dataSnapshot.child("location").getValue().toString();
                mStatus = dataSnapshot.child("status").getValue().toString();
                mImage = dataSnapshot.child("thumb_image").getValue().toString();
                mPhoneNumber = dataSnapshot.child("phone").getValue().toString();
                mEmail = dataSnapshot.child("email").getValue().toString();

                tvName.setText(mDisplayName);
                tvLocation.setText(mLocation);
                tvStatus.setText(mStatus);
                tvPhoneNumber.setText(mPhoneNumber);
                tvEmail.setText(mEmail);
                if (!mImage.equals("default")) {

                Picasso.with(ProfileActivity.this).load(mImage).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.user_avatar1)
                    .into(imgProfile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                        Picasso.with(ProfileActivity.this).load(mImage).placeholder(R.drawable.user_avatar1)
                                    .into(imgProfile);

                        }
                    });

                }
                dialog.dismiss();


        mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)){
                            imgBtnSendRequest.setVisibility(View.INVISIBLE);
                            tvAddTag.setVisibility(View.INVISIBLE);


                            btnAcceptRequest.setVisibility(View.GONE);
                            btnDeclineRequest.setVisibility(View.GONE);

                            btnDeclineRequest.setVisibility(View.INVISIBLE);
                            btnAcceptRequest.setVisibility(View.INVISIBLE);


                            mCurrent_state = "Friends";
                            btnUnfriend.setVisibility(View.VISIBLE);
                            btnUnfriend.setText("Unfriend " + mDisplayName);
                        }
                        if (mCurrentUser.getUid().equals(user_id)){
                            imgBtnSendRequest.setVisibility(View.INVISIBLE);
                            tvAddTag.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        //---------------FRIEND REQUEST LIST---------------------------------

        mFriendRequest.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

        if (dataSnapshot.hasChild(user_id)) {
                String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                if (request_type.equals("received")) {
                    tvAddTag.setVisibility(View.INVISIBLE);
                    imgBtnSendRequest.setVisibility(View.INVISIBLE);

                    mCurrent_state = "req_received";
                    btnAcceptRequest.setVisibility(View.VISIBLE);
                    btnAcceptRequest.setText("Accept Friend Request");
                    btnDeclineRequest.setVisibility(View.VISIBLE);
                    btnAcceptRequest.setTextColor(getResources()
                            .getColor(R.color.white));
                    btnAcceptRequest.setBackgroundResource(R.drawable.button_change_status);
                } else if (request_type.equals("sent")) {

                    mCurrent_state = "request_sent";
                    tvAddTag.setText("Cancel");

                    btnAcceptRequest.setVisibility(View.INVISIBLE);
                    btnDeclineRequest.setVisibility(View.INVISIBLE);
                }
        } else {
            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {
                        tvAddTag.setVisibility(View.INVISIBLE);
                        imgBtnSendRequest.setVisibility(View.INVISIBLE);

                        btnAcceptRequest.setVisibility(View.INVISIBLE);
                        btnDeclineRequest.setVisibility(View.INVISIBLE);


                        btnUnfriend.setVisibility(View.VISIBLE);
                        mCurrent_state = "Friends";
                        btnUnfriend.setText("Unfriend " + mDisplayName);
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    dialog.dismiss();
                }
            });
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            dialog.dismiss();
        }
    });


    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});

        imgBtnSendRequest.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                imgBtnSendRequest.setEnabled(false);

                //-----------------NOT FRIENDS STATE------------------------

                if (mCurrent_state.equals("not_friends")) {

//                    DatabaseReference newNotificationRef = mRootRef.child("notifications")
//                            .child(user_id).push();
//                    String newNotificationId = newNotificationRef.getKey();
//
//                    HashMap<String, String> notificationsData = new HashMap<>();
//                    notificationsData.put("from", mCurrentUser.getUid());
//                    notificationsData.put("type", "request");
//
//                    Map requestMap = new HashMap();
//                    requestMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + user_id + "/request_type"
//                            , "sent");
//                    requestMap.put("Friend_req/" + user_id + "/" +mCurrentUser.getUid() + "/request_type"
//                            , "received");
//                    requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationsData);
//
//                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                            if (databaseError == null){
//                                btnSendRequest.setEnabled(true);
//                                mCurrent_state = "request_sent";
//                                btnSendRequest.setText("Cancel Friend Request");
//                                btnSendRequest.setTextColor(getResources()
//                                        .getColor(R.color.white));
//                                btnSendRequest.setBackgroundResource(R.drawable.button_change_status);
//                            }else{
//                                Toast.makeText(ProfileActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });

                    mFriendRequest.child(mCurrentUser.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                mFriendRequest.child(user_id).child(mCurrentUser.getUid())
                                        .child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String, String> notificationsData = new HashMap<>();
                                        notificationsData.put("from", mCurrentUser.getUid());
                                        notificationsData.put("type", "request");

                                        mNotificationDatabase.child(user_id).push().setValue(notificationsData)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                imgBtnSendRequest.setEnabled(true);
                                                mCurrent_state = "request_sent";
                                                tvAddTag.setText("Cancel");

                                                //...................Create another parent...................................

                                                requestTo = FirebaseDatabase.getInstance()
                                                        .getReference().child("request_to")
                                                        .child(mCurrentUser.getUid()).child(user_id);
                                                requestTo.setValue(user_id).addOnCompleteListener(
                                                        new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                });


                                            }
                                        });


                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, "Error Sending request",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

//-------------------CANCEL REQUEST STATE...............................
                else if (mCurrent_state.equals("request_sent")) {

//        Map requestMap = new HashMap();
//        requestMap.put("friend_request/" + mCurrentUser.getUid() + "/" + user_id, null);
//        requestMap.put("friend_request/" + user_id + "/" +mCurrentUser.getUid(), null);
//
//        mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                if (databaseError == null){
//                    mCurrent_state = "not_friends";
//                    btnSendRequest.setText("Send Friend Request");
//                    btnSendRequest.setBackground(getResources()
//                            .getDrawable(R.drawable.button_change_name));
//                    btnSendRequest.setTextColor(getResources()
//                            .getColor(R.color.colorPrimaryDark));
//
//                }else{
//                    Toast.makeText(ProfileActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//                btnSendRequest.setEnabled(true);
//            }
//        });

                    mFriendRequest.child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        mFriendRequest.child(user_id).child(mCurrentUser.getUid())
                                                .removeValue().addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mCurrent_state = "not_friends";
                                                    tvAddTag.setText("Add Friend");

                                                } else {
                                                    Toast.makeText(ProfileActivity.this,
                                                    "Error cancelling request", Toast.LENGTH_SHORT).show();
                                                }
                                                imgBtnSendRequest.setEnabled(true);
                                            }
                                        });
                                    } else {
                                        imgBtnSendRequest.setEnabled(true);
                                        Toast.makeText(ProfileActivity.this, "Error cancelling " +
                                                "request", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            });

    //---------------------------ACCEPTING FRIEND REQUEST------------------------

        btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view){

            if (mCurrent_state.equals("req_received")) {
                final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

//                    Map friendMap = new HashMap();
//                    friendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id + "/date", currentDate);
//                    friendMap.put("Friends/" + user_id + "/" +mCurrentUser.getUid() + "/date", currentDate);
//
//                    friendMap.put("friend_request/" + mCurrentUser.getUid() + "/" + user_id, null);
//                    friendMap.put("friend_request/" + user_id + "/" +mCurrentUser.getUid(), null);
//
//                    mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                            if (databaseError == null){
//                                mCurrent_state = "Friends";
//                                btnSendRequest.setText("Unfriend " + displayName);
//                                btnSendRequest.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//                                btnSendRequest.setBackgroundResource(R.drawable.button_change_name);
//
//                                btnDeclineRequest.setVisibility(View.INVISIBLE);
//
//                            }else{
//                                Toast.makeText(ProfileActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });


                Map friendsMap = new HashMap();
                friendsMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id + "/date", currentDate);
                friendsMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid() + "/date", currentDate);


                friendsMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + user_id, null);
                friendsMap.put("Friend_req/" + user_id + "/" + mCurrentUser.getUid(), null);


                mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                        if (databaseError == null) {

                            btnUnfriend.setVisibility(View.VISIBLE);

                            imgBtnSendRequest.setVisibility(View.INVISIBLE);
                            tvAddTag.setVisibility(View.INVISIBLE);

                            mCurrent_state = "Friends";
                            btnUnfriend.setText("Unfriend " + mDisplayName);

                            btnDeclineRequest.setVisibility(View.INVISIBLE);
                            btnAcceptRequest.setVisibility(View.INVISIBLE);

                        } else {

                            String error = databaseError.getMessage();

                            Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                        }

                    }
                });
            }
        }
    });


//---------------UNFRIENDING-----------------------------------
    btnUnfriend.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View view){

        if (mCurrent_state.equals("Friends")) {

//            Map friendMap = new HashMap();
//            friendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id, null);
//            friendMap.put("Friends/" + user_id + "/" +mCurrentUser.getUid(), null);
//
//            mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
//                @Override
//                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                    if (databaseError == null){
//                        mCurrent_state = "not_friends";
//                        btnSendRequest.setText("Send Friend Request");
//                        btnSendRequest.setBackgroundResource(R.drawable.button_change_name);
//                        btnSendRequest.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//
//                        btnDeclineRequest.setVisibility(View.INVISIBLE);
//
//                    }else{
//                        Toast.makeText(ProfileActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                    btnSendRequest.setEnabled(true);
//                }
//            });
//
//

            Map unfriendMap = new HashMap();
            unfriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id, null);
            unfriendMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid(), null);

            mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                    if (databaseError == null) {

                        tvAddTag.setVisibility(View.VISIBLE);
                        imgBtnSendRequest.setVisibility(View.VISIBLE);

                        mCurrent_state = "not_friends";
                        tvAddTag.setText("Add Friend");

                        btnDeclineRequest.setVisibility(View.INVISIBLE);
                        btnAcceptRequest.setVisibility(View.INVISIBLE);
                        btnUnfriend.setVisibility(View.INVISIBLE);

                    } else {

                        String error = databaseError.getMessage();

                        Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();


                    }

                    btnDeclineRequest.setEnabled(true);

                }
            });

        }
    }
    });




        //--------------DECLINING FRIEND REQUEST--------------------------

        btnDeclineRequest.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            btnDeclineRequest.setEnabled(false);

            if (mCurrent_state.equals("req_received")) {

            Map friendMap = new HashMap();
            friendMap.put("friend_request/" + mCurrentUser.getUid() + "/" + user_id, null);
            friendMap.put("friend_request/" + user_id + "/" +mCurrentUser.getUid(), null);

            mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null){
                        mCurrent_state = "not_friends";
                        tvAddTag.setText("Add Friend");

                        btnDeclineRequest.setVisibility(View.INVISIBLE);
                        btnAcceptRequest.setVisibility(View.INVISIBLE);

                        imgBtnSendRequest.setVisibility(View.VISIBLE);
                        tvAddTag.setVisibility(View.VISIBLE);

                    }else{
                        Toast.makeText(ProfileActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    imgBtnSendRequest.setEnabled(true);
                }
            });
    }
}
});

    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        mUserRef.child("online").setValue("0");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentUser != null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
