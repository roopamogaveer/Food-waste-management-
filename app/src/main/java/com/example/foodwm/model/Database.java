package com.example.foodwm.model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Database
{
    FirebaseAuth mAuth;
    DatabaseReference myRef;
    public Task<DataSnapshot> userResult;

    public Database()
    {
        mAuth = FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference("Users");
    }

    public Task<DataSnapshot> getUsername(String uid)
    {

        myRef.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    userResult=task;

                }
            }
        });

        return userResult;
    }


}
