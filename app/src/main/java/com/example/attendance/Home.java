package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Home extends AppCompatActivity {
    TextView name;
    TextView level;
    TextView id;
    TextView gpa;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(getApplicationContext());

        name = findViewById(R.id.name);
        level = findViewById(R.id.level);
        gpa = findViewById(R.id.gpa);
        id = findViewById(R.id.id);

        name.setText("Name: "+sessionManager.getName());
        id.setText("ID: "+sessionManager.getId().toString());
        gpa.setText("GPA: "+sessionManager.getGPA().toString());
        level.setText("Level: "+sessionManager.getLevel().toString());
    }
}