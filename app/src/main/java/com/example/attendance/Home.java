package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {
    Button button;

    TextView name;
    TextView level;
    TextView id;
    TextView gpa;

    TextView token;
    TextView type;
    SwipeRefreshLayout swipeRefreshLayout;
    String [] courses=null;
    Attendance attendance=null;
    boolean found;
    String course =null;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(getApplicationContext());
        name = findViewById(R.id.name);
        token = findViewById(R.id.token);
        type = findViewById(R.id.type);

/*
        name.setText("Name: "+sessionManager.getName());
        id.setText("ID: "+sessionManager.getId().toString());
        gpa.setText("GPA: "+sessionManager.getGPA().toString());
        level.setText("Level: "+sessionManager.getLevel().toString());
*/
        name.setText(sessionManager.getName());
        token.setText(sessionManager.getToken());
        type.setText(sessionManager.getType());

        swipeRefreshLayout =(SwipeRefreshLayout) findViewById(R.id.swipe);
        button = (Button)findViewById(R.id.attendButton);
        button.setVisibility(View.INVISIBLE);

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

                button.setVisibility(View.INVISIBLE);
                attendance=null;

                Date date = new Date();

                Call<Attendance>call2 = APIClient.getClient().create(UserAPI.class).checkAttendance(sessionManager.getId(),courses,new SimpleDateFormat("dd-MM-yyyy").format(date));
                call2.enqueue(new Callback<Attendance>() {
                    @Override
                    public void onResponse(Call<Attendance> call, Response<Attendance> response) {
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

                if (found){
                    button.setVisibility(View.VISIBLE);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
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
}