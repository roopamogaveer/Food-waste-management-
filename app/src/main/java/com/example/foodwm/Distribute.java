package com.example.foodwm;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodwm.model.Donation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Distribute extends Fragment implements LocationListener
{

    View view;
    Spinner citySpinner;
    Button searchBtn;
    TextView noContribution, loadingDonation;

    FirebaseAuth mAuth;
    DatabaseReference myUserRef,myDonationRef;

    LocationManager locationManager;

    String loggedInUid;
    String currentCity=null;

    RecyclerView distributionSearchRecyclerView;
    DistributionSearchRecycleAdopter distributionSearchRecycleAdopter;

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {

                @Override
                public void onActivityResult(Boolean result) {
                    if (result)
                    {
                        getCurrentLocation();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Please provide required Permission", Toast.LENGTH_LONG).show();
                    }

                }
            }
    );


    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.activity_distribute,container,false);
        citySpinner=view.findViewById(R.id.city);
        searchBtn=view.findViewById(R.id.search);
        noContribution=view.findViewById(R.id.noContribution);
        loadingDonation=view.findViewById(R.id.loadingDonation);


        mAuth = FirebaseAuth.getInstance();
        myUserRef = FirebaseDatabase.getInstance().getReference("Users");
        myDonationRef = FirebaseDatabase.getInstance().getReference("Donations");

        getCurrentLocation();

        if(mAuth.getCurrentUser()!=null)
        {
            loggedInUid = mAuth.getUid();
        }

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentCity=citySpinner.getSelectedItem().toString();
                loadingDonation.setVisibility(View.VISIBLE);
                onSearch(currentCity);
            }
        });

        myDonationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                List<String> citylist=new ArrayList<>();

                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Donation donation= dataSnapshot.getValue(Donation.class);
                    if(donation!=null && !citylist.contains(donation.getCity().toUpperCase()))
                    {
                        citylist.add(donation.getCity().toUpperCase());
                    }
                }
                try
                {
                    ArrayAdapter arrayAdapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,citylist);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(arrayAdapter);
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });


        return view;

    }


    public void onSearch(String city)
    {

        myDonationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                List<Donation> donationList=new ArrayList<>();
                loadingDonation.setVisibility(View.INVISIBLE);
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Donation donation= dataSnapshot.getValue(Donation.class);
                    if(donation!=null && !donation.getDonarId().equals(loggedInUid) && donation.getCity().toLowerCase().equals(city.toLowerCase()) && donation.getStatus().equals("new"))
                    {
                        donationList.add(donation);
                    }
                }

                if(donationList.size()==0)
                {
                    noContribution.setVisibility(View.VISIBLE);
                }
                else
                {
                    noContribution.setVisibility(View.INVISIBLE);
                }

                distributionSearchRecyclerView=view.findViewById(R.id.linearLayout4);
                distributionSearchRecyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
                distributionSearchRecycleAdopter=new DistributionSearchRecycleAdopter(getContext(),donationList);
                distributionSearchRecyclerView.setAdapter(distributionSearchRecycleAdopter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }



    public void getCurrentLocation()
    {
        locationManager=(LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Toast.makeText(getContext(), "Waiting for location...", Toast.LENGTH_LONG).show();

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            loadingDonation.setVisibility(View.VISIBLE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,10,this);
        }
        else
        {
            getPermission();
        }
    }


    public void getPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

    }

    @Override
    public void onLocationChanged(@NonNull Location location)
    {

        if(currentCity==null)
        {


            try
            {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addressList = null;
                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                currentCity=addressList.get(0).getLocality();
                onSearch(currentCity);
            }
            catch (Exception e)
            {
                Toast.makeText(getContext(),"Unable fetch location", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        getCurrentLocation();
    }

    @Override
    public void onFlushComplete(int requestCode) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        showSettingAlert();
    }


    public void showSettingAlert()
    {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Turn on GPS");
        alertDialog.setMessage("Goto settings Please turn on GPS to autofill");

        alertDialog.setPositiveButton("Turn on", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getContext().startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alertDialog.show();
    }

}