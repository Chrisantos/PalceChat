package com.example.chrisantuseze.palcechat.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.R;
import com.example.chrisantuseze.palcechat.Utils.GroupMessages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by CHRISANTUS EZE on 09/12/2017.
 */

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.GroupMessageViewHolder> {
    private List<GroupMessages> mMessageLists;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String name;

    public GroupMessageAdapter(List<GroupMessages> mMessageLists) {
        this.mMessageLists = mMessageLists;
    }

    @Override
    public GroupMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_message_single_layout, parent, false);
        mAuth = FirebaseAuth.getInstance();
        return new GroupMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GroupMessageViewHolder holder, int position) {

        final GroupMessages c = mMessageLists.get(position);
        String from_user = c.getFrom();
        String message_type = c.getType();

        String current_user_id = mAuth.getCurrentUser().getUid();

        Log.d("GroupMessageAdapter", from_user + " " +current_user_id);


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();

                holder.timeView.setText(c.getTime());
                holder.messageText.setText(c.getMessage());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if (from_user != null){
            if (from_user.equals(current_user_id)){

                holder.relativeLayout.setBackgroundResource(R.drawable.message_current_user_back);
//            holder.messageText.setTextColor(Color.rgb(69,84,185));
                holder.messageText.setTextColor(Color.WHITE);
                holder.messageFrom.setText("me");

            }else{
                holder.messageFrom.setText(name);
            }

        }

    }

    @Override
    public int getItemCount() {
        return mMessageLists.size();
    }

    public class GroupMessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public TextView timeView;
        private TextView messageFrom;
        private RelativeLayout relativeLayout;

        public GroupMessageViewHolder(View itemView) {
            super(itemView);

            messageText = (TextView)itemView.findViewById(R.id.message);
            timeView = (TextView) itemView.findViewById(R.id.time);
            messageFrom = (TextView) itemView.findViewById(R.id.from_user);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.layout);
        }
    }
}
