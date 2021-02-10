package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        TA_name.setText(sessionManager.getName());
        TA_id.setText(sessionManager.getId().toString());
    }
}