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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.attendance.Announcement.Announcement_Student_Activity;
import com.example.attendance.Announcement.Announcement_TA_Activity;
import com.example.attendance.Deadline.DeadlineStudentActivity;
import com.example.attendance.Deadline.DeadlineTAActivity;
import com.example.attendance.Forums.ForumsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.example.attendance.Absence.TAAbsenceTab;

public class TA_home extends AppCompatActivity implements AdapterView.OnItemSelectedListener, LocationListener {
    LocationManager lm;
    LocationListener locationListener;
    SessionManager sessionManager;
    EditText groups;
    Button recordAttendance;
    Spinner selectCourse;

    ArrayAdapter<String> adapter;
    ArrayList<String> givenCourses = new ArrayList<String>();
    ArrayList<String> existingGroups = new ArrayList<String>();

    boolean gotLocation = false;
    private static final int CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ta_home);

        sessionManager = new SessionManager(getApplicationContext());
        groups = findViewById(R.id.Groups);
        selectCourse = findViewById(R.id.Courses);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }

        //this api call gets all the courses the user is teaching as they are enrolled in them
        //just like students are
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
                        //we put what we get inside the array in order to use it to fill our spinner
                        givenCourses.add(courseId);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
            }

        });

        givenCourses.add("None");
        // use default spinner item to show options in spinner and fill it with givenCourses
        adapter = new ArrayAdapter<>(this, R.layout.course_spinner_item, givenCourses);
        selectCourse.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.course_dropdown_item);
        selectCourse.setPrompt("Courses");
        selectCourse.setOnItemSelectedListener(this);

        recordAttendance = findViewById(R.id.RecordAttendance);

        //Record attendance button------------------------------------------------------------------------------------------------------------------------
        recordAttendance.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                getCurrentLocation();
                String courseCode =selectCourse.getSelectedItem().toString();
                if(courseCode.equals("None")){
                    Toast.makeText(getApplicationContext(), "you chose None for courses please change your choice", Toast.LENGTH_SHORT).show();
                }
                else {
                    java.util.Date date = new java.util.Date();
                    String currDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
                    String group = groups.getText().toString();

                    //starting the attendance process
                    startAttendance(currDate, group, courseCode);
                }
    }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_deadlines:
                        startActivity(new Intent(TA_home.this, DeadlineTAActivity.class));
                        finish();
                        return true;

                    case R.id.action_announcements:
                        startActivity(new Intent(TA_home.this, Announcement_TA_Activity.class));
                        finish();
                        return true;

                    case R.id.action_forum:
                        startActivity(new Intent(TA_home.this, ForumsActivity.class));
                        finish();
                        return true;

                    case R.id.action_absence:
                        startActivity(new Intent(TA_home.this, TAAbsenceTab.class));
                        finish();
                        return true;
                }
                return false;
            }
        });
}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
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
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }
    //function that sets the location ( longitude and latitude)and then calls the APIs and stops the refresh after
    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        lm.removeUpdates(this);
        lm=null;
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

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    /**
     *
     * void startAttendance(String currDate,String groups,String courseCode)
     *
     * Summary of the Sort function:
     *
     *    this function starts the attendance process if the attendance for the groups provided
     *    hasn't already been recorded.
     *
     * Parameters   : currDate: the current date
     *                groups: the groups attendance is being recorded for
     *                courseCode: the course code of the course this attendance is being recorded for
     *
     * Return Value : Nothing -- Note: adds attendance records to the database.
     *
     * Description:
     *
     *    This function start the attendance recording process if the groups provided with the current date
     *    and the given course haven't had attendance recorded for them before in that course in that date
     *    already, if so, we show a toast with all the groups that already have associated attendance records
     *    for the course and date we gave, if that doesn't happen the attendance process starts and records
     *    are added to the database
     *
     */
    public void startAttendance(String currDate,String groups,String courseCode){
        UserAPI userAPI = APIClient.getClient().create(UserAPI.class);

        //first api call to get the groups that already have attendance
        Call<ArrayList<String>> call = userAPI.getExistingAttendanceGroups(currDate,groups,
                courseCode);

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if (response.code() != 200) {
                    Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                }
                else {

                    existingGroups = response.body();
                    //if there are no groups that already have attendance recorded for them in the course and
                    //date we provided
                    if (existingGroups.isEmpty()){

                        //start the attendance process and add the records to the database
                        Call<Void> call2 = userAPI.taStartAttendance(currDate, groups, courseCode, sessionManager.getId());
                        call2.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call2, Response<Void> response) {
                                if (response.code() != 200) {
                                    Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                                } else {
                                    //got to the next required activity and pass the needed values
                                    Intent ta_page_2 = new Intent(TA_home.this, TA_closes_attendance.class);
                                    ta_page_2.putExtra("currDate", currDate);
                                    ta_page_2.putExtra("courseCode", courseCode);
                                    ta_page_2.putExtra("group", groups);
                                    startActivity(ta_page_2);
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call2, Throwable t) {
                                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    //if there are groups that already have attendance recorded for them in the date and course
                    //we provided
                    else{
                        String groups ="";
                        for(int i=0; i<existingGroups.size(); i++){
                            groups += (existingGroups.get(i)+" ");
                        }
                        Toast.makeText(getApplicationContext(), "the following groups " + groups +
                                "already have attendance recorded for them today in this course", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                }
            });
    }

}