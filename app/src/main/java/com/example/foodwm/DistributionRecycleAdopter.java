package com.example.foodwm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodwm.model.Donation;
import com.example.foodwm.model.User;
import com.example.foodwm.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Objects;

public class DistributionRecycleAdopter extends RecyclerView.Adapter< DistributionRecycleAdopter.ViewHolder>
{
    List<Donation> donationList;
    Context context;

    FirebaseAuth mAuth;
    DatabaseReference myUserRef,myDonationRef;
    String loggedInUser;

    public DistributionRecycleAdopter(Context context, List<Donation> donationList)
    {
        this.context=context;
        this.donationList=donationList;

        mAuth = FirebaseAuth.getInstance();
        loggedInUser=mAuth.getUid();
        myUserRef = FirebaseDatabase.getInstance().getReference("Users");
        myDonationRef = FirebaseDatabase.getInstance().getReference("Donations");

    }

    @NonNull
    @Override
    public DistributionRecycleAdopter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.distribution,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull DistributionRecycleAdopter.ViewHolder holder, int position)
    {
        myUserRef.child(donationList.get(position).getDistributerId()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                holder.name.setText(user.getUsername());
            }
        });

        if(donationList.get(position).getMap().equals(""))
        {
            holder.map.setVisibility(View.INVISIBLE);
        }

        holder.address.setText(donationList.get(position).getAddress());
        holder.time.setText(donationList.get(position).getTime().split(" ")[1]);

        int i=position;

        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location=donationList.get(i).getMap();
                String mapURL="http://maps.google.com/maps?q=loc:"+location+" ("+donationList.get(i).getAddress()+")";

                Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(mapURL));
                intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                context.startActivity(intent);
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myUserRef.child(donationList.get(i).getDonarId()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String mobile=user.getPhone();
                        String url="tel:"+mobile;
                        Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        context.startActivity(intent);
                    }
                });

            }
        });

        holder.finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Donation donation=donationList.get(i);
                donation.setStatus("completed");
                myDonationRef.child(donationList.get(i).getId()).setValue(donation).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Thank you for your Contribution",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDonationRef.child(donationList.get(i).getId()).removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }


    //    Holder
    public class ViewHolder extends RecyclerView.ViewHolder
    {
       TextView name,address,time;
       Button finishBtn,rejectBtn;
       ImageButton map, call;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            address=itemView.findViewById(R.id.address);
            time=itemView.findViewById(R.id.time);
            map=itemView.findViewById(R.id.map);
            call=itemView.findViewById(R.id.call);
            finishBtn=itemView.findViewById(R.id.finish);
            rejectBtn=itemView.findViewById(R.id.reject);
        }
    }
}
