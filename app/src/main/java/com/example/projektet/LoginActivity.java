package com.example.projektet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

/**
 * Denna klass handlar om att hantera login-begäran till appen via
 * Firebase authentiserings databasen. Klassen kontrollerar om det givna
 * Nickname:et har en email kopplat till sig.
 * @author Andreas Roghe, Sofia Ågren.
 * @version 2020-01-05
 */


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private List<MemberData> allMembers;
    private EditText nickName;
    private EditText password;
    private Button registerButton;
    private Button loginButton;
    private Activity activity;
    private DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        allMembers = new ArrayList<MemberData>();
        registerButton = (Button) findViewById(R.id.registerButton);
        loginButton = (Button) findViewById(R.id.loginButton);
        nickName = (EditText) findViewById(R.id.loginNickname);
        password = (EditText) findViewById(R.id.editTextTextPassword);
        activity = this;
        allMembers = fetchMembers();
    }

    @Override
    public void onStart() {
        super.onStart();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String providedNick = nickName.getText().toString().trim();
                String providedPass = password.getText().toString().trim();
                String email = null;

                for (MemberData member : allMembers) {
                    if (member.getNickName().equals(providedNick)) {
                        email = member.getEmail();
                    }
                }
                if (email == null) {
                    email = "empty";
                }

                if ((email == null || email.isEmpty()) || (providedPass == null || providedPass.isEmpty())) {
                    Toast.makeText(activity, "Missing information: Check nickname or password.", Toast.LENGTH_SHORT).show();
                } else {
                    signIn(email, providedPass);
                    clearFields();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterFragment registerFragment = new RegisterFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.contentFrame, registerFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack("register");
                ft.commit();
            }
        });
    }

    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
                            Toast.makeText(activity, "Authentication failed: Check nickname or password.",
                                    Toast.LENGTH_SHORT).show();
                            clearFields();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(this, HeadActivity.class);
            startActivity(intent);
        }
    }

    private List<MemberData> fetchMembers() {
        List<MemberData> members = new ArrayList<MemberData>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    child.getChildren().forEach(key -> {
                        String email = null;
                        if (key.getKey().equals("email")) {
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

    private void clearFields() {
        nickName.setText("");
        password.setText("");
    }
}