package com.example.chrisantuseze.palcechat.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by CHRISANTUS EZE on 23/10/2017.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<String> data= Collections.emptyList();
    private ItemClickListener clickListener;
    private ItemLongClickListener longClickListener;


    // create constructor to initialize context and data sent from MainActivity
    public GroupAdapter(Context context, List<String> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
    }
    public void setClickListener(ItemClickListener itemClickListener){
        this.clickListener = itemClickListener;
    }
    public void setLongClickListener(ItemLongClickListener longClickListener){
        this.longClickListener = longClickListener;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.groups_single_layout, parent,false);
        GroupViewHolder holder=new GroupViewHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {

        String name = data.get(position);
        String firstLetter = name.substring(0,1).toUpperCase();
        holder.textName.setText(name);
        holder.roundText.setText(firstLetter);

        //Typeface custom_font2 = Typeface.createFromAsset((GroupChat.class).getAssets(),  "fonts/Aller_Rg.ttf");
        //holder.textName.setTypeface(custom_font2);

        switch (firstLetter){
            case "A":
                holder.roundText.setBackgroundResource(R.drawable.round_light_green);
                break;
            case "B":
                holder.roundText.setBackgroundResource(R.drawable.round_orange);
                break;
            case "C":
                holder.roundText.setBackgroundResource(R.drawable.round_purple);
                break;
            case "D":
                holder.roundText.setBackgroundResource(R.drawable.round_light_blue);
                break;
            case "E":
                holder.roundText.setBackgroundResource(R.drawable.round_green);
                break;
            case "F":
                holder.roundText.setBackgroundResource(R.drawable.round_group);
                break;
            case "G":
                holder.roundText.setBackgroundResource(R.drawable.round_edit);
                break;
            case "H":
                holder.roundText.setBackgroundResource(R.drawable.round_color_primary_dark);
                break;
            case "I":
                holder.roundText.setBackgroundResource(R.drawable.round_edit);
                break;
            case "J":
                holder.roundText.setBackgroundResource(R.drawable.round_purple);
                break;
            case "K":
                holder.roundText.setBackgroundResource(R.drawable.round_color_primary_dark);
                break;
            case "L":
                holder.roundText.setBackgroundResource(R.drawable.round_purple);
                break;
            case "M":
                holder.roundText.setBackgroundResource(R.drawable.round_orange);
                break;
            case "N":
                holder.roundText.setBackgroundResource(R.drawable.round_light_green);
                break;
            case "O":
                holder.roundText.setBackgroundResource(R.drawable.round_light_blue);
                break;
            case "P":
                holder.roundText.setBackgroundResource(R.drawable.round_group);
                break;
            case "Q":
                holder.roundText.setBackgroundResource(R.drawable.round_red);
                break;
            case "R":
                holder.roundText.setBackgroundResource(R.drawable.round_red);
                break;
            case "S":
                holder.roundText.setBackgroundResource(R.drawable.round_group);
                break;
            case "T":
                holder.roundText.setBackgroundResource(R.drawable.round_light_blue);
                break;
            case "U":
                holder.roundText.setBackgroundResource(R.drawable.round_edit);
                break;
            default: break;
        }

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data == null? 0:  data.size();
    }


    class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView textName, roundText;

        // create constructor to get widget reference
        public GroupViewHolder(View itemView) {
            super(itemView);
            textName= (TextView) itemView.findViewById(R.id.name);
            roundText= (TextView) itemView.findViewById(R.id.circleView);
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


        @Override
        public boolean onLongClick(View v) {
            if (longClickListener != null){
                longClickListener.onLongClick(v, getAdapterPosition());
                return true;
            }
            return false;
        }
    }

}