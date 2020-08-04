package com.example.smarttrashapp.userui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarttrashapp.Model.Users;
import com.example.smarttrashapp.Prevalent.Prevalent;
import com.example.smarttrashapp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private LoginButton faceBookLoginBtn;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private EditText EtPhone, EtPassword;
    private Button loginBtn;
    private ProgressDialog progressDialog;
    private String parentDBName = "Users";
    private TextView passworForget;
    private CheckBox chboxremmberme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        faceBookLoginBtn = findViewById(R.id.facebook_login_button);
        EtPassword = findViewById(R.id.login_password_Et);
        EtPhone = findViewById(R.id.login_phone_Et);
        loginBtn = findViewById(R.id.login_btn);
        passworForget = findViewById(R.id.forrgetpass);
        progressDialog = new ProgressDialog(this);
        faceBookLoginBtn.setPermissions(Arrays.asList("email", "public_profile"));
        faceBookLoginBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginuser();
            }
        });
        passworForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PasswordResetActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);
            }
        });
        chboxremmberme = findViewById(R.id.checkBox);
        Paper.init(this);
    }

    private void loginuser() {
        String phone = EtPhone.getText().toString();
        String password = EtPassword.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter Your Phone ...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Your Password ...", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.setTitle("Login Account..");
            progressDialog.setMessage("Wait...........");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            allowaccessaccount(phone, password);
        }
    }

    private void allowaccessaccount(final String phone, final String password) {
        if (chboxremmberme.isChecked()) {
            Paper.book().write(Prevalent.userphonekey, phone);
            Paper.book().write(Prevalent.userpasswordkey, password);
        }
        final DatabaseReference Rootref;
        Rootref = FirebaseDatabase.getInstance().getReference();
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDBName).child(phone).exists()) {
                    Users userdata = dataSnapshot.child(parentDBName).child(phone).getValue(Users.class);
                    if (userdata.getPhone().equals(phone)) {
                        if (userdata.getPassword().equals(password)) {
                            if (parentDBName.equals("Users")) {
                                Toast.makeText(LoginActivity.this, "Done Logged In..!", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentonlineusers = userdata;
                                startActivity(intent);
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Password Incorrect..!", Toast.LENGTH_LONG).show();
                        }

                    }


                } else {
                    Toast.makeText(LoginActivity.this, "Account with " + phone + "Not Exist", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // The activity result pass back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, UI will update with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(LoginActivity.this, "Authentication Succeeded.", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign-in fails, a message will display to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Done !! ", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Network Error !! ", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                } else {
                    Toast.makeText(LoginActivity.this, "Already This Account Exist  ", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
