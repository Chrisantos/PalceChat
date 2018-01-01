package com.example.chrisantuseze.palcechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.Fragments.FriendsFragment;
import com.example.chrisantuseze.palcechat.Utils.Friends;
import com.example.chrisantuseze.palcechat.Utils.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddMembers extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mRecycler;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    static Typeface custom_font2;

    private TextView tvNumUsers, tvUsers, tvName;
    private long numbUsers = 0;

    private DatabaseReference mRootRef, mUserRef, mFriends;
    private String mCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);

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
        tvName.setText("Add Members");


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");
        tvName.setTypeface(custom_font2);

        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mFriends = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUserId);
        mFriends.keepSynced(true);


        mRecycler = (RecyclerView)findViewById(R.id.recyclerview);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        start();


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static class AddMemberViewHolder extends RecyclerView.ViewHolder{


        //Used for setting onclicklistener of the recycler items
        View mView;

        DatabaseReference mDatabase, mFriendRequest, mFriendDatabase, mNotificationDatabase, requestTo;
        private FirebaseUser mCurrentUser;

        DatabaseReference mGroups, mFriends, mGroupDatabase, mRootRef;
        private String mCurrentUserId;

        private String mCurrent_state, mCurrentState;
        public AddMemberViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
            mDatabase.keepSynced(true);

            mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("All_Groups");

            mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("All_Groups");
            mRootRef = FirebaseDatabase.getInstance().getReference();
            mGroups = FirebaseDatabase.getInstance().getReference().child("groups").child(mCurrentUserId);

            mFriends = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUserId);

            mFriends.keepSynced(true);


            mFriendRequest = FirebaseDatabase.getInstance().getReference().child("friend_request");
            mFriendRequest.keepSynced(true);

            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

            mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
            mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

            requestTo = FirebaseDatabase.getInstance().getReference().child("request_to");

            mCurrent_state = "not_friends";
            mCurrentState = "not_member";


        }
//        public void sendRequest(final String user_id, int option){
//
//            final Button btnAddFriend = (Button)mView.findViewById(R.id.send_request);
//
//            if (option == 0){
//                btnAddFriend.setVisibility(View.INVISIBLE);
//                return;
//            }
//
//
//            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(
//                    new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.hasChild(user_id)){
//                                btnAddFriend.setVisibility(View.INVISIBLE);
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//            mFriendRequest.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(
//                    new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.hasChild(user_id)){
//                                String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
//                                if (request_type.equals("sent")) {
//
//                                    mCurrent_state = "request_sent";
//                                    btnAddFriend.setText("Cancel");
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    }
//            );
//
//
//            btnAddFriend.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    btnAddFriend.setEnabled(false);
//
//                    if (mCurrent_state.equals("not_friends")){
//
//
//                        mFriendRequest.child(mCurrentUser.getUid()).child(user_id).child("request_type")
//                                .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//
//                                if (task.isSuccessful()) {
//                                    mFriendRequest.child(user_id).child(mCurrentUser.getUid())
//                                            .child("request_type")
//                                            .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                            HashMap<String, String> notificationsData = new HashMap<>();
//                                            notificationsData.put("from", mCurrentUser.getUid());
//                                            notificationsData.put("type", "request");
//
//                                            mNotificationDatabase.child(user_id).push().setValue(notificationsData)
//                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//
//                                                            btnAddFriend.setEnabled(true);
//                                                            mCurrent_state = "request_sent";
//                                                            btnAddFriend.setText("Cancel");
//                                                            btnAddFriend.setTextColor(Color.GRAY);
//
//                                                            //...................Create another parent...................................
//
//                                                            requestTo = FirebaseDatabase.getInstance().getReference().child("request_to")
//                                                                    .child(mCurrentUser.getUid()).child(user_id);
//                                                            requestTo.setValue(user_id).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                @Override
//                                                                public void onComplete(@NonNull Task<Void> task) {
//
//                                                                }
//                                                            });
//
//
//                                                        }
//                                                    });
//                                        }
//                                    });
//                                } else {
//
//                                }
//                            }
//                        });
//
//                    }
//                    else if (mCurrent_state.equals("request_sent")){
//
//                        mFriendRequest.child(mCurrentUser.getUid()).child(user_id).removeValue()
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//
//                                        if (task.isSuccessful()) {
//                                            mFriendRequest.child(user_id).child(mCurrentUser.getUid())
//                                                    .removeValue().addOnCompleteListener(
//                                                    new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if (task.isSuccessful()) {
//                                                                mCurrent_state = "not_friends";
//                                                                btnAddFriend.setText("Add Friend");
//                                                                btnAddFriend.setTextColor(Color.rgb(69,84,185));
//
//                                                            } else {
//                                                            }
//
//                                                        }
//                                                    });
//                                        } else {}
//                                        btnAddFriend.setEnabled(true);
//                                    }
//                                });
//
//                    }
//
//                }
//            });
//
//        }
//
//

        public void addMember(final String user_id, final String group_name){

            final Button btnAddFriend = (Button)mView.findViewById(R.id.add_user);

            mRootRef.child("groups").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)){
                            mRootRef.child("groups").child(user_id).addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot1) {
                                    if (dataSnapshot1.hasChild(group_name)){
                                        btnAddFriend.setText("Remove");
                                        mCurrentState = "member";
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            }
                            );

                        }
                    }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    btnAddFriend.setEnabled(false);

                    if (mCurrentState.equals("not_member")){
                        mRootRef.child("groups").child(user_id).child(group_name).setValue("")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            mCurrentState = "member";
                                            btnAddFriend.setText("Remove");
                                            btnAddFriend.setTextColor(Color.GRAY);

                                            Map map2 = new HashMap();
                                            map2.put(group_name + "/" + user_id, "");
                                            mGroupDatabase.updateChildren(map2).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()){
                                                        mGroupDatabase.child(group_name).child(user_id);
                                                    }
                                                }
                                            });
                                        }
                                        btnAddFriend.setEnabled(true);
                                    }
                                });

                    }else  if (mCurrentState.equals("member")){
                        mRootRef.child("groups").child(user_id).removeValue().addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            mCurrentState = "not_member";
                                            btnAddFriend.setText("Select");
                                            btnAddFriend.setTextColor(Color.rgb(69,84,185));

                                            mGroupDatabase.child(group_name).child(user_id).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        mGroupDatabase.child(group_name).child(user_id);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                    }
                }
            });
        }


        public void setName(String name) {
            TextView userNameView = (TextView)mView.findViewById(R.id.name);
            userNameView.setText(name);
            userNameView.setTypeface(custom_font2);
        }

        public void setStatus(String status) {
            TextView userStatusView = (TextView)mView.findViewById(R.id.status);
            userStatusView.setText(status);
            userStatusView.setTypeface(custom_font2);
        }

        public void setThumbImage(final String thumb_image, final Context applicationContext) {
            final CircleImageView circleImage = (CircleImageView)mView.findViewById(R.id.circleView);
            Picasso.with(applicationContext).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
            .placeholder(R.drawable.user_avatar1)
            .into(circleImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(applicationContext).load(thumb_image).placeholder(R.drawable.user_avatar1)
                            .into(circleImage);

                }
            });

        }
    }

