package com.example.chrisantuseze.palcechat.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chrisantuseze.palcechat.CreateGroup;
import com.example.chrisantuseze.palcechat.GroupChatRoom;
import com.example.chrisantuseze.palcechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private FloatingActionButton mFabAddGroup;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private String name;
    private DatabaseReference mGroupDatabase, mGroupDatabaseReference;

    private String mCurrentUser;
    private DatabaseReference mRootRef;



    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRootRef = FirebaseDatabase.getInstance().getReference().child("groups").child(mCurrentUser);
        mGroupDatabase = FirebaseDatabase.getInstance().getReference().child("All_Groups");

        listView = (ListView)view. findViewById(R.id.listView);

        mFabAddGroup = (FloatingActionButton)view. findViewById(R.id.fab);

        arrayAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,list_of_rooms);

        listView.setAdapter(arrayAdapter);

        mFabAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateGroup.class));
            }
        });


        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                list_of_rooms.clear();
                list_of_rooms.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getContext(),GroupChatRoom.class);
                intent.putExtra("group_name",((TextView)view).getText().toString() );
                startActivity(intent);
            }
        });


        return view;
    }

}
