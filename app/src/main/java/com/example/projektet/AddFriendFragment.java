package com.example.projektet;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AddFriendFragment extends Fragment {

    List<MemberData> allMembers;
    Button addFriend;
    EditText nickName;
    DatabaseReference myRef;
    Listener listener;
    static int id = 0;

    public interface Listener {
        void changeFragment();
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if(context instanceof AddFriendFragment.Listener){
            this.listener = (Listener) context;
        }
        else{
            throw new RuntimeException(context.toString() + "must implement listener interface.");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_add_friend, container, false);

        addFriend = (Button) layout.findViewById(R.id.confirmAddFriend);
        nickName = (EditText) layout.findViewById(R.id.nickNameSearch);
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = myAuth.getCurrentUser();
        allMembers = fetchMembers();

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String wantedFriend = nickName.getText().toString().trim();
                for(MemberData member : allMembers){
                    if(member.getNickName().equals(wantedFriend)){
                        myRef = FirebaseDatabase.getInstance().getReference("users/"+ currentUser.getDisplayName() + "/Friends");
                        myRef.child(String.valueOf(id++)).setValue(member.getNickName());
                    }
                }
                listener.changeFragment();
            }
        });
        return layout;
    }

    private List<MemberData> fetchMembers(){
        List<MemberData> members = new ArrayList<MemberData>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                members.clear();
                for(DataSnapshot child : snapshot.getChildren()){
                    members.add(new MemberData(child.getKey()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return members;
    }
}