package com.example.attendance.Forums;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.attendance.APIClient;
import com.example.attendance.Course.Course;
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.UserAPI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddForumActivity extends AppCompatActivity {
    private EditText title;
    private EditText description;
    private Button postButton;
    private Spinner courseSpinner;
    ArrayList<String> courses = new ArrayList<>();


    ForumsAPI forumsAPI;
    UserAPI userAPI;
    SessionManager sessionManager;
    String selectedCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_forum);
        this.sessionManager = new SessionManager(getApplicationContext());
        forumsAPI = APIClient.getClient().create(ForumsAPI.class);
        userAPI = APIClient.getClient().create(UserAPI.class);

        title = findViewById(R.id.new_post_title_edit_text);
        description = findViewById(R.id.new_post_description);
        postButton = findViewById(R.id.post_button);
        courseSpinner = (Spinner) findViewById(R.id.new_post_course);

        setSpinner();
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCourse = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_title = title.getText().toString();
                String str_description = description.getText().toString();
                if(str_title.equals("")){
                    Toast.makeText(getApplicationContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(str_description.equals("")){
                    Toast.makeText(getApplicationContext(), "description cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(courseSpinner.getSelectedItem().toString().equals("Courses")){
                    Toast.makeText(getApplicationContext(), "Course cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    Call<Void> add_post_call = forumsAPI.addPost(sessionManager.getId(), courseSpinner.getSelectedItem().toString(), getDateStringForCurrentDate(),str_title, str_description);
                    add_post_call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(getApplicationContext(), "posted", Toast.LENGTH_SHORT).show();
                            killActivity();
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "check your connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private String getDateStringForCurrentDate(){
        String result = "";
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(currentTime);

        result = "" + strDate;

        dateFormat = new SimpleDateFormat("hh:mm:ss");
        strDate = dateFormat.format(currentTime);

        result = result + " " + strDate;
        return result;
    }
    private void updateCourseData(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                courses.clear();
                courses.add("Courses");
                courses.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().courseDAO().getAll());
            }
        });
    }
    //drops the current courses room table and inserts all coursecodes that we returned from the api into a list into the room database
    private void syncCoursesFromAPI(ArrayList<String> courseCodes){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().courseDAO().nukeTable();

                ArrayList<Course> courses = new ArrayList<>();
                for(int i=0;i<courseCodes.size();i++){
                    Course nc= new Course(i,courseCodes.get(i));
                    courses.add(nc);
                }

                // if exists
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance")
                        .build().courseDAO().insertAll(courses.toArray(new Course[courses.size()]));

                updateCourseData();
            }
        });
    }
    private void getUserCourses() {
        UserAPI userAPI = APIClient.getClient().create(UserAPI.class);
        Call<ArrayList<String>> call = userAPI.getTaughtCourses(sessionManager.getId());

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                ArrayList<String> courses= response.body();
                if(response.code() != 200){
                    Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                }
                syncCoursesFromAPI(courses);
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                updateCourseData();
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                System.out.println(t.getMessage());
            }
        });
    }

    void setSpinner(){
        courses = new ArrayList<>();
        getUserCourses();
        courses.add("Courses");
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.post_course_spinner_item,courses);
        courseSpinner.setAdapter(arrayAdapter);
        courseSpinner.setSelection(arrayAdapter.getCount()-1);
        arrayAdapter.setDropDownViewResource(R.layout.post_course_dropdown_item);
        courseSpinner.setPrompt("Courses");

    }
    void killActivity(){
        Intent intent = new Intent(AddForumActivity.this, ForumsActivity.class);
        startActivity(intent);
        finish();
    }

}