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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.API.APIClient;
import com.example.attendance.Absence.AbsenceStudentActivity;
import com.example.attendance.Absence.AbsenceTAActivity;
import com.example.attendance.Announcement.Announcement_Student_Activity;
import com.example.attendance.Announcement.Announcement_TA_Activity;
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.Deadline.DeadlineStudentActivity;
import com.example.attendance.Deadline.DeadlineTAActivity;
import com.example.attendance.Home.HomeStudentActivity;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.Home.HomeTAActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {
    private List<Reply> replies = new ArrayList<>();
    private RecyclerView repliesRecyclerView;
    private RepliesListAdapter repliesListAdapter;
    private TextView postTitle;
    private TextView postDate;
    private TextView postDescription;
    private TextView postUsername;
    private TextView postCourseCode;
    private TextView emptyText;
    private EditText replyArea;
    private Button replyButton;
    public Post post;

    private Button deleteReply;

    private SwipeRefreshLayout swipeRefreshLayout;

    ForumsAPI forumsAPI;
    SessionManager sessionManager;
    Intent intent;
    private Integer post_id;
    private String course_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity_layout);
        sessionManager = new SessionManager(getApplicationContext());
        this.intent = getIntent();
        this.post_id = intent.getIntExtra("Post_id", 0);
        this.course_code = intent.getStringExtra("course_code");
        this.emptyText = (TextView) findViewById(R.id.empty_replies_text);

        getPostbyid();

        // binding to the swipe layout to refresh the page
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.post_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshForums();
            }
        });

        forumsAPI = APIClient.getClient().create(ForumsAPI.class);


        repliesRecyclerView = (RecyclerView) findViewById(R.id.posts_recycler_view);
        repliesListAdapter = new RepliesListAdapter(replies, this);
        repliesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        repliesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        repliesRecyclerView.setAdapter(repliesListAdapter);

        updateData();
        refreshForums();

        replyArea = findViewById(R.id.reply_edit);
        replyButton = findViewById(R.id.reply_button);

        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replyContent = replyArea.getText().toString();
                if(replyContent.equals("")){
                    Toast.makeText(getApplicationContext(), "Reply is empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    Call<Void> call = forumsAPI.addReply(sessionManager.getId(), post_id,getDateStringForCurrentDate(),replyContent);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.code() != 200) {
                                Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "posted", Toast.LENGTH_SHORT).show();
                                replyArea.getText().clear();
                                refreshForums();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "check your connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home:
                        if(sessionManager.getType().equals("student")){
                            startActivity(new Intent(PostActivity.this, HomeStudentActivity.class));
                        }else{
                            startActivity(new Intent(PostActivity.this, HomeTAActivity.class));
                        }
                        return true;

                    case R.id.action_announcements:
                        if(sessionManager.getType().equals("student")){
                            startActivity(new Intent(PostActivity.this, Announcement_Student_Activity.class));
                        }else{
                            startActivity(new Intent(PostActivity.this, Announcement_TA_Activity.class));
                        }
                        return true;

                    case R.id.action_deadlines:
                        if(sessionManager.getType().equals("student")){
                            startActivity(new Intent(PostActivity.this, DeadlineStudentActivity.class));
                        }else{
                            startActivity(new Intent(PostActivity.this, DeadlineTAActivity.class));
                        }
                        return true;
                    case R.id.action_forum:
                        startActivity(new Intent(PostActivity.this, ForumsActivity.class));
                        return true;

                    case R.id.action_absence:
                        if(sessionManager.getType().equals("student")){
                            startActivity(new Intent(PostActivity.this, AbsenceStudentActivity.class));
                        }else{
                            startActivity(new Intent(PostActivity.this, AbsenceTAActivity.class));
                        }
                        return true;
                }
                return false;
            }
        });

    }

    private void setPost() {
        postTitle = findViewById(R.id.post_title);
        postTitle.setText(post.getTitle());

        postDate = findViewById(R.id.forum_post_date_time);
        postDate.setText(getDateStringFromDate(post.getDate()));

        postCourseCode = findViewById(R.id.course_code_post);
        postCourseCode.setText(post.getCourseCode());

        postUsername = findViewById(R.id.publisher_name);
        postUsername.setText(post.getUserName());

        postDescription = findViewById(R.id.forum_description);
        postDescription.setText(post.getContent());

    }

    private String getDateStringForCurrentDate(){

        java.util.Date date = new java.util.Date();
        String strDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        return strDate;
    }

    private String getDateStringFromDate(Date date){
        String result = "";
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(date);

        result = "Posted " + strDate;

        dateFormat = new SimpleDateFormat("hh:mm a");
        strDate = dateFormat.format(date);

        result = result + " at " + strDate;
        return result;
    }

    private void updateData(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                replies.clear();
                replies.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().forumsDao().getAllRepliesForPost(post_id));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        repliesListAdapter.notifyDataSetChanged();
                        setEmptyTextVisibility(replies.size()==0);
                    }
                });
            }
        });
    }

    public void refreshForums(){
        Call<List<Reply>> getReplies_Call = forumsAPI.getReplies(post_id);
        getReplies_Call.enqueue(new Callback<List<Reply>>() {
            @Override
            public void onResponse(Call<List<Reply>> call, Response<List<Reply>> response) {
                List<Reply> replyListAPI = response.body();
                if(response.code() != 200){
                    Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                }

                syncForumsFromAPI(replyListAPI);
            }
            @Override
            public void onFailure(Call<List<Reply>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
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

    // syncs the retrieved deadlines with the ones that already exist in the Room db
    private void syncForumsFromAPI(List<Reply> _replies){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().forumsDao().deleteRepliesByPostId(post_id);

                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance")
                        .build().forumsDao().insertAllReply(_replies.toArray(new Reply[_replies.size()]));
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

    public void deleteReply(Integer replyId){
        Call<Void> call = forumsAPI.removeReply(replyId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getApplicationContext(), "removed", Toast.LENGTH_SHORT).show();
                deleteReplyFromRoom(replyId);
                setEmptyTextVisibility(replies.size()==0);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void deleteReplyFromRoom(Integer replyId) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().forumsDao().deleteReplyById(replyId);
                updateData();
                refreshForums();
            }
        });
    }


    public void getPostbyid(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
               post = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().forumsDao().loadPostById(post_id);

               setPost();
            }
        });

    }

    public void setEmptyTextVisibility(boolean emptyList){
        if(emptyList){
            repliesRecyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else{
            repliesRecyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }

}