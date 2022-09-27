package com.example.foodwm;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodwm.model.Donation;
import com.example.foodwm.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.internal.CollapsingTextHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

public class DonationRecycleAdopter extends RecyclerView.Adapter<DonationRecycleAdopter.ViewHolder>
{
    List<Donation> donationList;
    Context context;

    FirebaseAuth mAuth;
    DatabaseReference myUserRef,myDonationRef;
    String loggedInUser;

    public DonationRecycleAdopter(Context context, List<Donation> donationList)
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
    public DonationRecycleAdopter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.donation,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull DonationRecycleAdopter.ViewHolder holder, int position)
    {
        holder.address.setText(donationList.get(position).getAddress());
        holder.time.setText(donationList.get(position).getTime().split(" ")[1]);
        if(donationList.get(position).getStatus().equals("new"))
        {
            holder.callBtn.setVisibility(View.GONE);
            holder.finishBtn.setBackground(ContextCompat.getDrawable(context,R.drawable.btn_transparent));
            holder.finishBtn.setCompoundDrawables(null,null,null,null);
            holder.finishBtn.setText("Inprogress");
            holder.finishBtn.setEnabled(false);
            holder.finishBtn.setTextColor(ContextCompat.getColor(context,R.color.white));
        }
        else
        {
            myUserRef.child(donationList.get(position).getDistributerId()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    User user=dataSnapshot.getValue(User.class);
                    holder.name.setText(user.getUsername());
                }
            });
        }

        int i=position;
        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDonationRef.child(donationList.get(i).getId()).removeValue();
            }
        });

        holder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myUserRef.child(donationList.get(i).getDistributerId()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
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

    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }


    //    Holder
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView address,time,name;
        Button finishBtn,cancelBtn;
        ImageButton callBtn;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name= itemView.findViewById(R.id.name);
            address= itemView.findViewById(R.id.address);
            time=itemView.findViewById(R.id.time);
            finishBtn=itemView.findViewById(R.id.finish);
            cancelBtn=itemView.findViewById(R.id.cancel);
            callBtn=itemView.findViewById(R.id.call);
        }
    }
}
