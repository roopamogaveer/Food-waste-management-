package com.example.foodwm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    TextView email;
    Button resetPassword;
    TextView gotoLogin;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email=findViewById(R.id.email);
        resetPassword=findViewById(R.id.resetPasswordBtn);
        gotoLogin=findViewById(R.id.loginPage);
        mAuth=FirebaseAuth.getInstance();

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(ForgotPassword.this, Login.class);
                startActivity(login);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())
                {
                    email.setError("Please enter valid email");
                }
                else
                {
                    mAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent login = new Intent(ForgotPassword.this, Login.class);
                            startActivity(login);
                            Toast.makeText(getApplicationContext(), "Password reset mail sent, check SPAM folder",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}