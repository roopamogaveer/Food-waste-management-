package com.example.foodwm.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodwm.Distribute;
import com.example.foodwm.EditProfile;
import com.example.foodwm.ForgotPassword;
import com.example.foodwm.Login;
import com.example.foodwm.R;
import com.example.foodwm.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {

    View view;
    TextView username, email, phone;
    Button editProfileBtn, resetPasswordBtn;

    FirebaseAuth mAuth;
    DatabaseReference myUserRef;
    User user;

    public View onCreateView( LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_profile,container,false);
        editProfileBtn=view.findViewById(R.id.editBtn);
        resetPasswordBtn=view.findViewById(R.id.resetPassword);
        username=view.findViewById(R.id.textView6);
        email=view.findViewById(R.id.textView4);
        phone=view.findViewById(R.id.textView5);

        mAuth=FirebaseAuth.getInstance();
        myUserRef = FirebaseDatabase.getInstance().getReference("Users");

        myUserRef.child(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                email.setText(user.getEmail());
                phone.setText(user.getPhone());

            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new EditProfile());
            }
        });

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPassword = new Intent(getContext(), ForgotPassword.class);
                startActivity(forgotPassword);
            }
        });

        return view;

    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment);
        fragmentTransaction.commit();
    }


}