package com.example.chrisantuseze.palcechat.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.Adapters.FriendsAdapter;
import com.example.chrisantuseze.palcechat.Adapters.ItemClickListener;
import com.example.chrisantuseze.palcechat.ChatActivity;
import com.example.chrisantuseze.palcechat.GroupChat;
import com.example.chrisantuseze.palcechat.R;
import com.example.chrisantuseze.palcechat.Utils.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements ItemClickListener {

    private RecyclerView mRecycler;
    private DatabaseReference mFriendRequest, mFriends, mUserRef;
    private FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase;

    static Typeface custom_font2;

    private String mCurrentUserId;

    private List<String> requestList = new ArrayList<>();
    private List<Friends> mData = new ArrayList<>();
    private FriendsAdapter mAdapter;

    private FloatingActionButton fab;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);


        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.keepSynced(true);


        mFriendRequest = FirebaseDatabase.getInstance().getReference().child("request_to")
                .child(mCurrentUserId);

        mFriends = FirebaseDatabase.getInstance().getReference().child("Friends")
                .child(mCurrentUserId);

        mFriendRequest.keepSynced(true);

        mUserRef.child("online").setValue("0");

        custom_font2 = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/Aller_Rg.ttf");

        mRecycler = (RecyclerView)view.findViewById(R.id.recyclerview);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new FriendsAdapter(mData);
        mAdapter.setClickListener(this);

        fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), GroupChat.class));
            }
        });

        start();

        return view;
    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setStatus(String status) {
            TextView userStatusView = (TextView)mView.findViewById(R.id.status);
            userStatusView.setText(status);
            userStatusView.setTypeface(custom_font2);
        }
        public void setDisplayName(String name){
            TextView userNameView = (TextView)mView.findViewById(R.id.name);
            userNameView.setText(name);
            userNameView.setTypeface(custom_font2);

        }
        public void setThumbImage(final String thumb_image, final Context applicationContext) {
            final CircleImageView circleImage = (CircleImageView)mView.findViewById(R.id.circleView);
            Picasso.with(applicationContext).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.user_avatar1).into(circleImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {

                    Picasso.with(applicationContext).load(thumb_image)
                            .placeholder(R.drawable.user_avatar1).into(circleImage);

                }
            });

        }
        public void setOnlineIcon(String onlineStatus){
            ImageView onlineImage = (ImageView)mView.findViewById(R.id.online_status);
            if (onlineStatus.equals("0")){
                onlineImage.setImageResource(R.drawable.online);
            }
        }
    }

    private void start(){
        mFriends.keepSynced(true);

        FirebaseRecyclerAdapter<Friends, RequestViewHolder> friendsRecyclerViewAdapter =
                new FirebaseRecyclerAdapter<Friends, RequestViewHolder>(
                        Friends.class,
                        R.layout.friends_single_layout,
                        RequestViewHolder.class,
                        mFriends
                ) {
                    @Override
                    protected void populateViewHolder(final RequestViewHolder viewHolder, Friends model, int position) {
                        final String user_id = getRef(position).getKey();

                        mUserDatabase.keepSynced(true);

                        mUserDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                    final String userName = dataSnapshot.child("name").getValue().toString();
                                    String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                                    String status = dataSnapshot.child("status").getValue().toString();
                                    String userOnline = dataSnapshot.child("online").getValue().toString();

                                    viewHolder.setOnlineIcon(userOnline);
                                    viewHolder.setDisplayName(userName);
                                    viewHolder.setThumbImage(thumb_image, getContext());
                                    viewHolder.setStatus(status);

                                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(getContext(), ChatActivity.class);
                                            intent.putExtra("user_key", user_id);
                                            intent.putExtra("user_name", userName);
                                            startActivity(intent);

                                        }
                                    });


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                };

        mFriends.keepSynced(true);
        mRecycler.setAdapter(friendsRecyclerViewAdapter);


    }

    @Override
    public void onClick(View view, int position) {
//        Intent intent = new Intent(getApplicationContext(),GroupChatRoom.class);
//        intent.putExtra("group_name", mData.get(position));
//        startActivity(intent);
    }

    private void requests(){


//            mRootRef.child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    if (dataSnapshot.hasChild(user_id)) {
//                        String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
//                        if (request_type.equals("received")) {
//                            mCurrent_state = "req_received";
//                            btnSendRequest.setText("Accept Friend Request");
//                            btnDeclineRequest.setVisibility(View.VISIBLE);
//                            btnSendRequest.setTextColor(getResources()
//                                    .getColor(R.color.white));
//                            btnSendRequest.setBackgroundResource(R.drawable.button_change_status);
//                        } else if (request_type.equals("sent")) {
//                            mCurrent_state = "request_sent";
//                            btnSendRequest.setText("Cancel Friend Request");
//                            btnSendRequest.setBackgroundResource(R.drawable.button_change_status);
//                            btnSendRequest.setTextColor(getResources()
//                                    .getColor(R.color.white));
//                            btnDeclineRequest.setVisibility(View.INVISIBLE);
//                            btnDeclineRequest.setEnabled(false);
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//
//
//            mFriendRequest.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(final DataSnapshot dataSnapshot) {
//
//                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
//
//                        requestList.add(dsp.getKey());
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });



    }
    @Override
    public void onStop() {
        super.onStop();
        if (mCurrentUserId != null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
