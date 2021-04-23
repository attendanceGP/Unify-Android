package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.DataSetObserver;
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
//    private TextView attendanceDetails; // name of course, group
    private TextView attendanceCount; // total attendees, total registrants, total absent
    private EditText et_inputID;
    private Button buttonConfirm;
    private Button buttonAdd;
    private RecyclerView rv_studentsList;
    private recyclerView_studentsList_adapter rv_adapter;
    private List<Attendance> List_Attendance;

    private Intent intent;
    private String CourseID;
    private String Group;
    private String str_date;
    private UserAPI userAPI;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ta_attendance);
        this.intent = getIntent();
        this.CourseID = intent.getStringExtra("CoureID_Key");
        this.Group = intent.getStringExtra("Group_Key");
        this.str_date = intent.getStringExtra("Date_Key");

        setViews();


        // -------------  Calling the API to get Students List ----------------------
        userAPI = APIClient.getClient().create(UserAPI.class);

        Call<List<Attendance>> call = userAPI.getStudentsList(CourseID, Group, str_date);
        call.enqueue(new Callback<List<Attendance>>() {
            @Override
            public void onResponse(Call<List<Attendance>> call, Response<List<Attendance>> response) {
                List<Attendance> std_List = response.body();
                if(response.code() != 200){
                    Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                }else {
                    List_Attendance = std_List;
                    System.out.println(std_List.size());
                    rv_studentsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rv_adapter = new recyclerView_studentsList_adapter(getApplicationContext(), List_Attendance, str_date);
                    rv_studentsList.setAdapter(rv_adapter);

                    setAttendanceCount();
                    et_inputID.setEnabled(true);


                    rv_adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onChanged() {
                            setAttendanceCount();
                        }
                    });

                    buttonConfirm.setClickable(true);
                    buttonConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Call<Void> confirm_list = userAPI.confirmList(CourseID, Group, str_date);
                            confirm_list.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    Toast.makeText(getApplicationContext(), "confirmed", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ta_attendanceConfirmationList.this, TA_home.class));
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "an error occured", Toast.LENGTH_SHORT).show();
                                }
                            });
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
                                Call<Attendance> call_add = userAPI.setAbsence(CourseID, Group, str_date, studentID, false);
                                call_add.enqueue(new Callback<Attendance>() {
                                    @Override
                                    public void onResponse(Call<Attendance> call, Response<Attendance> response) {
                                        Attendance test = response.body();
                                        if (response.code() != 200) {
                                            Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                                        } else if(test.getError_code() == 2) {
                                            Toast.makeText(getApplicationContext(), "wrong ID provided", Toast.LENGTH_SHORT).show();
                                        } else if (test.getError_code() == 4) {
                                            Toast.makeText(getApplicationContext(), "this student is already present", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), studentID+" is added", Toast.LENGTH_SHORT).show();
                                            rv_adapter.addItem(test);
                                            setAttendanceCount();
                                            et_inputID.getText().clear();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<Attendance> call, Throwable t) {
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

    void setAttendanceCount(){
        attendanceCount.setText(rv_adapter.getItemCount() + " students attended");
    }

    void setViews(){
//        attendanceDetails = findViewById(R.id.textView_SectionNumber);
        attendanceCount = findViewById(R.id.textView_StudentsCount);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        rv_studentsList = findViewById(R.id.rv_studentsList);
        buttonAdd = findViewById(R.id.buttonAddStudent);
        et_inputID = findViewById(R.id.et_InputID);

        buttonConfirm.setClickable(false);
        buttonAdd.setClickable(false);
        et_inputID.setEnabled(false);
    }

}