package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// https://guides.codepath.com/android/using-the-recyclerview

public class ta_attendanceConfirmationList extends AppCompatActivity {
    private TextView attendanceDetails; // name of course, group
    private TextView attendanceCount; // total attendees, total registrants, total absent
    private EditText et_inputID;
    private Button buttonConfirm;
    private Button buttonAdd;
    private RecyclerView rv_studentsList;
    private recyclerView_studentsList_adapter rv_adapter;
    private List<Attendance> List_Attendance;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ta_attendance);
        Intent intent = getIntent();
        String CourseID = intent.getStringExtra("CoureID_Key");
        String Group = intent.getStringExtra("Group_Key");
        String str_date = intent.getStringExtra("Date_Key");

        attendanceDetails = findViewById(R.id.textView_SectionNumber);
        attendanceCount = findViewById(R.id.textView_StudentsCount);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        rv_studentsList = findViewById(R.id.rv_studentsList);
        buttonAdd = findViewById(R.id.buttonAddStudent);
        et_inputID = findViewById(R.id.et_InputID);

        buttonConfirm.setClickable(false);
        buttonAdd.setClickable(false);
        et_inputID.setEnabled(false);

        attendanceDetails.setText(CourseID+"-"+Group+"\n"+str_date);


        // -------------  Calling the API to get Students List ----------------------
        UserAPI userAPI = APIClient.getClient().create(UserAPI.class);

        Call<List<Attendance>> call = userAPI.getStudentsList(CourseID, Group, str_date);
        call.enqueue(new Callback<List<Attendance>>() {
            @Override
            public void onResponse(Call<List<Attendance>> call, Response<List<Attendance>> response) {
                List<Attendance> std_List = response.body();
                if(response.code() != 200){
                    Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                }else {
                    List_Attendance = std_List;

                    rv_studentsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rv_adapter = new recyclerView_studentsList_adapter(getApplicationContext(), List_Attendance, str_date);
                    rv_studentsList.setAdapter(rv_adapter);
                    attendanceCount.setText("attendees = " + rv_adapter.getItemCount());
                    et_inputID.setEnabled(true);

                    buttonConfirm.setClickable(true);
                    buttonConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(), "confirmed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ta_attendanceConfirmationList.this, Home.class));
                        }

                    });

                    buttonAdd.setClickable(true);
                    buttonAdd.setText("Add");
                    buttonAdd.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            String studentID_input = et_inputID.getText().toString();
                            if(studentID_input.equals("")){
                                Toast.makeText(getApplicationContext(), "You did not enter an ID", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Integer studentID = Integer.parseInt(studentID_input);
                                Call<ResponseBody> call_add = userAPI.setPresent(CourseID, Group, str_date, studentID);
                                call_add.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        String response_body = null;
                                        try {
                                            response_body = response.body().string();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        if (response.code() != 200) {
                                            Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (response_body.equals("No such ID")) {
                                                Toast.makeText(getApplicationContext(), "wrong ID provided", Toast.LENGTH_SHORT).show();
                                            } else if (response_body.equals("Student is already present")) {
                                                Toast.makeText(getApplicationContext(), "this student is already present", Toast.LENGTH_SHORT).show();
                                            } else if (response_body.equals("Done")) {
                                                Toast.makeText(getApplicationContext(), studentID+" is added", Toast.LENGTH_SHORT).show();
                                                Call<Attendance> addstudent_call = userAPI.getStudent(CourseID, Group, str_date, studentID);
                                                addstudent_call.enqueue(new Callback<Attendance>(){
                                                    @Override
                                                    public void onResponse(Call<Attendance> call, Response<Attendance> response){
                                                        Attendance att = response.body();
                                                        rv_adapter.addItem(att);
                                                        attendanceCount.setText("attendees = " + rv_adapter.getItemCount());
                                                    }
                                                    @Override
                                                    public void onFailure(Call<Attendance> call, Throwable t){
                                                        Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getApplicationContext(), "wrong data provided", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Attendance>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "wrong data provided", Toast.LENGTH_SHORT).show();
                System.out.println(t.getCause());
            }
        });

    }

    void setAttendanceCount(int count){
        attendanceCount.setText("attendees = " + rv_adapter.getItemCount());
    }
}