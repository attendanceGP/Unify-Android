package com.example.attendance.Announcement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.attendance.APIClient;
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.Deadline.DeadlineTAActivity;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.TA_home;
import com.example.attendance.UserAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Announcement_TA_Activity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;

    private Button addButton;

    private Button postAnnouncement;

    private RecyclerView announcementRecyclerView;

    private Context context;

    private AnnouncementsTAListAdapter announcementTaListAdapter;

    SessionManager sessionManager;

    List<Announcement> announcementList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_ta);
        sessionManager = new SessionManager(getApplicationContext());
        context = this;

            // binding to the swipe layout to refesh the page
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ta_announcement_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("test", "hello");
                refreshAnnouncements();
            }
        });

        //----------------------------------------------------------------------------------------------------------
        announcementRecyclerView = (RecyclerView) findViewById(R.id.announcements_you_posted);

        // instantiating new list adapter for announcements
        announcementTaListAdapter = new AnnouncementsTAListAdapter(announcementList,context); // sending context as well

        announcementRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        announcementRecyclerView.setItemAnimator(new DefaultItemAnimator());
        announcementRecyclerView.setAdapter(announcementTaListAdapter);


        //----------------------------------------------------------------------------------------------------------
        // inflating the popup when clicked
        addButton = (Button) findViewById(R.id.add_button);
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

                final EditText title = (EditText) dialogView.findViewById(R.id.announcement_title_in_popup);
                EditText description = (EditText) dialogView.findViewById(R.id.announcement_description_in_pop_up);

                //course spinner-----------------------------------------------------------------------------
                Spinner selectCourse = (Spinner) dialogView.findViewById(R.id.announcement_course_in_pop_up);

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

                //-----------------------------------------------------------------------------------------
                //post announcement button-----------------------------------------------------------------

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
        updateData();

        //------------------------------------------------------------------------------------------------------------------------------
        //nav bar-----------------------------------------------------------------------------------------------------------------------
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_deadlines:
                        startActivity(new Intent(Announcement_TA_Activity.this, DeadlineTAActivity.class));
                        return true;

                    case R.id.action_home:

                        startActivity(new Intent(Announcement_TA_Activity.this, TA_home.class));
                        return true;

                    case R.id.action_forum:
                        return true;

                    case R.id.action_absence:
                        return true;
                }
                return false;
            }
        });
    }

    //-------------------------------------------------------------------------------------------------------------------------------
    private void postAnnouncement(int userId, String courseId, Date postedDate, String title, String post) {
        AnnouncementAPI announcementAPI = APIClient.getClient().create(AnnouncementAPI.class);

        Call<Integer> call = announcementAPI.postAnnouncement(userId, courseId,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(postedDate),
                title, post);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer integer = response.body();

                if (response.code() != 200) {
                    Toast.makeText(getApplicationContext(), "your announcement couldn't be added", Toast.LENGTH_SHORT).show();
                }

                refreshAnnouncements();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void deleteAnnouncement(int id) {
        AnnouncementAPI announcementAPI = APIClient.getClient().create(AnnouncementAPI.class);

        Call<Integer> call = announcementAPI.deleteAnnouncement(id);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer integer = response.body();

                if (response.code() == 200) {
                    Toast.makeText(getApplicationContext(), "announcement deleted", Toast.LENGTH_SHORT).show();
                }

                refreshAnnouncements();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refreshAnnouncements(){
        AnnouncementAPI announcementAPI = APIClient.getClient().create(AnnouncementAPI.class);

        Call<List<Announcement>> call = announcementAPI.getTaAnnouncements(sessionManager.getId());

        call.enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {
                List<Announcement> announcements =response.body();
                if(response.code() != 200){
                    Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                }
                syncAnnouncementsFromAPI(announcements);
            }

            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                System.out.println(t.getMessage());
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void updateData(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                announcementList.clear();
                announcementList.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().announcementDao().getAll());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        announcementTaListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void syncAnnouncementsFromAPI(List<Announcement> announcements){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().announcementDao().nukeTable();

                //System.out.println(announcement.getId());
                // if exists
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance")
                        .build().announcementDao().insertAll(announcements.toArray(new Announcement[announcements.size()]));

                updateData();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }
}