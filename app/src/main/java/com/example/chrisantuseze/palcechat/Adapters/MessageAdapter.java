package com.example.chrisantuseze.palcechat.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.Utils.Messages;
import com.example.chrisantuseze.palcechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by CHRISANTUS EZE on 28/11/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String image;

    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        mAuth = FirebaseAuth.getInstance();

        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.sender_message_single_layout, parent, false);
                viewHolder = new SenderMessageViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.receiver_message_single_layout, parent, false);
                viewHolder = new ReceiverMessageViewHolder(viewChatOther);
                break;
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mMessageList.get(position).getFrom(),
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (TextUtils.equals(mMessageList.get(position).getFrom(),
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            configureSenderViewHolder((SenderMessageViewHolder) holder, position);
        } else {
            configureReceiverViewHolder((ReceiverMessageViewHolder) holder, position);
        }


    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class SenderMessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public TextView timeView;
        private ImageView imageMessage;
        private LinearLayout linearLayout;

        public SenderMessageViewHolder(View itemView) {
            super(itemView);

            messageText = (TextView)itemView.findViewById(R.id.message);
            timeView = (TextView) itemView.findViewById(R.id.time);
            //imageMessage = (ImageView)itemView.findViewById(R.id.image_message);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.layout);
        }
    }

    public class ReceiverMessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public TextView timeView;
        public CircleImageView profileImage;
        private ImageView imageMessage;
        private LinearLayout linearLayout;

        public ReceiverMessageViewHolder(View itemView) {
            super(itemView);

            messageText = (TextView)itemView.findViewById(R.id.message);
            profileImage = (CircleImageView)itemView.findViewById(R.id.circleView);
            timeView = (TextView) itemView.findViewById(R.id.time);
            //imageMessage = (ImageView)itemView.findViewById(R.id.image_message);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.layout);

        }
    }

    private void configureSenderViewHolder(final SenderMessageViewHolder holder, int position){

        final Messages c = mMessageList.get(position);
        String from_user = c.getFrom();
        String message_type = c.getType();

        String current_user_id = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();

                holder.timeView.setText(c.getTime());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (from_user.equals(current_user_id)){

            holder.linearLayout.setBackgroundResource(R.drawable.message_current_user_back);
//            holder.messageText.setTextColor(Color.rgb(69,84,185));
            holder.messageText.setTextColor(Color.WHITE);

        }

        if (message_type.equals("text")){

            holder.messageText.setText(c.getMessage());
//            holder.imageMessage.setVisibility(View.INVISIBLE);

        }else{
            holder.messageText.setVisibility(View.INVISIBLE);
        }

    }
    private void configureReceiverViewHolder(final ReceiverMessageViewHolder holder, int position){

        final Messages c = mMessageList.get(position);
        String from_user = c.getFrom();
        String to_user = c.getTo();
        String message_type = c.getType();

        String current_user_id = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                image = dataSnapshot.child("thumb_image").getValue().toString();

                holder.timeView.setText(c.getTime());

//                Picasso.with(holder.profileImage.getContext()).load(image).placeholder(R.drawable.user_avatar1)
//                        .into(holder.profileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (from_user.equals(current_user_id)){

            holder.linearLayout.setBackgroundResource(R.drawable.message_current_user_back);
//            holder.messageText.setTextColor(Color.rgb(69,84,185));
            holder.messageText.setTextColor(Color.WHITE);

        }

        if (message_type.equals("text")){

            holder.messageText.setText(c.getMessage());
        //    holder.imageMessage.setVisibility(View.INVISIBLE);

        }else{
            holder.messageText.setVisibility(View.INVISIBLE);

            Picasso.with(holder.profileImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.user_avatar1).into(holder.imageMessage);
        }

    }
}
