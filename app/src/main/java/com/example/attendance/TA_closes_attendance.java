package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TA_closes_attendance extends AppCompatActivity {

    Button showAttendeesList;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_a_closes_attendance);

        sessionManager = new SessionManager(getApplicationContext());

        showAttendeesList = findViewById(R.id.showAttendeesList);
        //show attendees list button------------------------------------------------------------------------------------------------------------------------
        showAttendeesList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                UserAPI userAPI = APIClient.getClient().create(UserAPI.class);
                Intent intent = getIntent();
                String currDate = intent.getStringExtra("currDate");
                String group = intent.getStringExtra("group");
                String courseCode = intent.getStringExtra("courseCode");

                Call<Void> call = userAPI.closeTAattendance(currDate,group,courseCode,sessionManager.getId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() != 200) {
                            Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            /// todo put in the appropriate activity
                            Intent dataIntent = getIntent();
                            Intent intent = new Intent(TA_closes_attendance.this, ta_attendanceConfirmationList.class);
                            intent.putExtra("CoureID_Key", dataIntent.getStringExtra("courseCode"));
                            intent.putExtra("Group_Key", dataIntent.getStringExtra("group"));
                            intent.putExtra("Date_Key", dataIntent.getStringExtra("currDate"));
                            startActivity(intent);
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
}