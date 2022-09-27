package com.example.foodwm.ui.Logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodwm.ForgotPassword;
import com.example.foodwm.Login;
import com.example.foodwm.R;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutFragment extends Fragment {

    View view;

    FirebaseAuth mAuth;

    public View onCreateView( LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {



        mAuth=FirebaseAuth.getInstance();
        mAuth.signOut();

        Intent login = new Intent(getContext(), Login.class);
        startActivity(login);

        view=inflater.inflate(R.layout.activity_login,container,false);
        return view;
    }


}