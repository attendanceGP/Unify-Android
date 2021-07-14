package com.example.attendance.Forums;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.APIClient;
import com.example.attendance.Absence.AbsenceTab;
import com.example.attendance.Absence.TAAbsenceTab;
import com.example.attendance.Announcement.Announcement_Student_Activity;
import com.example.attendance.Announcement.Announcement_TA_Activity;
import com.example.attendance.Course.Course;
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.Deadline.DeadlineStudentActivity;
import com.example.attendance.Deadline.DeadlineTAActivity;
import com.example.attendance.Home;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.TA_home;
import com.example.attendance.UserAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumsActivity extends AppCompatActivity {
    private List<Post> posts = new ArrayList<>();
    private List<Post> allPosts = new ArrayList<>();
    private List<Post> userPosts = new ArrayList<>();
    private List<Post> favPosts = new ArrayList<>();

    ArrayList<String> courseCodes = new ArrayList<>();
    ArrayList<String> selectedCourses = new ArrayList<>();

    private RecyclerView postsRecyclerView;
    private PostsListAdapter postsListAdapter;
    private FavPostsListAdapter favPostsListAdapter;
    private UserPostsListAdapter userPostsListAdapter;
    private CoursesListAdapter coursesListAdapter;


    private Button viewHome;
    private Button viewFavourites;
    private Button viewMyForums;
    private Button addForum;


    private RecyclerView coursesRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;
    TextView emptyText;

    ForumsAPI forumsAPI;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums_home_page);
        sessionManager = new SessionManager(getApplicationContext());

        emptyText = (TextView) findViewById(R.id.empty_forums_text);

        // binding to the swipe layout to refresh the page
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.forums_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshForums();
            }
        });

        forumsAPI = APIClient.getClient().create(ForumsAPI.class);

        // courses Recycler View
        coursesRecyclerView = (RecyclerView) findViewById(R.id.course_filters_rv);
        coursesRecyclerView.setLayoutManager((
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)));
        coursesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        coursesListAdapter = new CoursesListAdapter(this, courseCodes);
        coursesRecyclerView.setAdapter(coursesListAdapter);
        updateCourseData();
        getUserCourses();


