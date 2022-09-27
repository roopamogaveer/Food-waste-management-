package com.example.foodwm;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodwm.model.Donation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Objects;

public class ContributionRecycleAdopter extends RecyclerView.Adapter< ContributionRecycleAdopter.ViewHolder>
{
    List<Donation> donationList;
    Context context;

    FirebaseAuth mAuth;
    DatabaseReference myUserRef,myDonationRef;
    String loggedInUser;

    public ContributionRecycleAdopter(Context context, List<Donation> donationList)
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
    public ContributionRecycleAdopter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.contribution,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ContributionRecycleAdopter.ViewHolder holder, int position)
    {
        holder.contributionDate.setText(LocalDateTime.parse(donationList.get(position).getTime(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).format(DateTimeFormatter.ofPattern("dd MMM, yyyy")));
        holder.contributionType.setText(donationList.get(position).getDonarId().equals(loggedInUser) ?"Donation":"Distribution");
        holder.contributionType.setTextColor(donationList.get(position).getDonarId().equals(loggedInUser) ? ContextCompat.getColor(context,R.color.green):ContextCompat.getColor(context,R.color.purple_500));
        holder.contributionFrom.setText(donationList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }


//    Holder
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView contributionDate, contributionFrom, contributionType;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contributionDate= itemView.findViewById(R.id.contributionDate);
            contributionFrom= itemView.findViewById(R.id.contributionFrom);
            contributionType= itemView.findViewById(R.id.contributionType);
        }
    }
}
