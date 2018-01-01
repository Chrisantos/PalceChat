package com.example.chrisantuseze.palcechat.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.R;
import com.example.chrisantuseze.palcechat.Utils.Friends;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by CHRISANTUS EZE on 29/11/2017.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder> {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private Context context;

    private String image, online, name, status;
    private List<Friends> mFriends = Collections.emptyList();
    private ItemClickListener clickListener;

    public FriendsAdapter(List<Friends> mFriends) {
        this.mFriends = mFriends;
    }
    public void setClickListener(ItemClickListener itemClickListener){
        this.clickListener = itemClickListener;
    }

    @Override
    public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_single_layout,
                parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new FriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FriendsViewHolder holder, int position) {

        String current_user_id = mAuth.getCurrentUser().getUid();
        //String user_id = getRef(position).get
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                image = dataSnapshot.child("thumb_image").getValue().toString();
                status = dataSnapshot.child("status").getValue().toString();


                holder.tvName.setText(name);
                holder.tvStatus.setText(name);

                Picasso.with(holder.profileImage.getContext()).load(image).placeholder(R.drawable.user_avatar1)
                        .into(holder.profileImage);

                if (dataSnapshot.hasChild("online")){
                    String userOnline = dataSnapshot.child("online").getValue().toString();
                    holder.tvStatus.setText(userOnline);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mFriends == null? 0:  mFriends.size();
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvName;
        public TextView tvStatus;
        public TextView tvOnlineIcon;
        public CircleImageView profileImage;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView)itemView.findViewById(R.id.name);
            profileImage = (CircleImageView)itemView.findViewById(R.id.circleView);
            tvStatus = (TextView) itemView.findViewById(R.id.status);
            tvOnlineIcon = (TextView) itemView.findViewById(R.id.online_status);

            itemView.setOnClickListener(this);
            itemView.setTag(itemView);
        }

        // Click event for all items
        @Override
        public void onClick(View v) {
            if (clickListener != null){
                clickListener.onClick(v, getAdapterPosition());
            }

        }

    }
}