//       Posts Recycler View
        postsRecyclerView = (RecyclerView) findViewById(R.id.posts_recycler_view);

        postsListAdapter = new PostsListAdapter(this, posts);
        userPostsListAdapter = new UserPostsListAdapter(this, userPosts);
        favPostsListAdapter = new FavPostsListAdapter(this, favPosts);

        postsRecyclerView.setLayoutManager(
                new NPALinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
//        postsRecyclerView.setLayoutManager(
//                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        postsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        postsRecyclerView.setAdapter(postsListAdapter);


        // get data from API and ROOM
        updateData();
        refreshForums();

        // my Forums Button
        viewMyForums = (Button) findViewById(R.id.my_posts_button);
        viewMyForums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserPosts();
            }
        });


        // my Favourites forums Button
        viewFavourites = (Button) findViewById(R.id.my_Favourite_button);
        viewFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getStarredPosts();
            }
        });

        // View Home Forums
        viewHome = (Button) findViewById(R.id.all_posts_button);
        viewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForums();
            }
        });


        // add forum button
        addForum = (Button) findViewById(R.id.add_new_forum);
        addForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForumsActivity.this, AddForumActivity.class);
                startActivity(intent);
            }
        });


        //TODO add the rest of the activities to the bottom nav view when done
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home:
                        if(sessionManager.getType().equals("student")){
                            startActivity(new Intent(ForumsActivity.this, Home.class));
                            finish();
                        }else{
                            startActivity(new Intent(ForumsActivity.this, TA_home.class));
                            finish();
                        }
                        return true;

                    case R.id.action_announcements:
                        if(sessionManager.getType().equals("student")){
                            startActivity(new Intent(ForumsActivity.this, Announcement_Student_Activity.class));
                            finish();
                        }else{
                            startActivity(new Intent(ForumsActivity.this, Announcement_TA_Activity.class));
                            finish();
                        }
                        return true;

                    case R.id.action_deadlines:
                        if(sessionManager.getType().equals("student")){
                            startActivity(new Intent(ForumsActivity.this, DeadlineStudentActivity.class));
                            finish();
                        }else{
                            startActivity(new Intent(ForumsActivity.this, DeadlineTAActivity.class));
                            finish();
                        }
                        return true;

                    case R.id.action_absence:
                        if(sessionManager.getType().equals("student")){
                            startActivity(new Intent(ForumsActivity.this, AbsenceTab.class));
                            finish();
                        }else{
                            startActivity(new Intent(ForumsActivity.this, TAAbsenceTab.class));
                            finish();
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private void updateCourseData(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                courseCodes.clear();
                courseCodes.add("All");
                courseCodes.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().courseDAO().getAll());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        selectedCourses.clear();
                        selectedCourses.add(courseCodes.get(0));
                        coursesListAdapter.notifyDataSetChanged();
                    }
                });
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
                //System.out.println(announcement.getId());
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


    private void updateData(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                allPosts.clear();
                allPosts.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().forumsDao().getAllPosts());

                posts.clear();
                posts.addAll(allPosts);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postsListAdapter.notifyDataSetChanged();
                        filterPosts();  //todo
                        setEmptyTextVisibility(Objects.requireNonNull(postsRecyclerView.getAdapter()).getItemCount()==0);
                    }
                });
            }
        });
    }

    public void refreshForums(){
        Call<List<Post>> getForums_Call = forumsAPI.getForums(sessionManager.getId());
        getForums_Call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> postsListAPI = response.body();
                if(response.code() != 200){
                    Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                }

                syncForumsFromAPI(postsListAPI);
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    // syncs the retrieved deadlines with the ones that already exist in the Room db
    private void syncForumsFromAPI(List<Post> _posts){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for(Post post: _posts) {
                    // if exists
                    if (!Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "attendance").build().forumsDao().isExistsPosts(post.getId())) {
                        Room.databaseBuilder(getApplicationContext(),
                                AppDatabase.class, "attendance").build().forumsDao().insertAllPosts(post);
                    }
                }

                updateData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        });
    }
    //    open post activity
    public void onForumClick(int position, Post clickedPost) {
        Intent intent = new Intent(ForumsActivity.this, PostActivity.class);
        intent.putExtra("Post_id", clickedPost.getId());
        intent.putExtra("course_code", clickedPost.getCourseCode());
        startActivity(intent);
    }

    //    star and un-star posts
    public void removeFromStarred(Integer postId){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().forumsDao().changeToUnStarred(postId);
                updateData();
            }
        });
    }
    public void addToStarred(Integer postId){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().forumsDao().changeToStarred(postId);
                updateData();
            }
        });
    }

    //    delete user post from api and room
    public void deleteUserPost(Integer postId){
        Call<Void> call = forumsAPI.removePost(postId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getApplicationContext(), "removed", Toast.LENGTH_SHORT).show();
                deleteUserPostFromRoom(postId);
                setEmptyTextVisibility(userPosts.size()==0);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void deleteUserPostFromRoom(Integer postId){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().forumsDao().deleteRepliesByPostId(postId);
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().forumsDao().deletePostById(postId);
                updateData();
                refreshForums();
            }
        });
    }

//    show user posts
    public void getUserPosts(){
        userPosts.clear();

        for(Post d: allPosts){
            if(d.getUserId().equals(sessionManager.getId())){
                if(selectedCourses.contains("All") || selectedCourses.contains(d.getCourseCode())){
                    userPosts.add(d);
                }
            }
        }
        postsRecyclerView.setAdapter(userPostsListAdapter);
        userPostsListAdapter.notifyDataSetChanged();
        setEmptyTextVisibility(userPosts.size()==0);
    }
    public void filterUserPosts(){
        userPosts.clear();
        for(Post d: allPosts){
            if(d.getUserId().equals(sessionManager.getId())){
                if(selectedCourses.contains("All") || selectedCourses.contains(d.getCourseCode())){
                    userPosts.add(d);
                }
            }
        }
        userPostsListAdapter.notifyDataSetChanged();
    }

