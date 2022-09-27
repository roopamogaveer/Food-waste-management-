package com.example.foodwm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;

    EditText email, password;
    Button loginBtn;
    TextView newUser,forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        loginBtn=findViewById(R.id.loginBtn);
        newUser=findViewById(R.id.newUser);
        forgotPassword=findViewById(R.id.forgotPassword);

        mAuth=FirebaseAuth.getInstance();

        System.out.println("UUID : "+mAuth.getUid());

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(password.getText()))
                {
                    Toast.makeText(getApplicationContext(), "Please fill both email and password",Toast.LENGTH_LONG).show();
                }
                else
                {
                    login(email.getText().toString(),password.getText().toString());
                }
            }
        });

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent register = new Intent(Login.this, Register.class);
                startActivity(register);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent forgotPassword = new Intent(Login.this, ForgotPassword.class);
                startActivity(forgotPassword);
            }
        });


    }

    public void login(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Intent mainActivity = new Intent(Login.this, MainActivity.class);
                    startActivity(mainActivity);
                }
                else
                {
                    Intent login = new Intent(Login.this, Login.class);
                    startActivity(login);
                    Toast.makeText(getApplicationContext(), "Invalid email or password",Toast.LENGTH_LONG).show();

                }

            }
        });

    }
}