//    private void start(){
//        mDatabase.keepSynced(true);
//
//        FirebaseRecyclerAdapter<Users, AddMemberViewHolder> firebaseRecyclerAdapter =
//                new FirebaseRecyclerAdapter<Users, AddMemberViewHolder>(Users.class,
//                        R.layout.users_single_layout, AddMemberViewHolder.class, mDatabase) {
//                    @Override
//                    protected void populateViewHolder(AddMemberViewHolder viewHolder, Users model, int position) {
//                        final String key = getRef(position).getKey();
//
//                        if (!mCurrentUser.getUid().equals(key)){
//                            viewHolder.setName(model.getName());
//                            viewHolder.setStatus(model.getStatus());
//                            viewHolder.setThumbImage(model.getThumb_image(), getApplicationContext());
//                            viewHolder.sendRequest(key, 1);
//                        }else{
//                            viewHolder.setName(model.getName());
//                            viewHolder.setStatus(model.getStatus());
//                            viewHolder.setThumbImage(model.getThumb_image(), getApplicationContext());
//                            viewHolder.sendRequest(key, 0);
//                        }
//
//                    }
//                };
//        Log.e("Chris", ""+firebaseRecyclerAdapter);
//        mRecycler.setAdapter(firebaseRecyclerAdapter);
//        mDatabase.keepSynced(true);
//
//    }


    private void start(){
        mFriends.keepSynced(true);

        FirebaseRecyclerAdapter<Friends, AddMemberViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Friends, AddMemberViewHolder>(
                        Friends.class,
                        R.layout.add_members_single_layout,
                        AddMemberViewHolder.class,
                        mFriends)
                {
                    @Override
                    protected void populateViewHolder(final AddMemberViewHolder viewHolder, Friends model, int position) {
                        final String user_id = getRef(position).getKey();
                        final String group_name = getIntent().getExtras().get("group_name").toString();
                        mDatabase.keepSynced(true);

                        mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final String userName = dataSnapshot.child("name").getValue().toString();
                                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                                String status = dataSnapshot.child("status").getValue().toString();

                                viewHolder.setName(userName);
                                viewHolder.setThumbImage(thumb_image, getApplicationContext());
                                viewHolder.setStatus(status);
                                viewHolder.addMember(user_id, group_name);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                };
        Log.e("Chris", ""+firebaseRecyclerAdapter);
        mRecycler.setAdapter(firebaseRecyclerAdapter);
        mFriends.keepSynced(true);

    }

}
