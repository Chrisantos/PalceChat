package com.example.chrisantuseze.palcechat.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.R;
import com.example.chrisantuseze.palcechat.Utils.Users;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by CHRISANTUS EZE on 13/12/2017.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<Users> usersList= Collections.emptyList();
    private ItemClickListener clickListener;

    public UsersAdapter(Context context, List<Users> usersList) {
        this.context = context;
        this.usersList = usersList;
    }
    public void setClickListener(ItemClickListener itemClickListener){
        this.clickListener = itemClickListener;
    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_single_layout, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersViewHolder holder, int position) {
        final Users users = usersList.get(position);
        holder.userNameView.setText(users.getName());
        holder.userStatusView.setText(users.getStatus());
        holder.setThumbImage(users.getThumb_image(), context);
    }


    @Override
    public int getItemCount() {
        return usersList == null? 0:  usersList.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView userNameView;
        TextView userStatusView;
        CircleImageView profilePic;

        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            userNameView = (TextView)itemView.findViewById(R.id.name);
            userStatusView = (TextView)itemView.findViewById(R.id.status);
            profilePic = (CircleImageView)itemView.findViewById(R.id.circleView);
            itemView.setOnClickListener(this);
            itemView.setTag(itemView);
        }
        public void setThumbImage(final String thumb_image, final Context applicationContext) {
            final CircleImageView circleImage = (CircleImageView)mView.findViewById(R.id.circleView);
            Picasso.with(applicationContext).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.user_avatar1)
                    .into(profilePic, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(applicationContext).load(thumb_image).placeholder(R.drawable.user_avatar1)
                                    .into(profilePic);

                        }
                    });

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
