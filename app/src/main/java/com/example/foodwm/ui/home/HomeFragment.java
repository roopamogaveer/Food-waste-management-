package com.example.foodwm.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodwm.ContributionRecycleAdopter;
import com.example.foodwm.Distribute;
import com.example.foodwm.DistributionRecycleAdopter;
import com.example.foodwm.Donate;
import com.example.foodwm.DonationRecycleAdopter;
import com.example.foodwm.ForgotPassword;
import com.example.foodwm.Login;
import com.example.foodwm.R;
import com.example.foodwm.model.Donation;
import com.example.foodwm.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeFragment extends Fragment {

      View view;
      FirebaseAuth mAuth;
      DatabaseReference myUserRef,myDonationRef;
      String loggedInUser;

      TextView welcome,noContribution;

      RecyclerView contributionRecyclerView, donationRecyclerView,distributionRecyclerView;

      ContributionRecycleAdopter contributionRecycleAdopter;
      DonationRecycleAdopter donationRecycleAdopter;
      DistributionRecycleAdopter distributionRecycleAdopter;


    public View onCreateView( LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        this.view=inflater.inflate(R.layout.fragment_home,container,false);
        LinearLayout donate=view.findViewById(R.id.donate);
        LinearLayout distribute=view.findViewById(R.id.distribute);
        noContribution=view.findViewById(R.id.noContribution);

        welcome = view.findViewById(R.id.welcome_user);
        mAuth = FirebaseAuth.getInstance();
        loggedInUser=mAuth.getUid();
        myUserRef = FirebaseDatabase.getInstance().getReference("Users");
        myDonationRef = FirebaseDatabase.getInstance().getReference("Donations");



        if(mAuth.getCurrentUser()!=null)
        {
            String loggedUserId=mAuth.getUid();
            myUserRef.child(loggedUserId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        User  user=task.getResult().getValue(User.class);
                        String username=user!=null?user.getUsername() : " ";
                        String message="Welcome "+username;
                        welcome.setText(message);
                    }

                }
            });

        }




        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Donate());
            }
        });

        distribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new Distribute());
            }
        });




        myDonationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                List<Donation> donationInProgress=new ArrayList<>();
                List<Donation> distributionInProgress=new ArrayList<>();
                List<Donation> contributionHistory=new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {

                    Donation donation= dataSnapshot.getValue(Donation.class);

                    if(mAuth.getUid()==null)
                    {
                        Intent login = new Intent(getContext(), Login.class);
                        startActivity(login);
                    }
                    else if(donation.getDonarId().equals(loggedInUser) && (donation.getStatus().equals("new") || donation.getStatus().equals("inprogress")))
                    {
                        donationInProgress.add(donation);
                    }
                    else if(donation.getDistributerId()!=null && donation.getDistributerId().equals(loggedInUser) && donation.getStatus().equals("inprogress"))
                    {
                        distributionInProgress.add(donation);
                    }
                    else if(donation.getDistributerId()!=null && (donation.getDistributerId().equals(loggedInUser) || donation.getDonarId().equals(loggedInUser)) && donation.getStatus().equals("completed"))
                    {
                        contributionHistory.add(donation);
                    }


                }

                if(contributionHistory.size()==0)
                {
                    noContribution.setVisibility(View.VISIBLE);
                }
                else
                {
                    noContribution.setVisibility(View.INVISIBLE);
                }
                contributionRecyclerView=view.findViewById(R.id.linearLayout3);
                contributionRecyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
                contributionRecycleAdopter=new ContributionRecycleAdopter(getContext(),contributionHistory);
                contributionRecyclerView.setAdapter(contributionRecycleAdopter);

                donationRecyclerView=view.findViewById(R.id.linearLayout2);
                donationRecyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
                donationRecycleAdopter=new DonationRecycleAdopter(getContext(),donationInProgress);
                donationRecyclerView.setAdapter(donationRecycleAdopter);

                distributionRecyclerView=view.findViewById(R.id.linearLayout4);
                distributionRecyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
                distributionRecycleAdopter=new DistributionRecycleAdopter(getContext(),distributionInProgress);
                distributionRecyclerView.setAdapter(distributionRecycleAdopter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });


//        Contribution Recycle code



//        End of all Code
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