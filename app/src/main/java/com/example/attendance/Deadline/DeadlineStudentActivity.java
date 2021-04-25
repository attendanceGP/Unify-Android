package com.example.attendance.Deadline;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.attendance.APIClient;
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.R;
import com.example.attendance.SessionManager;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeadlineStudentActivity extends AppCompatActivity {
    private List<Deadline> upcomingList = new ArrayList<>();
    private List<Deadline> doneList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView upcomingRecyclerView;
    private RecyclerView doneRecyclerView;

    private UpcomingListAdapter upcomingListAdapter;
    private DoneListAdapter doneListAdapter;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadline_student);

        sessionManager = new SessionManager(getApplicationContext());

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

        // binding to the recycler views
        upcomingRecyclerView = (RecyclerView) findViewById(R.id.upcoming_deadlines);
        doneRecyclerView = (RecyclerView) findViewById(R.id.done_deadlines) ;

        // instantiating new list adapters for upcoming and done
        upcomingListAdapter = new UpcomingListAdapter(upcomingList, this);  // sending context as well
        doneListAdapter = new DoneListAdapter(doneList);

        // setting the upcoming recycler view to the upcoming adapter
        upcomingRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        upcomingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        upcomingRecyclerView.setAdapter(upcomingListAdapter);

        // setting the done recycler view to the done adapter
        doneRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        doneRecyclerView.setItemAnimator(new DefaultItemAnimator());
        doneRecyclerView.setAdapter(doneListAdapter);

        // preparing data
        updateData();

        refreshDeadlines();
    }

    // refreshes the data from the Room db
    private void updateData(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
//                Room.databaseBuilder(getApplicationContext(),
//                        AppDatabase.class, "attendance").build().deadlineDao().nukeTable();

                upcomingList.clear();
                upcomingList.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().deadlineDao().getAllUpcoming());

                doneList.clear();
                doneList.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().deadlineDao().getAllDone());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upcomingListAdapter.notifyDataSetChanged();
                        doneListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    // refreshes deadlines from the internet
    private void refreshDeadlines(){
        DeadlineAPI deadlineAPI = APIClient.getClient().create(DeadlineAPI.class);

        Call<List<Deadline>> call = deadlineAPI.getDeadlines(sessionManager.getId());

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

                Log.d("test", "sss");

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void removeFromUpcoming(int deadlineId){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().deadlineDao().changeToDone(deadlineId);

                updateData();
            }
        });
    }
}
