package com.example.foodwm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodwm.model.User;
import com.example.foodwm.ui.profile.ProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfile extends Fragment {

    View view;

    FirebaseAuth mAuth;
    DatabaseReference myUserRef;

    TextView username,phone;
    Button editprofileSubmitBtn;

    User user;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.activity_edit_profile,container,false);
        username=view.findViewById(R.id.username);
        phone=view.findViewById(R.id.mobile);
        editprofileSubmitBtn=view.findViewById(R.id.editprofileSubmitBtn);

        mAuth=FirebaseAuth.getInstance();
        myUserRef = FirebaseDatabase.getInstance().getReference("Users");

        myUserRef.child(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                phone.setText(user.getPhone());
            }
        });

        editprofileSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(username.getText()) || TextUtils.isEmpty(phone.getText()) )
                {
                    Toast.makeText(getContext(),"Please Fill all Fields",Toast.LENGTH_LONG).show();
                }
                else if(!Patterns.PHONE.matcher(phone.getText()).matches())
                {
                    phone.setError("Please enter valid Mobile number");
                }
                else
                {
                    user.setUsername(username.getText().toString());
                    user.setPhone(phone.getText().toString());
                    myUserRef.child(mAuth.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            replaceFragment(new ProfileFragment());
                            Toast.makeText(getContext(),"Profile edited",Toast.LENGTH_LONG).show();
                        }
                    });
                }
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