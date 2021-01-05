package com.example.projektet;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class RegisterFragment extends Fragment {

    FirebaseAuth mAuth;
    Button registerButton;
    EditText email;
    EditText nickName;
    EditText password;
    Activity activity;
    List<MemberData> membersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference members = database.getReference().child("users/");
        membersList = new ArrayList<MemberData>();

        members.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    membersList.add(new MemberData(child.getKey(), child.getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createAccount(MemberData member) {
        if (member != null) {
            if ((member.getEmail() == null || member.getEmail().isEmpty()) || (member.getPassword() == null || member.getPassword().isEmpty()) || (member.getNickName() == null || member.getNickName().isEmpty())) {
                Toast.makeText(activity, "All fields must be filled in.", Toast.LENGTH_SHORT).show();
                email.setText("");
                password.setText("");
                nickName.setText("");
            } else {
                if (member.getPassword().length() < 6) {
                    Toast.makeText(activity, "Password needs to be over 6 digits.", Toast.LENGTH_SHORT).show();
                    password.setText("");
                } else {
                    mAuth.createUserWithEmailAndPassword(member.getEmail(), member.getPassword())
                            .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information

                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(member.getNickName()).build();
                                        user.updateProfile(profileUpdates);
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference myRef = database.getReference("users");
                                        myRef.child(member.getNickName()).child("email").setValue(member.getEmail());
                                        Toast.makeText(activity, "Account Created!.",
                                                Toast.LENGTH_LONG).show();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(activity, "Authentication failed.",
                                                Toast.LENGTH_LONG).show();
                                        updateUI(null);
                                    }
                                }
                            });
                }
            }
        } else {
            Toast.makeText(activity, "Something went terribly wrong, try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_register, container, false);
        activity = getActivity();
        mAuth = FirebaseAuth.getInstance();
        registerButton = (Button) layout.findViewById(R.id.registerUserButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemberData newMember = null;
                boolean exists = false;
                email = (EditText) layout.findViewById(R.id.registerEmail);
                password = (EditText) layout.findViewById(R.id.registerPassword);
                String providedEmail = email.getText().toString().trim();
                String providedPassword = password.getText().toString().trim();
                nickName = (EditText) layout.findViewById(R.id.registerNickname);
                String providedNickName = nickName.getText().toString().trim();
                for (MemberData checkMember : membersList) {
                    if (providedNickName.equals(checkMember.getNickName())) {
                        Toast.makeText(activity, "Member already exists.", Toast.LENGTH_SHORT).show();
                        exists = true;
                    }
                }
                if (exists != true) {
                    newMember = new MemberData(providedNickName, providedEmail, providedPassword);
                    createAccount(newMember);
                }
            }
        });
        return layout;
    }
}