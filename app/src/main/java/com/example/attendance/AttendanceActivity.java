/*
package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    LinearLayout.LayoutParams layoutParams;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        sessionManager= new SessionManager(getApplicationContext());
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        layoutParams = new LinearLayout.LayoutParams(layoutParams.WRAP_CONTENT,layoutParams.WRAP_CONTENT);
        Call<String[]> call = APIClient.getClient().create(UserAPI.class).getStudentCourses(sessionManager.getId());
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                String[] sol = response.body();
                if (sol != null){
                    for (int i = 0; i <sol.length ; i++) {
                        TextView textView =new TextView(AttendanceActivity.this);
                        linearLayout.addView(textView);
                        textView.setText(sol[i]);
                        Button but = new Button(AttendanceActivity.this);
                        linearLayout.addView(but,layoutParams);
                        but.setText("Attend");
                        but.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Date date = new Date();
                                Call<String> call2 = APIClient.getClient().create(UserAPI.class)
                                        .attend(sessionManager.getId(),textView.getText().toString(),new SimpleDateFormat("dd-MM-yyyy").format(date));
                                call2.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                    String action = response.body();
                                    Toast.makeText(AttendanceActivity.this,action,Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(AttendanceActivity.this,"onFailure error",Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {

            }
        });
    }
}*/
