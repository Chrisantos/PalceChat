package com.example.chrisantuseze.palcechat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.Adapters.MessageAdapter;
import com.example.chrisantuseze.palcechat.Utils.GetTimeAgo;
import com.example.chrisantuseze.palcechat.Utils.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("VisibleForTests")
public class ChatActivity extends AppCompatActivity {
    private String user_id, userName;
    private Toolbar mToolbar;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private TextView mName, mLastSeen;
    private CircleImageView circleImageView;
    private SwipeRefreshLayout mRefreshLayout;
    private ImageView imgViewProfile, imgClearChat;

    private ImageButton btnAdd, btnSend;
    private EditText etText;

    private RelativeLayout parentLayout;

    private String mCurrentUserId;
    private RecyclerView mRecycler;

    private final List<Messages> messageList = new ArrayList<>();
    private MessageAdapter mAdapter;

    private DatabaseReference mMessageDatabase;

    private FirebaseUser currentUser;
    private DatabaseReference mUserRef;
    private StorageReference mImageStorage;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabase;

    private static final int TOTAL_ITMES_TO_LOAD = 5;
    private int mCurrentPage = 1;

    private LinearLayoutManager mLinearLayout;

    // New Solution

    int itemPos = 0;

    String mLastKey = "";
    String mPrevKey = "";

    private static final int GALLERY_PICK = 1;
    Typeface custom_font2;

    private static final int GALLERY_PICK_1 = 1;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = mCurrentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUserRef.keepSynced(true);

        user_id = getIntent().getStringExtra("user_key");
        userName = getIntent().getStringExtra("user_name");

        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        mRecycler = (RecyclerView)findViewById(R.id.recyclerview);
        mLinearLayout = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLinearLayout);
        mRecycler.setHasFixedSize(true);

        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.message_swipe_layout);

        mAdapter = new MessageAdapter(messageList);
        mRecycler.setAdapter(mAdapter);

        custom_font2  = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");

        loadMessage();


        mName = (TextView)findViewById(R.id.name);
        mLastSeen = (TextView)findViewById(R.id.online_status);
        circleImageView = (CircleImageView)findViewById(R.id.circleView);
        imgViewProfile = (ImageView)findViewById(R.id.view_profile);
        imgClearChat = (ImageView)findViewById(R.id.clear_chat);

        imgClearChat.setEnabled(true);

        btnAdd = (ImageButton)findViewById(R.id.add);
        btnSend = (ImageButton)findViewById(R.id.send);
        etText = (EditText)findViewById(R.id.text_pane);

        parentLayout = (RelativeLayout)findViewById(R.id.parent_layout);

        mName.setText(userName);
        mName.setTypeface(custom_font2);

        //------------Setting Online/Last seen----------------------------

        mRootRef.child("Users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();

                final String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                Picasso.with(ChatActivity.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.user_avatar1)
                        .into(circleImageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                Picasso.with(ChatActivity.this).load(thumb_image).placeholder(R.drawable.user_avatar1)
                                        .into(circleImageView);

                            }
                        });

                if (dataSnapshot.hasChild("background_image")){
                    String background_image = dataSnapshot.child("background_image").toString();

                    if (!background_image.equals("default")){
                        Picasso.with(ChatActivity.this).load(background_image).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                parentLayout.setBackground(new BitmapDrawable(bitmap));
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                    }

                }


                if (online.equals("0")){
                    mLastSeen.setText("online");
                }else if (!online.equals("0")){
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastSeen = Long.parseLong(online);
                    String lastSeenTime = getTimeAgo.getTimeAgo(lastSeen);

                    mLastSeen.setText(lastSeenTime);
                    mLastSeen.setTypeface(custom_font2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)){
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/" +user_id, chatAddMap);
                    chatUserMap.put("Chat/" + user_id + "/" + mCurrentUserId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null){
                                Log.d("CHAT LOG", databaseError.getMessage());
                            }

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //---------------------Chat Send----------------------------
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!etText.getText().toString().equals("")){
                    sendMessage();
                }



            }
        });

        //---------------Chat Add------------------------------------
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                Intent gallery = new Intent();
//                gallery.setType("image/*");
//                gallery.setAction(Intent.ACTION_GET_CONTENT);
//
//                startActivityForResult(Intent.createChooser(gallery, "Select Image"), GALLERY_PICK);

            }
        });

        imgViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                intent.putExtra("user_key", user_id);
                startActivity(intent);

            }
        });

        imgClearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference().child("messages")
                        .child(mCurrentUserId).child(user_id).setValue("");
                mAdapter.notifyDataSetChanged();

                imgClearChat.setEnabled(false);
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


    }

    private void loadMoreMessage(){

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(user_id);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if (!mPrevKey.equals(messageKey)){
                    messageList.add(itemPos++, message);
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

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(user_id);

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITMES_TO_LOAD);

        messageQuery.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Messages message = dataSnapshot.getValue(Messages.class);

                        itemPos++;
                        if (itemPos == 1){
                            String messageKey = dataSnapshot.getKey();
                            mLastKey = messageKey;
                            mPrevKey = messageKey;
                        }

                        Log.e("ChatsFragment", message.getMessage());

                        messageList.add(message);
                        mAdapter.notifyDataSetChanged();

                        mRecycler.scrollToPosition(messageList.size()-1);

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


    private void sendMessage() {
        String message = etText.getText().toString();
        if (!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + mCurrentUserId + "/" +user_id;
            String chat_user_ref = "messages/" + user_id + "/" +mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(user_id).push();
            String push_id = user_message_push.getKey();

                    Date date = new Date();
            String dateformat = "HH:mm a";
            SimpleDateFormat sdf = new SimpleDateFormat(dateformat);

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", sdf.format(date));
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" +push_id, messageMap);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            final String current_user_ref = "messages/" + mCurrentUserId + "/" +user_id;
            final String chat_user_ref = "messages/" + user_id + "/" +mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(user_id).push();

            final String push_id = user_message_push.getKey();

            StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        //we get the download url and store it the the database
                        String download_url = task.getResult().getDownloadUrl().toString();

                        Map messageMap = new HashMap();
                        messageMap.put("message", download_url);
                        messageMap.put("seen", false);
                        messageMap.put("type", "image");
                        messageMap.put("time", ServerValue.TIMESTAMP);
                        messageMap.put("from", mCurrentUserId);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" +push_id, messageMap);

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
            });

        }
