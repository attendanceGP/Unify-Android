package com.example.attendance.Announcement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.attendance.APIClient;
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.Deadline.DeadlineStudentActivity;
import com.example.attendance.Home;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.UserAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Announcement_Student_Activity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView announcementRecyclerView;

    private RecyclerView filterRecyclerView;

    private Context context;

    private AnnouncementsStudentListAdapter announcementsStudentListAdapter;

    private AnnouncementFilterListAdapter announcementFilterListAdapter;

    SessionManager sessionManager;

    List<Announcement> announcementList = new ArrayList<>();

    List<Announcement> unchangedAnnouncementList = new ArrayList<>();


    ArrayList<String> courseCodes = new ArrayList<>();

    //will be turned into an array on api call
    ArrayList<String> activeFilters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_student);

        sessionManager = new SessionManager(getApplicationContext());

        context = this;



        // binding to the swipe layout to refesh the page
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.student_announcement_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("test", "hello");
                refreshAnnouncements();
            }
        });
        //-----------------------------------------------------------------------------------------------------------------------------------
        //setting up recycler view for course filter-----------------------------------------------------------------------------------------
        //getFilterCourses();

        courseCodes.add("All");
        getFilterCourses();
        filterRecyclerView =(RecyclerView) findViewById(R.id.course_filters_rv);
        announcementFilterListAdapter = new AnnouncementFilterListAdapter(courseCodes,context);
//
        filterRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        filterRecyclerView.setItemAnimator(new DefaultItemAnimator());
        filterRecyclerView.setAdapter(announcementFilterListAdapter);

        //----------------------------------------------------------------------------------------------------------
        //setting up the recycler view for student announcements-----------------------------------------------------------------------------
        announcementRecyclerView = (RecyclerView) findViewById(R.id.student_announcements);

        // instantiating new list adapter for student announcements
        announcementsStudentListAdapter = new AnnouncementsStudentListAdapter(announcementList,context); // sending context as well

        announcementRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        announcementRecyclerView.setItemAnimator(new DefaultItemAnimator());
        announcementRecyclerView.setAdapter(announcementsStudentListAdapter);
        updateData();
        //------------------------------------------------------------------------------------------------------------------------------
        //nav bar-----------------------------------------------------------------------------------------------------------------------
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_deadlines:
                        startActivity(new Intent(Announcement_Student_Activity.this, DeadlineStudentActivity.class));
                        return true;

                    case R.id.action_home:
                        startActivity(new Intent(Announcement_Student_Activity.this, Home.class));
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

    //gets all announcements in all the courses in which the current user is registered and uses syncAnnouncements to fill the room database and the announcement
    //arrayList we have which populates the announcements recyclerView
    private void refreshAnnouncements(){
        AnnouncementAPI announcementAPI = APIClient.getClient().create(AnnouncementAPI.class);
        Call<List<Announcement>> call = announcementAPI.getStudentAnnouncements(sessionManager.getId());

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

    //this function is used to get all the courses a user is registered in to fill the arraylist we use to populate the filters recyclerView
    private void getFilterCourses(){

        UserAPI userAPI = APIClient.getClient().create(UserAPI.class);
        Call<ArrayList<String>> call = userAPI.getTaughtCourses(sessionManager.getId());

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if(response.code() != 200){
                    Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                }
                else{
                    courseCodes.addAll(response.body());
                    announcementFilterListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                System.out.println(t.getMessage());
            }
        });
    }


    //fills the announcementList used for the list adapter with all the announcements we have in our room database in descending order according to date
    private void updateData(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                announcementList.clear();
                announcementList.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().announcementDao().getAll());

                unchangedAnnouncementList.clear();
                unchangedAnnouncementList.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().announcementDao().getAll());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        announcementsStudentListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    //drops the current announcement room table and inserts all announcements that we returned from the api into a list into the room database
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

    //filters all announcements according to selected filter in the filter recycler view
    public void filter(String courseId) {
        if (courseId.equals("All")) {
            Log.d("temp list size",""+unchangedAnnouncementList.size());
            //here we reset the filter before getting all the announcements without any filters
            activeFilters.clear();
            updateData();
        }

        else if(activeFilters.contains(courseId)) {
            //this is used for when a filter is applied and we want to remove it and apply the remaining filters in the filter array if any,
            //if none exist we get all the announcements with no filters
            activeFilters.remove(courseId);

            if(activeFilters.size() == 0){
                updateData();
            }

            else {
                announcementList.clear();
                announcementList.addAll(unchangedAnnouncementList);

                for (int i = 0; i < announcementList.size(); i++) {
                    if (!activeFilters.contains(announcementList.get(i).getCourseId())) {
                        announcementList.remove(i);
                    }
                }

                announcementsStudentListAdapter.notifyDataSetChanged();
            }
        }
        else{

            activeFilters.add(courseId);

            announcementList.clear();
            announcementList.addAll(unchangedAnnouncementList);

            for (int i = 0; i < announcementList.size(); i++) {
                if(!activeFilters.contains(announcementList.get(i).getCourseId())){
                    announcementList.remove(i);
                }
            }

            announcementsStudentListAdapter.notifyDataSetChanged();
        }
    }
}