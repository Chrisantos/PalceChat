package com.example.chrisantuseze.palcechat;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.Adapters.ItemClickListener;
import com.example.chrisantuseze.palcechat.Adapters.UsersAdapter;
import com.example.chrisantuseze.palcechat.Utils.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity implements ItemClickListener, SearchView.OnQueryTextListener {
    private Toolbar mToolbar;
    private RecyclerView mRecycler;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    static Typeface custom_font2;

    private TextView tvNumUsers, tvUsers;
    private long numbUsers = 0;
    SearchView searchView = null;

    private UsersAdapter mAdapter;
    private List<Users> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.all_users_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        tvNumUsers = (TextView)findViewById(R.id.numb_users);
        tvUsers = (TextView)findViewById(R.id.users);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        FirebaseDatabase.getInstance().goOnline();

        custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");

        mRecycler = (RecyclerView)findViewById(R.id.recyclerview);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

//        mAdapter = new UsersAdapter(this, usersList);
//        mRecycler.setAdapter(mAdapter);
//        mAdapter.setClickListener(this);

        start("");
        mDatabase.keepSynced(true);


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numbUsers = dataSnapshot.getChildrenCount();

                tvNumUsers.setText(numbUsers + " users");
                tvNumUsers.setTypeface(custom_font2);

                tvUsers.setText("Select User");
                tvUsers.setTypeface(custom_font2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.users_menu, menu);

//        MenuItem searchItem = menu.findItem(R.id.search);
//        SearchManager searchManager = (SearchManager) UsersActivity.this.getSystemService(Context.SEARCH_SERVICE);
//        if (searchItem != null) {
//            searchView = (SearchView) searchItem.getActionView();
//        }

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        start(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        start(newText);

        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view, int position) {
        final List<String> keys = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Users").addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            keys.add(snapshot.getKey());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//
//        Intent intent = new Intent(getApplicationContext(),GroupChatRoom.class);
//        Users users = usersList.get(position);
//        intent.putExtra("group_name", keys.get(position));
//        startActivity(intent);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{


        //Used for setting onclicklistener of the recycler items
        View mView;

        DatabaseReference mDatabase, mFriendRequest, mFriendDatabase, mNotificationDatabase, requestTo;
        private FirebaseUser mCurrentUser;

        private String mCurrent_state;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
            mDatabase.keepSynced(true);

            mFriendRequest = FirebaseDatabase.getInstance().getReference().child("friend_request");
            mFriendRequest.keepSynced(true);

            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

            mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
            mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");

            requestTo = FirebaseDatabase.getInstance().getReference().child("request_to");

            mCurrent_state = "not_friends";


        }
        public void sendRequest(final String user_id, int option){

           final Button btnAddFriend = (Button)mView.findViewById(R.id.send_request);

            if (option == 0){
                btnAddFriend.setVisibility(View.INVISIBLE);
                return;
            }


            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(user_id)){
                                btnAddFriend.setVisibility(View.INVISIBLE);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            mFriendRequest.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(user_id)){
                                String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                                if (request_type.equals("sent")) {

                                    mCurrent_state = "request_sent";
                                    btnAddFriend.setText("Cancel");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );


    btnAddFriend.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            btnAddFriend.setEnabled(false);

    if (mCurrent_state.equals("not_friends")){


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

                            btnAddFriend.setEnabled(true);
                            mCurrent_state = "request_sent";
                            btnAddFriend.setText("Cancel");
                            btnAddFriend.setTextColor(Color.GRAY);

                            //...................Create another parent...................................

                            requestTo = FirebaseDatabase.getInstance().getReference().child("request_to")
                                    .child(mCurrentUser.getUid()).child(user_id);
                            requestTo.setValue(user_id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });


                                }
                            });
                }
            });
        } else {

        }
                }
            });

            }
        else if (mCurrent_state.equals("request_sent")){

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
                                btnAddFriend.setText("Add Friend");
                                btnAddFriend.setTextColor(Color.rgb(69,84,185));

                            } else {
                            }

                                }
                            });
                } else {}
                    btnAddFriend.setEnabled(true);
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

    private void start(String searchText){
        mDatabase.keepSynced(true);

        //Query firebaseSearchQuery = mDatabase.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, UsersViewHolder>(Users.class,
                        R.layout.users_single_layout, UsersViewHolder.class, mDatabase) {
                    @Override
                    protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {
                        final String key = getRef(position).getKey();

                        if (!mCurrentUser.getUid().equals(key)){
                            viewHolder.setName(model.getName());
                            viewHolder.setStatus(model.getStatus());
                            viewHolder.setThumbImage(model.getThumb_image(), getApplicationContext());
                            viewHolder.sendRequest(key, 1);
                        }else{
                            viewHolder.setName(model.getName());
                            viewHolder.setStatus(model.getStatus());
                            viewHolder.setThumbImage(model.getThumb_image(), getApplicationContext());
                            viewHolder.sendRequest(key, 0);
                        }



                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(UsersActivity.this, ProfileActivity.class);
                                intent.putExtra("user_key", key);
                                startActivity(intent);

                            }
                        });




                    }
                };
        Log.e("Chris", ""+firebaseRecyclerAdapter);
        mRecycler.setAdapter(firebaseRecyclerAdapter);
        mDatabase.keepSynced(true);

    }
}
