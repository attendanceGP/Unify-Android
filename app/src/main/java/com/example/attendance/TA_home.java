package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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

public class TA_home extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView TA_name;
    TextView TA_id;
    SessionManager sessionManager;
    EditText groups;
    Button recordAttendance;
    Spinner selectCourse;
    ArrayAdapter<String> adapter;
    ArrayList<String> givenCourses = new ArrayList<String>();
    private SharedPreferences sharedPreferences;

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
                        String balf = response.body().get(i);
                        givenCourses.add(balf);
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
                UserAPI userAPI = APIClient.getClient().create(UserAPI.class);

                java.util.Date date=new java.util.Date();

                Call<String> call = userAPI.taStartAttendance(new SimpleDateFormat("dd-MM-yyyy").format(date),groups.getText().toString(),selectCourse.getSelectedItem().toString(),sessionManager.getId());
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.code() != 200) {
                            Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
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
}