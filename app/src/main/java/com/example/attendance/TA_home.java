package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

public class TA_home extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LocationListener {
    TextView TA_name;
    TextView TA_id;
    SessionManager sessionManager;
    EditText groups;
    Button recordAttendance;
    Spinner selectCourse;
    ArrayAdapter<String> adapter;
    ArrayList<String> givenCourses = new ArrayList<String>();
    boolean gotLocation = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_a_home);

        sessionManager = new SessionManager(getApplicationContext());
        groups = findViewById(R.id.Groups);
        selectCourse = findViewById(R.id.Courses);


        UserAPI userAPI = APIClient.getClient().create(UserAPI.class);
        Call<ArrayList<String>> call = userAPI.getTaughtCourses(sessionManager.getId());
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if (response.code() != 200) {
                    Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                }
                else{
                    for(int i=0; i<response.body().size(); i++){
                        String courseId = response.body().get(i);
                        givenCourses.add(courseId);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {

            }

        });

        givenCourses.add("");

        // use default spinner item to show options in spinner
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, givenCourses);
        selectCourse.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCourse.setOnItemSelectedListener(this);




        //TA name and id shown-------------------------------------------------------------------------------------------------------------------------
        TA_name = findViewById(R.id.TAname);
        TA_id = findViewById(R.id.TAid);
        recordAttendance = findViewById(R.id.RecordAttendance);

        TA_name.setText(sessionManager.getName());
        TA_id.setText(sessionManager.getId().toString());

        //Record attendance button------------------------------------------------------------------------------------------------------------------------
        recordAttendance.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getCurrentLocation();
                UserAPI userAPI = APIClient.getClient().create(UserAPI.class);

                java.util.Date date=new java.util.Date();
                String currDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
                String courseCode =selectCourse.getSelectedItem().toString();
                String group = groups.getText().toString();
                Call<Void> call = userAPI.taStartAttendance(currDate,group,courseCode,sessionManager.getId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() != 200) {
                            Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent ta_page_2 = new Intent(TA_home.this, TA_closes_attendance.class);
                            ta_page_2.putExtra("currDate",currDate);
                            ta_page_2.putExtra("courseCode",courseCode);
                            ta_page_2.putExtra("group",group);
                            startActivity(ta_page_2);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }
        });
}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "selected: " + text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
    private void getCurrentLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }
    //function that sets the location ( longitude and latitude)and then calls the APIs and stops the refresh after
    @Override
    public void onLocationChanged(@NonNull Location location) {
    if (!gotLocation){
        gotLocation = true;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Call<Void>TALocation = APIClient.getClient().create(UserAPI.class).updateTaLocation(sessionManager.getId(),longitude,latitude);
        TALocation.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(TA_home.this, "location updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TA_home.this, "error in updating location", Toast.LENGTH_SHORT).show();
            }
        });
    }
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