package com.example.foodwm;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.foodwm.model.Donation;
import com.example.foodwm.model.User;
import com.example.foodwm.ui.home.HomeFragment;
import com.google.android.gms.common.util.NumberUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import android.util.Patterns;


public class Donate extends Fragment implements LocationListener {

    View view;
    CheckBox isHuman, isAnimal;
    EditText weight, vehicle, address, city, additionalContact, timeToPickup, footItems;
    Button donateBtn;
    String map = "";

    int day,month,year;
    int myHour,myMinute;
    String datetime="";

    FirebaseAuth mAuth;
    DatabaseReference myRef;

    LocationManager locationManager;

    String loggedInUid;

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

        view = inflater.inflate(R.layout.activity_donate, container, false);
        isHuman = view.findViewById(R.id.human);
        isAnimal = view.findViewById(R.id.animal);
        weight = view.findViewById(R.id.weight);
        vehicle = view.findViewById(R.id.vehicle);
        address = view.findViewById(R.id.address);
        city = view.findViewById(R.id.city);
        additionalContact = view.findViewById(R.id.contact);
        timeToPickup = view.findViewById(R.id.time);
        footItems = view.findViewById(R.id.food_items);
        donateBtn=view.findViewById(R.id.donateBtn);

        mAuth = FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference("Donations");

        getCurrentLocation();

        if(mAuth.getCurrentUser()!=null)
        {
            loggedInUid = mAuth.getUid();
        }


        timeToPickup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    Calendar calendar= Calendar.getInstance();
                    year=calendar.get(Calendar.YEAR);
                    month=calendar.get(Calendar.MONTH);
                    day=calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            datetime=year+"-";
                            if(month<9)
                            {
                                datetime+="0"+(month+1)+"-";
                            }
                            else
                            {
                                datetime+=(month+1)+"-";
                            }

                            if(day<10)
                            {
                                datetime+="0"+day+" ";
                            }
                            else
                            {
                                datetime+=day+" ";
                            }

                            timePicker();
                        }
                    }, year, month, day);

                    datePickerDialog.show();

                }

            }
        });


        donateBtn.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view)
            {
                if(TextUtils.isEmpty(weight.getText()) || TextUtils.isEmpty(vehicle.getText()) || TextUtils.isEmpty(address.getText())
                        || TextUtils.isEmpty(city.getText()) || TextUtils.isEmpty(additionalContact.getText()) || TextUtils.isEmpty(timeToPickup.getText()) || TextUtils.isEmpty(footItems.getText()))
                {
                    Toast.makeText(getContext(),"Please fill all details",Toast.LENGTH_LONG).show();
                }
                else if(!isNumber(weight.getText().toString()))
                {
                    weight.setError("Please fill numeric value");
                }
                else if(!isValidPhone(additionalContact.getText().toString()))
                {
                    additionalContact.setError("Please fill valid mobile number");
                }
                else if(!isHuman.isChecked() && !isAnimal.isChecked())
                {
                    Toast.makeText(getContext(),"Select for HUMAN or/and ANIMAL",Toast.LENGTH_LONG).show();
                }
                else if(address.getText().toString().equals("Loading...") || city.getText().toString().equals("Loading...") )
                {
                    address.setError("Location is loading...");
                }
                else
                {
                    confirmDonate();
                }

            }
        });
//        End of code
        return view;

    }

    private boolean isValidPhone(String phone)
    {
        return (!Pattern.matches("[a-zA-Z]+", phone)) && phone.length() == 10;
    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment);
        fragmentTransaction.commit();
    }

    private void timePicker()
    {
        Calendar calendar=Calendar.getInstance();
        myHour=calendar.get(Calendar.HOUR_OF_DAY);
        myMinute=calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog=new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                myHour=hour;
                myMinute=minute;
                if(myHour<10)
                {
                    datetime+="0"+myHour+":";
                }
                else
                {
                    datetime+=myHour+":";
                }

                if(myMinute<10)
                {
                    datetime+="0"+myMinute;
                }
                else
                {
                    datetime+=myMinute;
                }
                timeToPickup.setText(datetime);
            }
        },myHour,myMinute,false);
        timePickerDialog.show();
    }


    private boolean isNumber(String value)
    {
        if(value==null)
        {
            return false;
        }
        try
        {
            Double val=Double.parseDouble(value);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void confirmDonate()
    {
        try
        {
            DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime localDateTime=LocalDateTime.parse(datetime,formatter);
            String id=UUID.randomUUID().toString();
            Donation donation=new Donation(id,address.getText().toString(),loggedInUid,null,city.getText().toString(),vehicle.getText().toString(),additionalContact.getText().toString(),footItems.getText().toString(),"new",map,timeToPickup.getText().toString(),Double.parseDouble(weight.getText().toString()),isHuman.isChecked(),isAnimal.isChecked());
            myRef.child(id).setValue(donation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        replaceFragment(new HomeFragment());
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Unable to donate. Please try again", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        catch(Exception e)
        {
            timeToPickup.setError("Please select valid date and time");
        }
    }

    public void getCurrentLocation()
    {

        locationManager=(LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            address.setText("Loading...");
            city.setText("Loading...");
            Toast.makeText(getContext(), "Waiting for location...", Toast.LENGTH_LONG).show();
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

        if( address.getText().toString().equals("Loading...") || city.getText().toString().equals("Loading...") )
        {


            try {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addressList = null;
                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                map = addressList.get(0).getLatitude() + "," + addressList.get(0).getLongitude();
                String place=addressList.get(0).getAddressLine(0);
                address.setText(place.substring(place.indexOf(",")+1, 35));
                city.setText(addressList.get(0).getLocality());

            } catch (Exception e) {
                address.setText("");
                city.setText("");
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

        Toast.makeText(getContext(), "Loading... Please wait", Toast.LENGTH_LONG).show();
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
                address.setText("");
                city.setText("");
                dialogInterface.cancel();
            }
        });

        alertDialog.show();
    }




}