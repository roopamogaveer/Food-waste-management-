package com.example.foodwm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import java.util.Locale;
import java.util.Objects;

public class DistributionSearchRecycleAdopter extends RecyclerView.Adapter<DistributionSearchRecycleAdopter.ViewHolder>
{
    List<Donation> donationList;
    Context context;


    FirebaseAuth mAuth;
    DatabaseReference myUserRef,myDonationRef;
    String loggedInUser;



    public DistributionSearchRecycleAdopter(Context context, List<Donation> donationList)
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
    public DistributionSearchRecycleAdopter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.available_distribution,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull DistributionSearchRecycleAdopter.ViewHolder holder, int position)
    {

        myUserRef.child(donationList.get(position).getDonarId()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                holder.name.setText(user.getUsername());
            }
        });

        holder.time.setText(donationList.get(position).getTime().split(" ")[1]);
        holder.food_items.setText(donationList.get(position).getFoodItems());
        holder.weight.setText(donationList.get(position).getWeight()+" KG");
        holder.vehicle.setText(donationList.get(position).getVehicle().toLowerCase());
        holder.mobile.setText(donationList.get(position).getAdditionalContact());

        if(donationList.get(position).getMap().equals(""))
        {
            holder.map.setVisibility(View.INVISIBLE);
        }

        if(donationList.get(position).getHuman())
        {
            holder.human.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_baseline_check_circle_15),null,null,null);
        }
        else
        {
            holder.human.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_baseline_cancel_15),null,null,null);
        }

        if(donationList.get(position).getAnimal())
        {
            holder.animal.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_baseline_check_circle_15),null,null,null);
        }
        else
        {
            holder.animal.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_baseline_cancel_15),null,null,null);
        }

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

        holder.callBtn.setOnClickListener(new View.OnClickListener() {
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

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Donation donation=donationList.get(i);
                donation.setDistributerId(loggedInUser);
                donation.setStatus("inprogress");
                myDonationRef.child(donationList.get(i).getId()).setValue(donation).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        replaceFragment(new HomeFragment());
                    }
                });
            }
        });
    }




    private void replaceFragment(Fragment fragment)
    {

        FragmentManager fragmentManager= ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }


    //    Holder
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,time,food_items,human,animal,weight,vehicle,mobile;
        Button acceptBtn,callBtn;
        ImageButton map;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            time=itemView.findViewById(R.id.time);
            map=itemView.findViewById(R.id.map);
            food_items=itemView.findViewById(R.id.food_items);
            human=itemView.findViewById(R.id.human);
            animal=itemView.findViewById(R.id.animal);
            weight=itemView.findViewById(R.id.weight);
            vehicle=itemView.findViewById(R.id.vehicle);
            mobile=itemView.findViewById(R.id.mobile);
            acceptBtn=itemView.findViewById(R.id.accept);
            callBtn=itemView.findViewById(R.id.call);
        }
    }
}
