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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.API.APIClient;
import com.example.attendance.Absence.AbsenceStudentActivity;
import com.example.attendance.Course.Course;
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.Deadline.DeadlineStudentActivity;
import com.example.attendance.Forums.ForumsActivity;
import com.example.attendance.Home.HomeStudentActivity;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.Home.HomeTAActivity;
import com.example.attendance.Home.UserAPI;
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

    private TextView noAnnouncementsTV;

    private Context context;

    private AnnouncementsStudentListAdapter announcementsStudentListAdapter;

    private AnnouncementFilterListAdapter announcementFilterListAdapter;

    SessionManager sessionManager;

    List<Announcement> announcementList = new ArrayList<>();

    List<Announcement> unchangedAnnouncementList = new ArrayList<>();


    List<String> courseCodes = new ArrayList<>();

    //will be turned into an array on api call
    ArrayList<String> activeFilters = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_student);

        sessionManager = new SessionManager(getApplicationContext());

        context = this;

        noAnnouncementsTV = (TextView) findViewById(R.id.empty_student_announcements);

        // binding to the swipe layout to refesh the page
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.student_announcement_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("test", "hello");
                refreshAnnouncements();
                getFilterCourses();
                //resets all button colors to show that they have been unselected
                for(int i=0;i<courseCodes.size();i++) {
                    filterRecyclerView.getLayoutManager().findViewByPosition(i).
                            findViewById(R.id.course_filter_button).setBackgroundResource(R.drawable.announcement_filter_button_unselected);
                }

                //here we reset the filter before getting all the announcements without any filters
                activeFilters.clear();
            }
        });

        //-----------------------------------------------------------------------------------------------------------------------------------

        //setting up recycler view for course filter-----------------------------------------------------------------------------------------

        filterRecyclerView =(RecyclerView) findViewById(R.id.course_filters_rv);
        announcementFilterListAdapter = new AnnouncementFilterListAdapter(courseCodes,context);
        filterRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        filterRecyclerView.setItemAnimator(new DefaultItemAnimator());
        filterRecyclerView.setAdapter(announcementFilterListAdapter);
        updateCourseData();
        getFilterCourses();

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
        refreshAnnouncements();

        //------------------------------------------------------------------------------------------------------------------------------

        //nav bar-----------------------------------------------------------------------------------------------------------------------
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_deadlines:
                        startActivity(new Intent(Announcement_Student_Activity.this, DeadlineStudentActivity.class));
                        finish();
                        return true;

                    case R.id.action_home:
                        startActivity(new Intent(Announcement_Student_Activity.this, HomeStudentActivity.class));
                        finish();
                        return true;

                    case R.id.action_forum:
                        startActivity(new Intent(Announcement_Student_Activity.this, ForumsActivity.class));
                        finish();
                        return true;

                    case R.id.action_absence:
                        startActivity(new Intent(Announcement_Student_Activity.this, AbsenceStudentActivity.class));
                        finish();
                        return true;
                }
                return false;
            }
        });

    }

    ////////////////////////////////////ROOM ANNOUNCEMENT FUNCTIONS/////////////////////////////////////////////////////////////////////////////////
    /**
     *
     * void refreshAnnouncements()
     *
     * Summary of the refreshAnnouncements function:
     *
     *    updates the announcementList from the database.
     *
     * Parameters   : none.

     * Return Value : Nothing .
     *
     * Description:
     *
     *
     *
     *    This function is used to get all the announcements from the database for
     *    the courses and the groups in which the current user is registered and uses syncAnnouncements to
     *    fill the room database and the announcementList we have which populates the announcements recyclerView
     */
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
                Toast.makeText(getApplicationContext(), "you are now seeing all announcements but they are not up to date, please check" +
                        " your internet connection and try again", Toast.LENGTH_SHORT).show();
                updateData();
                System.out.println(t.getMessage());
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     *
     * void updateData()
     *
     * Summary of the updateData function:
     *
     *    updates the announcementList data.
     *
     * Parameters   : none.

     * Return Value : Nothing .
     *
     * Description:
     *
     *    This function is used to update the data in the announcementList by clearing the list
     *    and adding all the announcements from the room database to it.
     *
     */
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

                        if(announcementList.isEmpty()){
                            announcementRecyclerView.setVisibility(View.GONE);
                            noAnnouncementsTV.setVisibility(View.VISIBLE);
                        }
                        else{
                            announcementRecyclerView.setVisibility(View.VISIBLE);
                            noAnnouncementsTV.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    /**
     *
     * void syncAnnouncementsFromAPI(List<Announcement> announcements)
     *
     * Summary of the syncAnnouncementsFromAPI function:
     *
     *    updates the room database.
     *
     * Parameters   : announcements: a list of the announcements retrieved from the api call
     *                  in refreshAnnouncements.

     * Return Value : Nothing .
     *
     * Description:
     *
     *    This function is used to update the data in the room database for the announcements
     *    by dropping all announcements already present in the room database and putting in
     *    the new announcements retrieved from the api.
     *
     */
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


    ////////////////////////////////////ROOM COURSE FUNCTIONS/////////////////////////////////////////////////////////////////////////////////
    /**
     *
     * void getFilterCourses()
     *
     * Summary of the getFilterCourses function:
     *
     *    updates the room database.
     *
     * Parameters   : none.

     * Return Value : Nothing .
     *
     * Description:
     *
     *     this function is used to get all the courses a user is
     *     registered in to fill the arraylist we use to populate the filters recyclerView
     *     calling syncCoursesFromAPI and giving it the course codes returned from the api
     *     call
     *
     *
     */
    private void getFilterCourses(){

        UserAPI userAPI = APIClient.getClient().create(UserAPI.class);
        //return all course codes where the user is enrolled
        Call<ArrayList<String>> call = userAPI.getTaughtCourses(sessionManager.getId());

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                ArrayList<String> courses= response.body();

                if(response.code() != 200){
                    Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                }

                //used to fill room database table for courses with the courses we got from the api
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

    //fills the courseCodes List used for the list adapter with all the courses we have in our room database
    /**
     *
     * void updateCourseData()
     *
     * Summary of the updateCourseData function:
     *
     *    updates the courseCodes data.
     *
     * Parameters   : none.

     * Return Value : Nothing .
     *
     * Description:
     *
     *    This function is used to update the data in the courseList by clearing the list
     *    and adding all the course codes from the room database to it.
     *
     */
    private void updateCourseData(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                courseCodes.clear();
                courseCodes.add("All");
                //adding all the course codes for the user from the room database
                courseCodes.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().courseDAO().getAll());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        announcementFilterListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    /**
     *
     * void syncCoursesFromAPI(ArrayList<String> courseCodes)
     *
     * Summary of the syncCoursesFromAPI function:
     *
     *    updates the room course database table.
     *
     * Parameters   : courseCodes: a list of the courseCodes retrieved from the api call
     *                  in getFilterCourses.

     * Return Value : Nothing .
     *
     * Description:
     *
     *    This function is used to update the data in the room database for the courses
     *    by dropping all courses already present in the room database and putting in
     *    the new courses retrieved from the api.
     *
     */
    private void syncCoursesFromAPI(ArrayList<String> courseCodes){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //clearing the room database
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().courseDAO().nukeTable();

                ArrayList<Course> courses = new ArrayList<>();
                //making an arraylist of courses used to populate room
                for(int i=0;i<courseCodes.size();i++){
                    Course nc= new Course(i,courseCodes.get(i));
                    courses.add(nc);
                }

                //adding the new courses to the room database
                // if exists
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance")
                        .build().courseDAO().insertAll(courses.toArray(new Course[courses.size()]));

                updateCourseData();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }


    //filters all announcements according to selected filter in the filter recycler view
    /**
     *
     * void filter(String courseId)
     *
     * Summary of the filter function:
     *
     *    filters all announcements according to selected course code in the filter recycler view.
     *
     * Parameters   : courseId: the course code which we will filter using.

     * Return Value : Nothing .
     *
     * Description:
     *
     *    This function is used to filter the announcements in our recycler view according to the courses
     *    clicked from the filter recycler view, if all is chosen, all filters are reset and we see all
     *    announcements.
     *
     *    if a filter is already chosen and is present in the filter list, the filter
     *    the highlight on the filter is removed from the
     *    activeFilters arrayList and the announcements are shown without that filter being applied
     *    while applying all the others in the activeFilters arraylist.
     *
     *    if a filter is not highlighted meaning it's not chosen, we add it to the filters list, highlight its
     *    button in the filter recycler view and show all the announcements with that filter applied along with
     *    all the others in the activeFilters arraylist.
     *
     */
    public void filter(String courseId) {
        ArrayList<Announcement> undeletedAnnouncements = new ArrayList<>();

        if (courseId.equals("All")) {
            //resets all button colors to show that they have been unselected
            for(int i=0;i<courseCodes.size();i++) {
                filterRecyclerView.getLayoutManager().findViewByPosition(i).
                        findViewById(R.id.course_filter_button).setBackgroundResource(R.drawable.announcement_filter_button_unselected);
            }

            //here we reset the filter before getting all the announcements without any filters
            activeFilters.clear();
            updateData();
        }

        //this is used for when a filter is applied and we want to remove it and apply the remaining filters in the filter array if any,
        //if none exist we get all the announcements with no filters
        else if(activeFilters.contains(courseId)) {

            filterRecyclerView.getLayoutManager().findViewByPosition(courseCodes.indexOf(courseId)).
                    findViewById(R.id.course_filter_button).setBackgroundResource(R.drawable.announcement_filter_button_unselected);

            activeFilters.remove(courseId);

            //this is the case where no filters are applied so we show all announcements unfiltered
            if(activeFilters.size() == 0){
                updateData();
            }

            else {
                announcementList.clear();
                undeletedAnnouncements.clear();

                announcementList.addAll(unchangedAnnouncementList);

                //here we add only the announcements in which the filter is applicable to the
                //undeletedAnnouncements List
                for (int i = 0; i < announcementList.size(); i++) {
                    if (activeFilters.contains(announcementList.get(i).getCourseId())) {
                        undeletedAnnouncements.add(announcementList.get(i));
                    }
                }

                announcementList.clear();
                announcementList.addAll(undeletedAnnouncements);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        announcementsStudentListAdapter.notifyDataSetChanged();
                    }
                });

            }
        }
        else{
            //setting the clicked button color to darker green to show that it is selected
            filterRecyclerView.getLayoutManager().findViewByPosition(courseCodes.indexOf(courseId)).
                    findViewById(R.id.course_filter_button).setBackgroundResource(R.drawable.announcement_filter_button_selected);

            activeFilters.add(courseId);

            announcementList.clear();
            undeletedAnnouncements.clear();

            announcementList.addAll(unchangedAnnouncementList);

            for (int i = 0; i < announcementList.size(); i++) {
                if(activeFilters.contains(announcementList.get(i).getCourseId())){
                    undeletedAnnouncements.add(announcementList.get(i));
                }
            }

            announcementList.clear();
            announcementList.addAll(undeletedAnnouncements);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    announcementsStudentListAdapter.notifyDataSetChanged();
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        if(sessionManager.getType().equals("student")){
            startActivity(new Intent(Announcement_Student_Activity.this, HomeStudentActivity.class));
            finish();
        }else{
            startActivity(new Intent(Announcement_Student_Activity.this, HomeTAActivity.class));
            finish();
        }
    }
}