package com.example.attendance;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TA_home extends AppCompatActivity {
    TextView TA_name;
    TextView TA_id;
    SessionManager sessionManager;
    EditText groups;
    Button recordAttendance;
    Button selectCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_a_home);

        sessionManager = new SessionManager(getApplicationContext());

        TA_name = findViewById(R.id.TAname);
        TA_id = findViewById(R.id.TAid);
        recordAttendance = findViewById(R.id.RecordAttendance);

        TA_name.setText(sessionManager.getName());
        TA_id.setText(sessionManager.getId().toString());

        recordAttendance.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                UserAPI userAPI = APIClient.getClient().create(UserAPI.class);

                java.util.Date date=new java.util.Date();

                Call<String> call = userAPI.taStartAttendance(new SimpleDateFormat("dd-MM-yyyy").format(date),"CS_DS_1","CS467",sessionManager.getId());
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
}