//    show all forums (with no filter)
    public void showForums(){
        postsRecyclerView.setAdapter(postsListAdapter);
        posts.clear();
        for(Post d: allPosts){
            if(selectedCourses.contains("All") || selectedCourses.contains(d.getCourseCode())){
                posts.add(d);
            }
        }
        postsListAdapter.notifyDataSetChanged();
        setEmptyTextVisibility(posts.size()==0);
    }
    public void filterAllPosts(){
        posts.clear();
        for(Post d: allPosts){
            if(selectedCourses.contains("All") || selectedCourses.contains(d.getCourseCode())){
                posts.add(d);
            }
        }
        postsListAdapter.notifyDataSetChanged();
    }

//    show starred posts
    public void getStarredPosts(){
        favPosts.clear();
        for(Post d: allPosts){
            if(d.isStarred()){
                if(selectedCourses.contains("All") || selectedCourses.contains(d.getCourseCode())){
                    favPosts.add(d);
                }
            }
        }
        postsRecyclerView.setAdapter(favPostsListAdapter);
        favPostsListAdapter.notifyDataSetChanged();

        setEmptyTextVisibility(favPosts.size()==0);
    }
    public void filterStarredPosts(){
        favPosts.clear();
        for(Post d: allPosts){
            if(d.isStarred()){
                if(selectedCourses.contains("All") || selectedCourses.contains(d.getCourseCode())){
                    favPosts.add(d);
                }
            }
        }
        favPostsListAdapter.notifyDataSetChanged();
    }

    public void setEmptyTextVisibility(boolean emptyList){
        Log.i("sss", String.valueOf(emptyList));
        if(emptyList){
            postsRecyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else{
            postsRecyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
        Log.i("sss", String.valueOf(emptyList));
    }
    public void onCourseFilterClick(int viewPosition, String course) {

//        All courses are selected and user is trying to select all again.
        if(course.equals("All")){

            for(String x: selectedCourses){
                coursesRecyclerView.getLayoutManager().findViewByPosition(courseCodes.indexOf(x)).
                        findViewById(R.id.forums_course_filter_button).setBackgroundResource(R.drawable.course_filter_button_unselected);
            }

            selectedCourses.clear();
            selectedCourses.add("All");
        }

//        if user is selecting first course
        else if(selectedCourses.contains("All") && !course.equals("All")){
            /* if the selected course IS NOT "All"
            *  1- set "All" unselected
            *  2- remove "All" from selectedCourses
            *  3- add new Course to selectedCourses
            *  4- set new Course selected */

            coursesRecyclerView.getLayoutManager().findViewByPosition(viewPosition).
                    findViewById(R.id.forums_course_filter_button).setBackgroundResource(R.drawable.course_filter_button_selected);

            selectedCourses.clear();
            selectedCourses.add(course);
        }

//        if user is un-selecting the selected course
        else if(selectedCourses.contains(course)){
            /* we have 2 cases:
            *   ----- case 1 -----   user un-selecting only ONE selected course
            *       1: select ALL
            *       2: un-select el course
            *
            *   ----- case 2 -----    user is un-selecting a course and there are OTHER selected
            *       1: un-select the course  */

            if(selectedCourses.size() == 1){
                selectedCourses.add("All");
            }
            coursesRecyclerView.getLayoutManager().findViewByPosition(viewPosition).
                    findViewById(R.id.forums_course_filter_button).setBackgroundResource(R.drawable.course_filter_button_unselected);
            selectedCourses.remove(course);

        }

//        selecting an additional course
        else {
            coursesRecyclerView.getLayoutManager().findViewByPosition(courseCodes.indexOf(course)).
                    findViewById(R.id.forums_course_filter_button).setBackgroundResource(R.drawable.course_filter_button_selected);
            selectedCourses.add(course);
        }

        filterPosts();
    }
    public void filterPosts(){
        filterUserPosts();
        filterStarredPosts();
        filterAllPosts();
        setEmptyTextVisibility(Objects.requireNonNull(postsRecyclerView.getAdapter()).getItemCount()==0);
    }

    @Override
    public void onBackPressed() {
        if(sessionManager.getType().equals("student")){
            startActivity(new Intent(ForumsActivity.this, Home.class));
            finish();
        }else{
            startActivity(new Intent(ForumsActivity.this, TA_home.class));
            finish();
        }
    }
}