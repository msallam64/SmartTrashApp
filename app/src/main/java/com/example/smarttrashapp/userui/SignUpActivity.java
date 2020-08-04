package com.example.smarttrashapp.userui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smarttrashapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private Button signUp;
    private EditText signUpUserName, signUpUserpassword, signUpUserphone;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        signUpUserName = findViewById(R.id.sign_up_usename);
        signUpUserpassword = findViewById(R.id.sign_up_userpassword);
        signUpUserphone = findViewById(R.id.sign_up_usenphone);
        signUp = findViewById(R.id.register_btn);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
                //Toast.makeText(SignUpActivity.this, "Ok", Toast.LENGTH_SHORT).show();
            }
        });
        progressDialog = new ProgressDialog(this);

    }

    private void CreateAccount() {
        String name = signUpUserName.getText().toString();
        String phone = signUpUserphone.getText().toString();
        String password = signUpUserpassword.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter Your Name ...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter Your Phone ...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Your Password ...", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.setTitle("Creating Account..");
            progressDialog.setMessage("Wait...........");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            ValidateAccount(name, phone, password);

        }


    }

    private void ValidateAccount(final String name, final String phone, final String password) {
        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((!dataSnapshot.child("Users").child(phone).exists())) {
                    HashMap<String, Object> usermap = new HashMap<>();
                    usermap.put("phone", phone);
                    usermap.put("name", name);
                    usermap.put("password", password);
                    Rootref.child("Users").child(phone).updateChildren(usermap)
                            .addOnCompleteListener( new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "Done !! ", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(SignUpActivity.this, "Network Error !! ", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                } else {
                    Toast.makeText(SignUpActivity.this, "Already This Account Exist  ", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}