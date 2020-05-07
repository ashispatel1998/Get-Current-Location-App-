package com.example.getcurrentlocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    TextView txt_lat, txt_long,address;
    Button btn_share,btn_store,btn_show;
    DatabaseHelper mydb;
    protected LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb=new DatabaseHelper(this);
        address=findViewById(R.id.address);
        txt_lat = findViewById(R.id.txt_lat);
        txt_long = findViewById(R.id.txt_long);
        btn_share = findViewById(R.id.btn_share);
        btn_store=findViewById(R.id.btn_store);
        btn_show=findViewById(R.id.btn_show);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionListener permissionListener=new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    Toast.makeText(getApplicationContext(),"Permission granted",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                    Toast.makeText(getApplicationContext(),"Permission not given",Toast.LENGTH_SHORT).show();
                }
            };

            TedPermission.with(MainActivity.this).setPermissionListener(permissionListener)
                    .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET).check();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Toast.makeText(getApplicationContext(),"It may take some Time,please wait!",Toast.LENGTH_LONG).show();

        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Button is clicked",Toast.LENGTH_SHORT).show();
                Cursor res= mydb.getallData();
                if(res.getCount()==0){
                    showMessage("Error","No data is avaliable");
                }
                StringBuffer stringBuffer=new StringBuffer();

                while (res.moveToNext()) {
                    stringBuffer.append("No :"+res.getString(0)+"\n");
                    stringBuffer.append("Latitude :"+res.getString(1)+"\n");
                    stringBuffer.append("Longitude :"+res.getString(2)+"\n\n");
                }
                showMessage("Location Data",stringBuffer.toString());
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        txt_lat = findViewById(R.id.txt_lat);
        final String lat=location.getLatitude()+"";
        txt_long = findViewById(R.id.txt_long);
        final String longi=location.getLongitude()+"";
        txt_lat.setText(lat);
        txt_long.setText(longi);

        double longitude=location.getLongitude();
        double latitude=location.getLatitude();
        try {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
            address.setText(addressList.get(0).getAddressLine(0));

            } catch (IOException e) {
                e.printStackTrace();
            }



        btn_share.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent shareintent=new Intent();
                 shareintent.setAction(Intent.ACTION_SEND);
                 shareintent.putExtra(Intent.EXTRA_TEXT,"https://maps.google.com/?q="+lat+","+longi+"\nAnd this is my location\n"+address.getText().toString());
                 shareintent.setType("text/plain");
                 startActivity(Intent.createChooser(shareintent,"Share via"));
             }
         });

         btn_store.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
              AddData(lat,longi);
             }
         });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    //ADD DATA TO DATABASE
    public void AddData(String lat,String longi){
        boolean isInsurted=mydb.insertData(lat,longi);
        if(isInsurted=true){
            Toast.makeText(getApplicationContext(),"Data inserted!",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Data not inserted!",Toast.LENGTH_SHORT).show();
        }
    }
    //SHOW DATA
    public void showMessage(String title,String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
    }
}