//        else{
//
//            if (requestCode == GALLERY_PICK_1 && resultCode == RESULT_OK){
//                Uri imageUri = data.getData();
//                CropImage.activity(imageUri).start(ChatActivity.this);
//            }
//            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//                if (resultCode == RESULT_OK){
//                    dialog = new ProgressDialog(ChatActivity.this);
//                    dialog.setTitle("Uploading Background");
//                    dialog.setMessage("Please wait...");
//                    dialog.show();
//                    dialog.setCanceledOnTouchOutside(false);
//
//                    Uri resultUri = result.getUri();
//                    //Getting the file
//                    File thumb_filePath = new File(resultUri.getPath());
//                    String currentUserID = mCurrentUser.getUid();
//
//                    //Compressing the image
//                    Bitmap thumb_bitmap = new Compressor(this)
//                            .setMaxWidth(400)
//                            .setMaxHeight(350)
//                            .setQuality(75)
//                            .compressToBitmap(thumb_filePath);
//
//                    //To store byte array into firebase
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    final byte[] thumb_byte = baos.toByteArray();
//
//
//                    final StorageReference thumb_filepath = mImageStorage.child("background_image").child("thumb").child(currentUserID + ".jpg");
//                    UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
//
//
//                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @SuppressWarnings("VisibleForTests")
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
//
//                            String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
//                            if (thumb_task.isSuccessful()){
//
////                                        Map<String, Object> update_map = new HashMap();
//                                Map update_map = new HashMap();
//                                update_map.put("background_image", thumb_downloadUrl);
//
//                                mDatabase.updateChildren(update_map)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                                if (task.isSuccessful()){
//
//                                                    dialog.dismiss();
//                                                    Toast.makeText(ChatActivity.this,
//                                                            "Success uploading", Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
//                            }else{
//                                dialog.dismiss();
//                                Toast.makeText(ChatActivity.this,
//                                        "Error uploading", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                    ;
//
//                }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
//                    Exception  error = result.getError();
//                }
//            }
//
//        }
    }




    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        mUserRef.child("online").setValue("0");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentUser != null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
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
