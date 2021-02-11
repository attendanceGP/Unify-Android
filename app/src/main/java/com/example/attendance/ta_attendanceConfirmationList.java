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

import java.text.SimpleDateFormat;
import java.util.List;

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
                    rv_adapter = new recyclerView_studentsList_adapter(getApplicationContext(), List_Attendance);
                    rv_studentsList.setAdapter(rv_adapter);
                    attendanceCount.setText("attendees = ");
                    et_inputID.setEnabled(true);

                    buttonConfirm.setClickable(true);
                    buttonConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
                        }

                    });

                    buttonAdd.setClickable(true);
                    buttonAdd.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view){
                            Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
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
}