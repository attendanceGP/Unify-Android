package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {
    Button button;
    double longitude,latitude,geoDistance;
    private static final int CODE = 1;
    /*TextView name;
    TextView level;
    TextView id;
    TextView gpa;

    TextView token;
    TextView type;*/
    SwipeRefreshLayout swipeRefreshLayout;
    String [] courses=null;
    Attendance attendance=null;
    boolean found;
    String course =null;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_student);

        sessionManager = new SessionManager(getApplicationContext());
        /*name = findViewById(R.id.name);
        token = findViewById(R.id.token);
        type = findViewById(R.id.type);
*/
/*
        name.setText("Name: "+sessionManager.getName());
        id.setText("ID: "+sessionManager.getId().toString());
        gpa.setText("GPA: "+sessionManager.getGPA().toString());
        level.setText("Level: "+sessionManager.getLevel().toString());
*/
  /*      name.setText(sessionManager.getName());
        token.setText(sessionManager.getToken());
        type.setText(sessionManager.getType());
*/
        swipeRefreshLayout =(SwipeRefreshLayout) findViewById(R.id.swipe);
        button = (Button)findViewById(R.id.attend_butt);
        //button.setVisibility(View.INVISIBLE);
        // call the get student courses API and store them in courses variable
        Call<String[]> call = APIClient.getClient().create(UserAPI.class).getStudentCourses(sessionManager.getId());
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                courses=response.body();
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                Toast.makeText(Home.this, "error", Toast.LENGTH_SHORT).show();

            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                button.setBackground(getResources().getDrawable(R.drawable.attend_button));
                button.setEnabled(false);
          //      button.setVisibility(View.INVISIBLE);
                attendance=null;

                Date date = new Date();
                //call the check attendance API to check if there is any attendance available at the
                //time the student refreshes the page
                Call<Attendance>call2 = APIClient.getClient().create(UserAPI.class).checkAttendance(sessionManager.getId(),courses,new SimpleDateFormat("dd-MM-yyyy").format(date));
                call2.enqueue(new Callback<Attendance>() {
                    @Override
                    public void onResponse(Call<Attendance> call, Response<Attendance> response) {
                        //if there is an attendance available it gets the course name and save the
                        //store the row of the TA from the DB in the variable attendance
                        attendance = response.body();
                        if(attendance != null){
                            found = true;
                            course = attendance.getCourseName();
                        }
                        else{
                            found=false;
                        }
                    }

                    @Override
                    public void onFailure(Call<Attendance> call, Throwable t) {
                        Toast.makeText(Home.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                    // for the geolocation
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE);
                } else {
                    getCurrentLocation(30,31);
                }

                // if there is an attendance available make the attend button appear
                ///todo change the geodistance 
                if (found && geoDistance < 100000){
                    //button.setVisibility(View.VISIBLE);
                    Toast.makeText(Home.this, Double.toString(geoDistance), Toast.LENGTH_SHORT).show();
                    button.setBackground(getResources().getDrawable(R.drawable.attend_button_on));
                    button.setEnabled(true);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // on clicking the button it calls the attend API which checks again if the attendance is available
                            // and if so it records the student attendance
                            Call<Void>call3 = APIClient.getClient().create(UserAPI.class).attend(sessionManager.getId(),course,new SimpleDateFormat("dd-MM-yyyy").format(date),attendance.getId());
                            call3.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    Toast.makeText(Home.this, "Done", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(Home.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    //functions for the geolocation
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation(30,31);
            } else {
                Toast.makeText(this, "leeeh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation(final double TALat, final double TALong) {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(Home.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(Home.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            latitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            //longitudeText.setText("longitude =" + Double.toString(longitude)+" , latitude =" + Double.toString(latitude));
                            geoDistance =distance(latitude,TALat,longitude,TALong);
                       //     Toast.makeText(Home.this, Double.toString(geoDistance), Toast.LENGTH_SHORT).show();

                        }

                    }
                }, Looper.getMainLooper());

    }
    public static double distance(double lat1,
                                  double lat2, double lon1,
                                  double lon2)
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r*1000);
    }

}