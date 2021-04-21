package com.example.attendance.Deadline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.attendance.APIClient;
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeadlineTAActivity extends AppCompatActivity {
    List<Deadline> deadlinesList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView deadlineRecyclerView;

    private DeadlinesTAListAdapter deadlinesTAListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadline_ta);

        // binding to the swipe layout to refesh the page
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);

        // when pull down, refresh deadlines
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("test", "sss");
                refreshDeadlines();
            }
        });

        // binding to the recycler view
        deadlineRecyclerView = (RecyclerView) findViewById(R.id.deadlines_you_posted);

        // instantiating new list adapter for deadlines
        deadlinesTAListAdapter = new DeadlinesTAListAdapter(deadlinesList, this); // sending context as well

        deadlineRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        deadlineRecyclerView.setItemAnimator(new DefaultItemAnimator());
        deadlineRecyclerView.setAdapter(deadlinesTAListAdapter);

        updateData();
    }

    // refreshes the data from the Room db
    private void updateData(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
//                Room.databaseBuilder(getApplicationContext(),
//                        AppDatabase.class, "attendance").build().deadlineDao().nukeTable();

                deadlinesList.clear();
                deadlinesList.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().deadlineDao().getAll());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deadlinesTAListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    // refreshes deadlines from the internet
    private void refreshDeadlines(){
        DeadlineAPI deadlineAPI = APIClient.getClient().create(DeadlineAPI.class);

        //TODO make the user id the logged in user's id
        Call<List<Deadline>> call = deadlineAPI.getDeadlines(20170171);

        call.enqueue(new Callback<List<Deadline>>() {
            @Override
            public void onResponse(Call<List<Deadline>> call, Response<List<Deadline>> response) {
                List<Deadline> deadlineList = response.body();

                if(response.code() != 200){
                    Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                }

                syncDeadlinesFromAPI(deadlineList);
            }

            @Override
            public void onFailure(Call<List<Deadline>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    // syncs the retrieved deadlines with the ones that already exist in the Room db
    private void syncDeadlinesFromAPI(List<Deadline> deadlines){
        // if the deadline already exists in room db, update date only
        // if not, add it to Room and update the lists

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for(Deadline deadline: deadlines){
                    // if exists
                    if(Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "attendance").build().deadlineDao().isExists(deadline.getId())){

                        Room.databaseBuilder(getApplicationContext(),
                                AppDatabase.class, "attendance").build().deadlineDao().updateDate(deadline.getId(), deadline.getDueDate());
                    }else{
                        Room.databaseBuilder(getApplicationContext(),
                                AppDatabase.class, "attendance").build().deadlineDao().insertAll(deadline);
                    }
                }

                updateData();

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void updateDate(int deadlineId, int year, int month, int day, int hour, int minute){
        Log.d("datetest", "" + year + " " + month + " " + day + " " + hour + " " + minute);
        String newDueDate = "" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00";

        DeadlineAPI deadlineAPI = APIClient.getClient().create(DeadlineAPI.class);
        Call<Integer> call = deadlineAPI.updateDueDate(deadlineId, newDueDate);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.code() == 200){
                    Toast.makeText(getApplicationContext(), "due date updated", Toast.LENGTH_SHORT).show();
                    refreshDeadlines();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}