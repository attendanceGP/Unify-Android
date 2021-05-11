package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TA_home extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    SessionManager sessionManager;
    EditText groups;
    Button recordAttendance;
    Spinner selectCourse;
    ArrayAdapter<String> adapter;
    ArrayList<String> givenCourses = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ta_home);

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

        givenCourses.add("None");

        // use default spinner item to show options in spinner
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
                String courseCode =selectCourse.getSelectedItem().toString();
                if(courseCode.equals("None")){
                    Toast.makeText(getApplicationContext(), "you chose None for courses please change your choice", Toast.LENGTH_SHORT).show();
                }
                else {
                    UserAPI userAPI = APIClient.getClient().create(UserAPI.class);
                    java.util.Date date = new java.util.Date();
                    String currDate = new SimpleDateFormat("dd-MM-yyyy").format(date);
                    String group = groups.getText().toString();
                    Call<Void> call = userAPI.taStartAttendance(currDate, group, courseCode, sessionManager.getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.code() != 200) {
                                Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent ta_page_2 = new Intent(TA_home.this, TA_closes_attendance.class);
                                ta_page_2.putExtra("currDate", currDate);
                                ta_page_2.putExtra("courseCode", courseCode);
                                ta_page_2.putExtra("group", group);
                                startActivity(ta_page_2);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
    }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_deadlines:
                        if(sessionManager.getType().equals("student")){
                            startActivity(new Intent(TA_home.this, DeadlineStudentActivity.class));
                        }else{
                            startActivity(new Intent(TA_home.this, DeadlineTAActivity.class));
                        }
                        return true;

                    case R.id.action_announcements:
                        if(sessionManager.getType().equals("student")){
                            startActivity(new Intent(TA_home.this, Announcement_Student_Activity.class));
                        }else{
                            startActivity(new Intent(TA_home.this, Announcement_TA_Activity.class));
                        }
                        return true;

                    case R.id.action_forum:
                        return true;

                    case R.id.action_absence:
                        return true;
                }
                return false;
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
}