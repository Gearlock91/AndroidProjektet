package com.example.projektet;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Fragmentet som hanterar när man adderar nya vänner till appen.
 * Denna klass skapa även nyckelparet som behövs vid krypteringen.
 * {@link KeyGen}
 * @author Andreas Roghe, Sofia Ågren
 * @version 2020-01-05
 */

public class AddFriendFragment extends Fragment {

    private List<MemberData> allMembers;
    private Button addFriend;
    private EditText nickName;
    private DatabaseReference myRef;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_add_friend, container, false);

        addFriend = (Button) layout.findViewById(R.id.confirmAddFriend);
        nickName = (EditText) layout.findViewById(R.id.nickNameSearch);
        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = myAuth.getCurrentUser();
        allMembers = fetchMembers();

        try {
            SQLiteOpenHelper sqlCryptoHelper = new SqlCryptoHelper(layout.getContext());
            db = sqlCryptoHelper.getWritableDatabase();

        } catch (SQLiteException e) {
            Toast.makeText(layout.getContext(), "Database unavailable", Toast.LENGTH_LONG).show();
        }

        addFriend.setOnClickListener(v -> {
            boolean successful = false;
            String wantedFriend = nickName.getText().toString().trim();
            for (MemberData member : allMembers) {
                if (member.getNickName().equals(wantedFriend)) {
                    KeyGen gen = new KeyGen();
                    assert currentUser != null;
                    myRef = FirebaseDatabase.getInstance().getReference("users/" + currentUser.getDisplayName() + "/Friends");
                    myRef.child(member.getNickName()).child("PubKey").setValue(Base64.getEncoder().encodeToString(gen.getPuk().getEncoded()));

                    ContentValues friendsValues = new ContentValues();
                    friendsValues.put("FRIEND", member.getNickName());
                    friendsValues.put("PRIVATE_KEY", Base64.getEncoder().encodeToString(gen.getPik().getEncoded()));
                    db.insert("CRYPTOLEDGER", null, friendsValues);

                    assert getFragmentManager() != null;
                    getFragmentManager().popBackStack();
                    Toast success = Toast.makeText(layout.getContext(), "Success!", Toast.LENGTH_SHORT);
                    success.show();
                    successful = true;
                }
            }
            if (!successful) {
                Toast invalidMember = Toast.makeText(layout.getContext(), "This member does not exist!", Toast.LENGTH_SHORT);
                invalidMember.show();
                nickName.setText("");
            }

        });
        return layout;
    }

    private List<MemberData> fetchMembers() {
        List<MemberData> members = new ArrayList<MemberData>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                members.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
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