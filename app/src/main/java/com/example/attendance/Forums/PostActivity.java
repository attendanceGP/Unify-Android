package com.example.attendance.Forums;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.APIClient;
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.R;
import com.example.attendance.SessionManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        Log.i("TESTPOST", "2- " +post_id);
        getPostbyid();

        // binding to the swipe layout to refresh the page
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.post_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshForums();
            }
        });

        forumsAPI = APIClient.getClient().create(ForumsAPI.class);

//        setPost();

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
                        AppDatabase.class, "attendance").build().forumsDao().getAllReplies());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        repliesListAdapter.notifyDataSetChanged();
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
                System.out.println(t.getCause());
            }
        });
    }

    // syncs the retrieved deadlines with the ones that already exist in the Room db
    private void syncForumsFromAPI(List<Reply> _replies){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for(Reply reply: _replies) {
                    System.out.println(reply.getId());
                    // if exists
                    if (!Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "attendance").build().forumsDao().isExistsReply(reply.getId())) {
                        Room.databaseBuilder(getApplicationContext(),
                                AppDatabase.class, "attendance").build().forumsDao().insertAllReply(reply);
                        System.out.println("here");
                    }
                    updateData();
                }
            }
        });
    }

    public void deleteReply(Reply reply) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().forumsDao().deleteReply(reply);
                updateData();
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

                Log.i("TESTPOST", "3- " + post.getId());
                System.out.println("post title  " + post.getTitle());
            }
        });

    }
}