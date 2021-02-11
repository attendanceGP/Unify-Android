package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Home extends AppCompatActivity {
    TextView name;
    TextView level;
    TextView id;
    TextView gpa;
    TextView token;
    TextView type;
    Button button;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(getApplicationContext());

/*
        name = findViewById(R.id.name);
        level = findViewById(R.id.level);
        gpa = findViewById(R.id.gpa);
        token = findViewById(R.id.token);
        id = findViewById(R.id.id);
        type = findViewById(R.id.type);

        name.setText(sessionManager.getName());
        token.setText(sessionManager.getToken());
        type.setText(sessionManager.getType());
*/

        button = (Button)findViewById(R.id.coursesButton);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this, AttendanceActivity.class));
            }
        });
    }
}