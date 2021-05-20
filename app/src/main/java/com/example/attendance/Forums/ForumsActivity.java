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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.attendance.APIClient;
import com.example.attendance.Course;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumsActivity extends AppCompatActivity {
    private List<Post> posts = new ArrayList<>();
    private List<Post> userPosts = new ArrayList<>();
    private List<Post> favPosts = new ArrayList<>();
    private List<String> filterCourses = new ArrayList<>();

    ArrayList<String> courseCodes = new ArrayList<>();

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

    ForumsAPI forumsAPI;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums_home_page);
        sessionManager = new SessionManager(getApplicationContext());

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
                        }else{
                            startActivity(new Intent(ForumsActivity.this, TA_home.class));
                        }
                        return true;

                    case R.id.action_announcements:
                        return true;

                    case R.id.action_deadlines:
                        if(sessionManager.getType().equals("student")){
                            startActivity(new Intent(ForumsActivity.this, DeadlineStudentActivity.class));
                        }else{
                            startActivity(new Intent(ForumsActivity.this, DeadlineTAActivity.class));
                        }
                        return true;

                    case R.id.action_absence:
                        return true;
                }
                return false;
            }
        });
    }

    private void getUserCourses() {
        UserAPI userAPI = APIClient.getClient().create(UserAPI.class);
        Call<ArrayList<String>> call = userAPI.getTaughtCourses(sessionManager.getId());

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                if(response.code() != 200){
                    Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                }
                else{
                    courseCodes.add("All");
                    courseCodes.addAll(response.body());
                    coursesListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                System.out.println(t.getMessage());
            }
        });
    }


    private void updateData(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                posts.clear();
                posts.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().forumsDao().getAllPosts());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postsListAdapter.notifyDataSetChanged();
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
            }
        });
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


//    show user posts
    public void getUserPosts(){
        userPosts.clear();

        for(Post d: posts){
            if(d.getUserId().equals(sessionManager.getId())){
                userPosts.add(d);
            }
        }
        postsRecyclerView.setAdapter(userPostsListAdapter);
        userPostsListAdapter.notifyDataSetChanged();
    }

//    show all forums (with no filter)
    public void showForums(){
        postsRecyclerView.setAdapter(postsListAdapter);
        postsListAdapter.notifyDataSetChanged();
        updateData();
        refreshForums();
    }

//    show starred posts
    public void getStarredPosts(){
        favPosts.clear();
        for(Post d: posts){
            if(d.isStarred()){
                favPosts.add(d);
            }
        }
        postsRecyclerView.setAdapter(favPostsListAdapter);
        favPostsListAdapter.notifyDataSetChanged();
    }


//    open post activity
    public void onForumClick(int position, Post clickedPost) {
        Intent intent = new Intent(ForumsActivity.this, PostActivity.class);
        intent.putExtra("Post_id", clickedPost.getId());
        intent.putExtra("course_code", clickedPost.getCourseCode());
        startActivity(intent);
    }


//    delete user post from api and room
    public void deleteUserPost(Integer postId){
        Call<Void> call = forumsAPI.removePost(postId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getApplicationContext(), "removed", Toast.LENGTH_SHORT).show();
                deleteUserPostFromRoom(postId);
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

//    public void onCourseFilterClick(int position, String course) {
//        if (course.equals("All")) {
//            //resets all button colors to show that they have been unselected
//            for (int i = 0; i < courseCodes.size(); i++) {
//                coursesRecyclerView.getLayoutManager().findViewByPosition(i).
//                        findViewById(R.id.course_filter_button).setBackgroundResource(R.drawable.course_filter_button_unselected);
//            }
//
//            //here we reset the filter before getting all the announcements without any filters
//            filterCourses.clear();
//            updateData();
//            refreshForums();
//        } else if (filterCourses.contains(course)) {
//            //this is used for when a filter is applied and we want to remove it and apply the remaining filters in the filter array if any,
//            //if none exist we get all the announcements with no filters
//
//            coursesRecyclerView.getLayoutManager().findViewByPosition(courseCodes.indexOf(course)).
//                    findViewById(R.id.course_filter_button).setBackgroundResource(R.drawable.course_filter_button_unselected);
//
//            filterCourses.remove(course);
//
//            if (filterCourses.size() == 0) {
//                updateData();
//                refreshForums();
//            }
//            //
////                else {
////                    announcementList.clear();
////                    announcementList.addAll(unchangedAnnouncementList);
////
////                    for (int i = 0; i < announcementList.size(); i++) {
////                        if (!activeFilters.contains(announcementList.get(i).getCourseId())) {
////                            announcementList.remove(i);
////                        }
////                    }
////
////                    announcementsStudentListAdapter.notifyDataSetChanged();
////                }
////            }
//
////            else{
////                //setting the clicked button color to darker green to show that it is selected
////                coursesRecyclerView.getLayoutManager().findViewByPosition(courseCodes.indexOf(course)).
////                        findViewById(R.id.course_filter_button).setBackgroundResource(R.drawable.course_filter_button_selected);
////
////                activeFilters.add(courseId);
////
////                announcementList.clear();
////                announcementList.addAll(unchangedAnnouncementList);
////
////                for (int i = 0; i < announcementList.size(); i++) {
////                    if(!activeFilters.contains(announcementList.get(i).getCourseId())){
////                        announcementList.remove(i);
////                    }
////                }
////
////                announcementsStudentListAdapter.notifyDataSetChanged();
////            }
////
//        }

}