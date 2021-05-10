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
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.Deadline.DeadlineStudentActivity;
import com.example.attendance.Deadline.DeadlineTAActivity;
import com.example.attendance.Home;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.TA_home;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumsActivity extends AppCompatActivity {
    private List<Post> posts = new ArrayList<>();
    private RecyclerView postsRecyclerView;
    private PostsListAdapter postsListAdapter;
    private UserPostsListAdapter userPostsListAdapter;

    private Button viewFavourites;
    private ToggleButton viewMyForums;
    private Button addForum;
    private Spinner filterCourses;

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

//        Recycler View
        postsRecyclerView = (RecyclerView) findViewById(R.id.posts_recycler_view);
        postsListAdapter = new PostsListAdapter(this, posts);
        postsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        postsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        postsRecyclerView.setAdapter(postsListAdapter);

        updateData();
        refreshForums();

        // my Forums Button
        viewMyForums = (ToggleButton) findViewById(R.id.my_posts_button);
        viewMyForums.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getUserPosts();
                } else {
                    updateData();
                    refreshForums();
                    postsListAdapter.notifyDataSetChanged();
                }
            }
        });


        // my Favourites forums Button
        viewFavourites = (Button) findViewById(R.id.my_Favourite_button);
        viewFavourites.setOnClickListener(new View.OnClickListener(){
            int m_counts =0;
            @Override
            public void onClick(View view) {
                if (m_counts%2 == 0){
                    updateData();
                    refreshForums();
                }
                else{
                    getStarredPosts();
                }
            }
        });


        //  add forum button
        addForum = (Button) findViewById(R.id.add_new_forum);
        addForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForumsActivity.this, AddForumActivity.class);
                startActivity(intent);
            }
        });


        // Filter courses spinner
        filterCourses = (Spinner) findViewById(R.id.course_filter_spinner);


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
                    System.out.println(post.getId());
                    // if exists
                    if (!Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "attendance").build().forumsDao().isExistsPosts(post.getId())) {
                        Room.databaseBuilder(getApplicationContext(),
                                AppDatabase.class, "attendance").build().forumsDao().insertAllPosts(post);
                        System.out.println("here");
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

//    public void showStarred(){
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                Room.databaseBuilder(getApplicationContext(),
//                        AppDatabase.class, "attendance").build().forumsDao().getAllStarred();
//                updateData();
//            }
//        });
//    }
//

    public List<Post> getUserPosts(){
        List<Post> temp = new ArrayList();
        for(Post d: posts){
            if(d.getUserId().equals(this.sessionManager.getId())){
                temp.add(d);
            }
        }
        return temp;
    }

    public void showUserForums(){
        posts.clear();
        posts = getUserPosts();
        userPostsListAdapter = new UserPostsListAdapter(this, posts);
        postsRecyclerView.setAdapter(userPostsListAdapter);
    }

    public void showForums(){
        updateData();
        refreshForums();
        postsRecyclerView.setAdapter(postsListAdapter);
    }


    public void onForumClick(int position, Post clickedPost) {
        System.out.println("here forums activity");
        Intent intent = new Intent(ForumsActivity.this, PostActivity.class);
        intent.putExtra("Post_id", clickedPost.getId());
        System.out.println(clickedPost.getId());
        intent.putExtra("course_code", clickedPost.getCourseCode());
        System.out.println(clickedPost.getCourseCode());
        startActivity(intent);
    }


    public void getStarredPosts(){
        List<Post> temp = new ArrayList();
        for(Post d: posts){
            if(d.isStarred()){
                temp.add(d);
            }
        }
        //update recyclerview
        postsListAdapter.filterList(temp);
    }

    public void deleteUserPost(Integer postId){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().forumsDao().deletePostById(postId);
                updateData();
            }
        });
    }

}