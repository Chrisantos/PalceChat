package com.example.chrisantuseze.palcechat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.Adapters.GroupAdapter;
import com.example.chrisantuseze.palcechat.Adapters.ItemClickListener;
import com.example.chrisantuseze.palcechat.Adapters.ItemLongClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GroupChat extends AppCompatActivity implements ItemClickListener, ItemLongClickListener{

    private FloatingActionButton mFabAddGroup;

    private ArrayAdapter<String> arrayAdapter;
    private DatabaseReference mGroupDatabase, mGroupDatabaseReference;

    private String mCurrentUser;
    private DatabaseReference mRootRef;

    Toolbar mToolbar;
    private TextView tvName;
    private List<String> mData = new ArrayList<>();
    private GroupAdapter mAdapter;
    private RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.main_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        tvName = (TextView)findViewById(R.id.users);
        tvName.setText("Group");

        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/Aller_Rg.ttf");
        tvName.setTypeface(custom_font2);


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("All_Groups");

        mRecycler = (RecyclerView)findViewById(R.id.recyclerview);
        mAdapter = new GroupAdapter(this, mData);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
        mAdapter.setClickListener(this);

        //listView = (ListView) findViewById(R.id.listView);

        mFabAddGroup = (FloatingActionButton)findViewById(R.id.fab);


        mFabAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupChat.this, CreateGroup.class));
            }
        });

        mGroupDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                mData.clear();
                mData.addAll(set);

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        root.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                Set<String> set = new HashSet<String>();
//                Iterator i = dataSnapshot.getChildren().iterator();
//
//                while (i.hasNext()){
//                    set.add(((DataSnapshot)i.next()).getKey());
//                }
//
//                list_of_rooms.clear();
//                list_of_rooms.addAll(set);
//
//                arrayAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                Intent intent = new Intent(getApplicationContext(),GroupChatRoom.class);
//                intent.putExtra("group_name",((TextView)view).getText().toString() );
//                startActivity(intent);
//            }
//        });

    }

//    private void request_user_name() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Enter name:");
//
//        final EditText input_field = new EditText(this);
//
//        builder.setView(input_field);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                name = input_field.getText().toString();
//            }
//        });
//
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
//                request_user_name();
//            }
//        });
//
//        builder.show();
//    }
//

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(getApplicationContext(),GroupChatRoom.class);
        intent.putExtra("group_name", mData.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View view, int position) {
        String groupName = mData.get(position);
        FirebaseDatabase.getInstance().getReference().child("All_Groups")
                .child(groupName).removeValue();
        mAdapter.notifyDataSetChanged();
        return true;
    }
}
