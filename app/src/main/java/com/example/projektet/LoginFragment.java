package com.example.projektet;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;

    List<MemberData> allMembers;

    String neededEmail = null;
    EditText nickName;
    EditText password;

    Button registerButton;
    Button loginButton;

    Activity activity;

    DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void updateUI(FirebaseUser currentUser) {
        Log.d(TAG, "CURRENT USER DISPLAY NAME" + currentUser.getDisplayName());
        if(currentUser != null){
          Intent intent = new Intent(activity, HeadActivity.class);
          startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_login, container, false);
        activity = getActivity();
        allMembers = new ArrayList<MemberData>();
        registerButton = (Button) layout.findViewById(R.id.registerButton);
        loginButton = (Button) layout.findViewById(R.id.loginButton);
        nickName = (EditText) layout.findViewById(R.id.loginNickname);
        password = (EditText) layout.findViewById(R.id.editTextTextPassword);

        allMembers = fetchMembers();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String providedNick = nickName.getText().toString().trim();
                String providedPass = password.getText().toString().trim();
                String email = null;

                for(MemberData member : allMembers){
                    if(member.getNickName().equals(providedNick)){
                       email = member.getEmail();
                       Log.d(TAG,"Email found:" + email);
                    }
                }

                if(!email.isEmpty() && !providedPass.isEmpty()){
                    signIn(email, providedPass);
                }else{
                    Toast.makeText(activity, "Check email or password.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterFragment registerFragment = new RegisterFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, registerFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return layout;
    }

    private void signIn(String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed: Check email or password.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });


    }
    private List<MemberData> fetchMembers(){
        List<MemberData> members = new ArrayList<MemberData>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()){
                    child.getChildren().forEach(key -> {
                        String email = null;
                        if(key.getKey().equals("email")){
                            email = key.getValue().toString();
                        }
                        members.add(new MemberData(child.getKey(), email));
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return members;
    }

}