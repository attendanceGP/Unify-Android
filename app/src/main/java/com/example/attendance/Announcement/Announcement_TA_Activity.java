package com.example.attendance.Announcement;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.attendance.APIClient;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.UserAPI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Announcement_TA_Activity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;

    private Button addButton;

    private Button postAnnouncement;

    private Context context;

    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_ta);
        sessionManager = new SessionManager(getApplicationContext());

        // binding to the swipe layout to refesh the page
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ta_announcement_swipe_layout);

        addButton = (Button) findViewById(R.id.add_button);
        context = this;

        // inflating the popup when clicked
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.add_announcement_popup, null);

                // to make the popup rounded
                dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // to add animation
                dialogBuilder.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                final EditText title = (EditText) dialogView.findViewById(R.id.announcement_title);
                EditText description = (EditText) dialogView.findViewById(R.id.announcement_description);

                //course spinner-----------------------------------------------------------------------------
                Spinner selectCourse = (Spinner) dialogView.findViewById(R.id.announcement_course);

                ArrayAdapter<String> adapter;
                ArrayList<String> givenCourses = new ArrayList<String>();

                UserAPI userAPI = APIClient.getClient().create(UserAPI.class);

                Call<ArrayList<String>> call = userAPI.getTaughtCourses(sessionManager.getId());
                call.enqueue(new Callback<ArrayList<String>>() {
                    @Override
                    public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                        if (response.code() != 200) {
                            Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            for(int i=0; i<response.body().size(); i++){
                                String courseId = response.body().get(i);
                                givenCourses.add(courseId);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "please check your connection", Toast.LENGTH_SHORT).show();
                    }

                });

                givenCourses.add("None");

                // use default spinner item to show options in spinner
                adapter = new ArrayAdapter<>(context, R.layout.course_spinner_item, givenCourses);
                selectCourse.setAdapter(adapter);
                adapter.setDropDownViewResource(R.layout.course_dropdown_item);
                selectCourse.setPrompt("Courses");
                selectCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String text = parent.getItemAtPosition(position).toString();
//                        Toast.makeText(parent.getContext(), "selected: " + text, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                postAnnouncement = (Button) dialogView.findViewById(R.id.post_announcement);

                // when clicked, check that all fields are valid and then send to the api
                postAnnouncement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String postTitle = title.getText().toString();
                        String courseCode =selectCourse.getSelectedItem().toString();
                        String postDescription = description.getText().toString();

                        if(postTitle.equals("")){
                            Toast.makeText(context, "announcement title name cannot be empty", Toast.LENGTH_SHORT).show();
                        }else if(courseCode.equals("None")){
                            Toast.makeText(context, "please select a course", Toast.LENGTH_SHORT).show();
                        }else if(postDescription.equals("")){
                            Toast.makeText(getApplicationContext(), "announcement description can't be empty", Toast.LENGTH_SHORT).show();
                        }else{
                            java.util.Date date = new java.util.Date();
                            postAnnouncement(sessionManager.getId(), courseCode, date, postTitle, postDescription);
                            dialogBuilder.dismiss();
                        }
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
            }
        });
    }

    private void postAnnouncement(int userId, String courseId, Date postedDate, String title, String post) {
        AnnouncementAPI announcementAPI = APIClient.getClient().create(AnnouncementAPI.class);

        Call<Integer> call = announcementAPI.postDeadline(userId, courseId,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(postedDate),
                title,post
                );

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer integer = response.body();

                if(response.code() != 200){
                    Toast.makeText(getApplicationContext(), "announcement added", Toast.LENGTH_SHORT).show();
                }

                //refreshDeadlines();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}