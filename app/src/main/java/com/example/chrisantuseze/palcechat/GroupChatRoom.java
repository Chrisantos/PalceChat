package com.example.chrisantuseze.palcechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.Adapters.GroupMessageAdapter;
import com.example.chrisantuseze.palcechat.Utils.GroupMessages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatRoom extends AppCompatActivity {
    private Button btn_send_msg;
    private EditText input_msg;
    private TextView chat_conversation;

    private DatabaseReference root ;
    private String temp_key;

    private RecyclerView mRecycler;
    private String mCurrentUserId;
    private String group_id, group_name;
    private Toolbar mToolbar;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private TextView mName;
    private CircleImageView circleImageView;
    private SwipeRefreshLayout mRefreshLayout;
    private ImageView imgViewProfile, imgClearChat;

    private ImageButton btnAdd, btnSend;
    private EditText etText;


    private final List<GroupMessages> messageLists = new ArrayList<>();
    private GroupMessageAdapter mAdapter;


    private DatabaseReference mUserRef;

    private static final int TOTAL_ITMES_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private LinearLayoutManager mLinearLayout;

    // New Solution

    int itemPos = 0;

    String mLastKey = "";
    String mPrevKey = "";
    Typeface custom_font2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_room);


        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.group_chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid() ;
        mRootRef = FirebaseDatabase.getInstance().getReference();

        group_name = getIntent().getExtras().get("group_name").toString();

        mRecycler = (RecyclerView)findViewById(R.id.recyclerview);
        mLinearLayout = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLinearLayout);
        mRecycler.setHasFixedSize(true);

        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);

        mAdapter = new GroupMessageAdapter(messageLists);
        mRecycler.setAdapter(mAdapter);

        custom_font2  = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");

        loadMessage();


        mName = (TextView)findViewById(R.id.name);
        imgViewProfile = (ImageView)findViewById(R.id.view_profile);
        imgClearChat = (ImageView)findViewById(R.id.clear_chat);

        imgClearChat.setEnabled(true);

        btnAdd = (ImageButton)findViewById(R.id.add);
        btnSend = (ImageButton)findViewById(R.id.send);
        etText = (EditText)findViewById(R.id.text_pane);

        mName.setText(group_name);
        mName.setTypeface(custom_font2);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etText.getText().toString().equals("")){
                    sendMessage();
                }
            }
        });
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos = 0;

                loadMoreMessage();
            }
        });

        imgViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatRoom.this, UsersActivity.class);
                intent.putExtra("group_name", group_name);
                startActivity(intent);
            }
        });

        imgClearChat.setOnClickListener(new View.OnClickListener() {
            @Override
public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("All_Groups")
                .child(group_name).setValue("");
                mAdapter.notifyDataSetChanged();

                imgClearChat.setEnabled(false);
            }
        });

    }


    private void loadMoreMessage(){

        DatabaseReference messageRef = mRootRef.child("All_Groups").child(group_name);

        //Fetches or queries the last 10 chats
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                GroupMessages message = dataSnapshot.getValue(GroupMessages.class);
                String messageKey = dataSnapshot.getKey();

                if (!mPrevKey.equals(messageKey)){
                    messageLists.add(itemPos++, message);
                }else{
                    mPrevKey = mLastKey;
                }

                if (itemPos == 1){
                    mLastKey = messageKey;
                }

                mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10, 0);

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

    }

    private void loadMessage() {

        DatabaseReference messageRef = mRootRef.child("All_Groups").child(group_name);

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITMES_TO_LOAD);

        messageQuery.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        GroupMessages message = dataSnapshot.getValue(GroupMessages.class);

                        itemPos++;
                        if (itemPos == 1){
                            String messageKey = dataSnapshot.getKey();
                            mLastKey = messageKey;
                            mPrevKey = messageKey;
                        }


                        messageLists.add(message);
                        mAdapter.notifyDataSetChanged();

                        mRecycler.scrollToPosition(messageLists.size()-1);

                        mRefreshLayout.setRefreshing(false);

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

    }
//
//     root.addChildEventListener(new ChildEventListener() {
//        @Override
//        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//            append_chat_conversation(dataSnapshot);
//        }
//
//        @Override
//        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            append_chat_conversation(dataSnapshot);
//
//        }
//
//        @Override
//        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//        }
//
//        @Override
//        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//
//        }
//    });
//
//}
//
//    private String chat_msg,chat_user_name;
//
//    private void append_chat_conversation(DataSnapshot dataSnapshot) {
//
//        Iterator i = dataSnapshot.getChildren().iterator();
//
//        while (i.hasNext()){
//
//            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
//            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
//
//            chat_conversation.append(chat_user_name +" : "+chat_msg +" \n");
//        }
//
//
//    }
    private void sendMessage() {
        String message = etText.getText().toString();
        if (!TextUtils.isEmpty(message)){

//            DatabaseReference groupMembers = mRootRef.child("All_Groups").child(group_name);
//            groupMembers.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    for (DataSnapshot dsp : dataSnapshot.getChildren()){
//                        String userId = dsp.getKey();
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

            String current_user_ref = "All_Groups/" + group_name + "/" +mCurrentUserId;
            String group_ref = "All_Groups/" + group_name;

//            DatabaseReference user_message_push = mRootRef.child("All_Groups").child(group_name)
//                    .child(mCurrentUserId).push();
            DatabaseReference user_message_push = mRootRef.child("All_Groups").child(group_name).push();
            String push_id = user_message_push.getKey();

            Date date = new Date();
            String dateformat = "HH:mm a";
            SimpleDateFormat sdf = new SimpleDateFormat(dateformat);

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("type", "text");
            messageMap.put("from", mCurrentUserId);
            messageMap.put("time", sdf.format(date));

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(group_ref + "/" +push_id, messageMap);

            etText.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null){

                    }
                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);

    }

}
