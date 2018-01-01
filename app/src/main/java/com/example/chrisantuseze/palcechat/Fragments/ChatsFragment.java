package com.example.chrisantuseze.palcechat.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.ChatActivity;
import com.example.chrisantuseze.palcechat.Utils.Friends;
import com.example.chrisantuseze.palcechat.R;
import com.example.chrisantuseze.palcechat.UsersActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private FloatingActionButton fab;

    private RecyclerView mRecycler;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;

    static Typeface custom_font2;

    private String mCurrentUserId;

    int itemPos = 0;

    String mLastKey = "";
    String mPrevKey = "";


    private final List<String> lastMessageList = new ArrayList<>();
    private final List<String> timeList = new ArrayList<>();





    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        mAuth = FirebaseAuth.getInstance();
        String mCurrentUser = mAuth.getCurrentUser().getUid();
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUser);
        mMessageDatabase.keepSynced(true);

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mMessageDatabase.keepSynced(true);
        mRootRef.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        custom_font2 = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/Aller_Rg.ttf");

        mRecycler = (RecyclerView)view.findViewById(R.id.recyclerview);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));


        fab = (FloatingActionButton)view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), UsersActivity.class));
            }
        });



        //loadMessage();




        final DatabaseReference myChildren = mRootRef.child("messages").child(mCurrentUserId);

        myChildren.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                    Query messageQuery = myChildren.child(dsp.getKey()).limitToLast(1);
                    myChildren.keepSynced(true);


                    messageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            Log.e("My Message", ""+myChildren.child(dataSnapshot1.getKey()));

                            for (DataSnapshot dsp1 : dataSnapshot1.getChildren()) {
                                Log.e("My Message", dsp1.getValue().toString());


                                String message = dsp1.child("message").getValue().toString();
                                String time = dsp1.child("time").getValue().toString();

                                Log.e("My Message", message + " " + time);
                                timeList.add(time);
                                lastMessageList.add(message); //add result into array list
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        start();

        myChildren.keepSynced(true);

        return view;
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setLastMessage(String lastMessage){
            TextView tvlastmessage = (TextView)mView.findViewById(R.id.last_message);
            tvlastmessage.setText(lastMessage);
            tvlastmessage.setTypeface(custom_font2);
        }
        public void setDisplayName(String name){
            TextView userNameView = (TextView)mView.findViewById(R.id.name);
            userNameView.setText(name);
            userNameView.setTypeface(custom_font2);

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
        public void setTimeSent(String timeSent){
            TextView tvTimeSent = (TextView) mView.findViewById(R.id.time_sent);
            tvTimeSent.setText(timeSent);
        }
    }

    private void start(){
        mMessageDatabase.keepSynced(true);

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter =
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                        Friends.class,
                        R.layout.chats_history_single_layout,
                        FriendsViewHolder.class,
                        mMessageDatabase
                ) {
                    @Override
                    protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {
                        final String user_id = getRef(position).getKey();

                        final String lastMessage;
                        final String time;
                        if (lastMessageList.size()>0){
                            lastMessage = lastMessageList.get(position);

                        }else{
                            lastMessage = "Will soon be there";
                        }
                        if (timeList.size()>0){
                            time= timeList.get(position);
                        }else{
                            time = "01:17 am";
                        }

                        mUsersDatabase.keepSynced(true);

                        mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String userName = dataSnapshot.child("name").getValue().toString();
                                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                                viewHolder.setDisplayName(userName);
                                viewHolder.setThumbImage(thumb_image, getContext());
                                viewHolder.setLastMessage(lastMessage);
                                viewHolder.setTimeSent(time);
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

        mUsersDatabase.keepSynced(true);
        mRecycler.setAdapter(friendsRecyclerViewAdapter);

    }

//    private void loadMessage() {
//
//        String userId = mRootRef.child("messages").child(mCurrentUserId).getKey();
//
//        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(userId);
//
//        Query messageQuery = messageRef.limitToLast(1);
//
//        messageQuery.addChildEventListener(
//                new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                        Messages message = dataSnapshot.getValue(Messages.class);
//
//                        time = dataSnapshot.child("time").toString();
//
//                        itemPos++;
//                        if (itemPos == 1){
//                            String messageKey = dataSnapshot.getKey();
//                            mLastKey = messageKey;
//                            mPrevKey = messageKey;
//                        }
//
//                        lastMessage = message.getMessage();
//
//                        Toast.makeText(getContext(), lastMessage + "Chris", Toast.LENGTH_SHORT).show();
//                        Log.e("ChatsFragment", lastMessage);
//
////                        messageList.add(message);
////                        mAdapter.notifyDataSetChanged();
////
////                        mRecycler.scrollToPosition(messageList.size()-1);
////
////                        mRefreshLayout.setRefreshing(false);
//
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//    }


}
