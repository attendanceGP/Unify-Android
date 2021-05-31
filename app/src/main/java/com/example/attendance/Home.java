package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.Absence.AbsenceTab;
import com.example.attendance.Absence.TAAbsenceTab;
import com.example.attendance.Announcement.Announcement_Student_Activity;
import com.example.attendance.Deadline.DeadlineStudentActivity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity implements LocationListener {
    LocationManager lm;
    Button button;
    double longitude, latitude, geoDistance;
    private static final int CODE = 1;
    SwipeRefreshLayout swipeRefreshLayout;
    String[] courses = null;
    Attendance attendance = null;
    boolean found;
    String course = null;
    SessionManager sessionManager;
    boolean gotLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_student);
        gotLocation = false;
        sessionManager = new SessionManager(getApplicationContext());
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        button = (Button) findViewById(R.id.attend_butt);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
        // call the get student courses API and store them in courses variable
        Call<String[]> call = APIClient.getClient().create(UserAPI.class).getStudentCourses(sessionManager.getId());
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                courses = response.body();
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                Toast.makeText(Home.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*if(ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE);
                    ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                    swipeRefreshLayout.setRefreshing(false);

                }*/
            if (!gotLocation){
                getCurrentLocation();
                gotLocation=true;
            }
            }
        });
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_absence:
                        startActivity(new Intent(Home.this, AbsenceTab.class));
                        return true;

                    case R.id.action_announcements:
                        startActivity(new Intent(Home.this, Announcement_Student_Activity.class));
                        return true;

                    case R.id.action_forum:
                        return true;

                    case R.id.action_deadlines:
                        startActivity(new Intent(Home.this, DeadlineStudentActivity.class));
                        return true;
                }
                return false;
            }
        });
    }


    public void callAPIs() {
        button.setBackground(getResources().getDrawable(R.drawable.attend_button));
        button.setEnabled(false);
        //      button.setVisibility(View.INVISIBLE);
        attendance = null;

        Date date = new Date();
        //call the check attendance API to check if there is any attendance available at the
        //time the student refreshes the page
        Call<Attendance> call2 = APIClient.getClient().create(UserAPI.class).checkAttendance(sessionManager.getId(), courses, new SimpleDateFormat("dd-MM-yyyy").format(date));
        call2.enqueue(new Callback<Attendance>() {
            @Override
            public void onResponse(Call<Attendance> call, Response<Attendance> response) {
                //if there is an attendance available it gets the course name and save the
                //store the row of the TA from the DB in the variable attendance

                if (response.body() != null) {
                    course = response.body().getCourseName();
                    //for the TA location
                    int taID = response.body().getUserId();
                    Call<TeachingAssistant>taData = APIClient.getClient().create(UserAPI.class).getTA(taID);
                    taData.enqueue(new Callback<TeachingAssistant>() {
                        @Override
                        public void onResponse(Call<TeachingAssistant> call, Response<TeachingAssistant> taResponse) {
                            // for the geolocation
                            geoDistance = distance(latitude,taResponse.body().getLatitude(),longitude,taResponse.body().getLongitude());
                            if (geoDistance < 50) {
                                button.setBackground(getResources().getDrawable(R.drawable.attend_button_on));
                                button.setEnabled(true);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // on clicking the button it calls the attend API which checks again if the attendance is available
                                        // and if so it records the student attendance
                                        Call<Void> call3 = APIClient.getClient().create(UserAPI.class).attend(sessionManager.getId(), course, new SimpleDateFormat("dd-MM-yyyy").format(date), response.body().getId());
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

                        }

                        @Override
                        public void onFailure(Call<TeachingAssistant> call, Throwable t) {

                        }
                    });


                } else {
                    found = false;
                }
            }

            @Override
            public void onFailure(Call<Attendance> call, Throwable t) {
                Toast.makeText(Home.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //function that gets the current location
    private void getCurrentLocation() {
         lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},CODE);
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
            return;
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }
    //function that sets the location ( longitude and latitude)and then calls the APIs and stops the refresh after
    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lm.removeUpdates(this);
        lm = null;
        if (swipeRefreshLayout.isRefreshing()) {
            callAPIs();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public static double distance(double lat1,
                                  double lat2, double lon1,
                                  double lon2) {

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
                * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return (c * r * 1000);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

}
