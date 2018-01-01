package com.example.chrisantuseze.palcechat.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.ChatActivity;
import com.example.chrisantuseze.palcechat.Utils.Friends;
import com.example.chrisantuseze.palcechat.ProfileActivity;
import com.example.chrisantuseze.palcechat.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {
    private RecyclerView mRecycler;
    private DatabaseReference mDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private FirebaseUser currentUser;
    static Typeface custom_font2;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth = FirebaseAuth.getInstance();
        String mCurrentUser = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUser);
        mDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.keepSynced(true);

        custom_font2 = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/Aller_Rg.ttf");

        mRecycler = (RecyclerView)view.findViewById(R.id.recyclerview);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        mUserRef.child("online").setValue("true");
        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter =
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                        Friends.class,
                        R.layout.friends_single_layout,
                        FriendsViewHolder.class,
                        mDatabase
                ) {
                    @Override
                    protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {
                        final String user_id = getRef(position).getKey();

                        mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String userName = dataSnapshot.child("name").getValue().toString();
                                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                                String status = dataSnapshot.child("status").getValue().toString();
                                if (dataSnapshot.hasChild("online")){
                                    String userOnline = dataSnapshot.child("online").getValue().toString();
                                    viewHolder.setOnlineIcon(userOnline);
                                }
                                viewHolder.setDisplayName(userName);
                                viewHolder.setThumbImage(thumb_image, getContext());
                                viewHolder.setStatus(status);
                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CharSequence options[] = new CharSequence[] {"Open Profile", "Send Message"};
                                        new AlertDialog.Builder(getContext()).setTitle("Select Options")
                                                .setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int position) {

                                                        if (position == 0){
                                                            Intent intent = new Intent(getContext(), ProfileActivity.class);
                                                            intent.putExtra("user_key", user_id);
                                                            startActivity(intent);
                                                        }else if (position == 1){
                                                            Intent intent = new Intent(getContext(), ChatActivity.class);
                                                            intent.putExtra("user_key", user_id);
                                                            intent.putExtra("user_name", userName);
                                                            startActivity(intent);
                                                        }

                                                    }
                                                }).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                };
        mRecycler.setAdapter(friendsRecyclerViewAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (currentUser != null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setStatus(String status){
            TextView tvdate = (TextView)mView.findViewById(R.id.status);
            tvdate.setText(status);
            tvdate.setTypeface(custom_font2);
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
        public void setOnlineIcon(String onlineStatus){
            ImageView onlineImage = (ImageView)mView.findViewById(R.id.online_status);
            if (onlineStatus.equals("0")){
                onlineImage.setImageResource(R.drawable.online);
            }
        }
    }

}
