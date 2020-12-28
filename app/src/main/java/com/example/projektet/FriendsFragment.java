package com.example.projektet;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    List<MemberData> fListDatabase;
    ListView friendsList;
    FirebaseAuth mAuth;
    ArrayAdapter<String> arrayAdapter;
    private Listener listener;

    public interface Listener {
        void switchToChat();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if(context instanceof FriendsFragment.Listener){
            this.listener = (FriendsFragment.Listener) context;
        }
        else{
            throw new RuntimeException(context.toString() + "must implement listener interface.");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_friends, container, false);
        fListDatabase = new ArrayList<MemberData>();
        friendsList = (ListView) layout.findViewById(R.id.friends_List);
        arrayAdapter = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_list_item_1);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users/" + currentUser.getDisplayName() +"/Friends");


        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.switchToChat();
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fListDatabase.clear();
                arrayAdapter.clear();
                MemberData friend = null;
               for(DataSnapshot child : snapshot.getChildren()){
                  friend = new MemberData(child.getValue().toString());
                  fListDatabase.add(friend);
               }
                for(MemberData f : fListDatabase){
                    arrayAdapter.add(f.getNickName());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        friendsList.setAdapter(arrayAdapter);
        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();
        fListDatabase.clear();
        arrayAdapter.clear();
    }
}