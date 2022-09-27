package com.example.foodwm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodwm.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference myRef;

    EditText username, email, phone, password, confPassword;
    Button registerBtn;
    TextView alreadyReg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


         username=findViewById(R.id.username);
         email=findViewById(R.id.email);
         phone=findViewById(R.id.mobile);
         password=findViewById(R.id.password);
         confPassword=findViewById(R.id.confpassword);
         registerBtn=findViewById(R.id.registerBtn);
         alreadyReg=findViewById(R.id.alreadyReg);

        mAuth = FirebaseAuth.getInstance();
        myRef=FirebaseDatabase.getInstance().getReference("Users");


        // Initialize Firebase Auth
        alreadyReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(Register.this, Login.class);
                startActivity(login);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(username.getText()) || TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(phone.getText()) || TextUtils.isEmpty(password.getText()) || TextUtils.isEmpty(confPassword.getText()) )
                {
                    Toast.makeText(getApplicationContext(),"Please Fill all Fields",Toast.LENGTH_LONG).show();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())
                {
                    email.setError("Please enter valid email");
                }
                else if(!isValidPhone(phone.getText().toString()))
                {
                    phone.setError("Please enter valid Mobile number");
                }
                else if(password.getText().toString().length()<8)
                {
                    password.setError("Password length should be more than 8");
                }
                else if(!password.getText().toString().equals(confPassword.getText().toString()))
                {
                    confPassword.setError("Password doesn't match");
                }
                else
                {
                    register(email.getText().toString(), password.getText().toString(),phone.getText().toString(), username.getText().toString());
                }
            }
        });
    }

    private boolean isValidPhone(String phone)
    {
        return (!Pattern.matches("[a-zA-Z]+", phone)) && phone.length() == 10;
    }

    public void register(String email, String password, String phone, String username)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            User user=new User(username, email, phone);
                            myRef.child(currentUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Intent login = new Intent(Register.this, Login.class);
                                        startActivity(login);
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "User Registration failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Registration failed.",
                                    Toast.LENGTH_SHORT).show();
                            Intent register = new Intent(Register.this, Register.class);
                            startActivity(register);
                        }
                    }
                });
    }



    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent mainActivity = new Intent(Register.this, MainActivity.class);
            startActivity(mainActivity);
        }
    }
}