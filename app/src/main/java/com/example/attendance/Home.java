package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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


        /// todo put in the appropriate activity
        Intent intent = new Intent(Home.this, ta_attendanceConfirmationList.class);
        intent.putExtra("CoureID_Key", "CS467");
        intent.putExtra("Group_Key", "G1");
        intent.putExtra("Date_Key", "29-03-2020");
        startActivity(intent);

    }
}