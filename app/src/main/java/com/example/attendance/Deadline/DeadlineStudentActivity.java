package com.example.attendance.Deadline;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.attendance.Database.AppDatabase;
import com.example.attendance.R;
import com.example.attendance.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

public class DeadlineStudentActivity extends AppCompatActivity {
    private List<Deadline> upcomingList = new ArrayList<>();
    private List<Deadline> doneList = new ArrayList<>();

    private RecyclerView upcomingRecyclerView;
    private RecyclerView doneRecyclerView;

    private UpcomingListAdapter upcomingListAdapter;
    private DoneListAdapter doneListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadline_student);

        // binding to the recycler views
        upcomingRecyclerView = (RecyclerView) findViewById(R.id.upcoming_deadlines);
        doneRecyclerView = (RecyclerView) findViewById(R.id.done_deadlines) ;

        // instantiating new list adapters for upcoming and done
        upcomingListAdapter = new UpcomingListAdapter(upcomingList);
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
        prepareData();
    }

    void prepareData(){
        //TODO remove
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Insert Data
                upcomingList.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().deadlineDao().getAllUpcoming());
                Log.i("Donzel", "" + upcomingList.size());

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
}
