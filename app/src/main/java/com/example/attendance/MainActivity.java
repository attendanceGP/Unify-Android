package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.attendance.Database.AppDatabase;
import com.example.attendance.Deadline.DeadlineStudentActivity;
import com.example.attendance.Deadline.DeadlineTAActivity;

public class MainActivity extends AppCompatActivity {
    SessionManager sessionManager;

    private EditText username;
    private EditText password;
    private Button login;
    private String queryUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if logged in go to home page
        sessionManager = new SessionManager(getApplicationContext());
        
        if (sessionManager.isLoggedIn()) {
            if(sessionManager.getType().equals("student")) {
                startActivity(new Intent(MainActivity.this, Home.class));
            }else{
                startActivity(new Intent(MainActivity.this, TA_home.class));
            }
        }

        setContentView(R.layout.login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        // when we click activity_login, one of these scenarios will happen:
        // 1- incorrect username or password, the app will let the user try again
        // 2- correct credentials, the user will be logged in and sent to the home activity
        // and their detail will be persisted in the app
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAPI userAPI = APIClient.getClient().create(UserAPI.class);

                Call<User> call = userAPI.login(username.getText().toString(), password.getText().toString());

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User test = response.body();
                        if(response.code() != 200){
                            Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                        }else if(test.getErrorCode() == 1){
                            Toast.makeText(getApplicationContext(), "you can only activity_login once every 30 days", Toast.LENGTH_LONG).show();
                        }else if(test.getErrorCode() == 2){
                            Toast.makeText(getApplicationContext(), "incorrect username or password", Toast.LENGTH_SHORT).show();
                        }else {
                            sessionManager.login(response.body());
                            if(sessionManager.getType().equals("student")) {
                                startActivity(new Intent(MainActivity.this, Home.class));
                            }
                            else{
                                startActivity(new Intent(MainActivity.this, TA_home.class));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //setContentView(R.layout.activity_ta_attendance);
    }
}