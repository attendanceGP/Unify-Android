package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Home extends AppCompatActivity {
    TextView name;
    TextView level;
    TextView id;
    TextView gpa;
    TextView token;
    TextView type;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(getApplicationContext());

        name = findViewById(R.id.name);
        level = findViewById(R.id.level);
        gpa = findViewById(R.id.gpa);
        token = findViewById(R.id.token);
        id = findViewById(R.id.id);
        type = findViewById(R.id.type);

        name.setText(sessionManager.getName());
        token.setText(sessionManager.getToken());
        type.setText(sessionManager.getType());
    }
}