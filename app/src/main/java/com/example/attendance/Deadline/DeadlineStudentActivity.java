package com.example.attendance.Deadline;

import android.os.AsyncTask;
import android.os.Bundle;

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
    private List<Deadline> deadlineList = new ArrayList<>();
    private RecyclerView upcomingRecyclerView;
    private RecyclerView doneRecyclerView;
    private UpcomingListAdapter upcomingListAdapter;
    private DoneListAdapter doneListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadline_student);

        upcomingRecyclerView = (RecyclerView) findViewById(R.id.upcoming_deadlines);
        doneRecyclerView = (RecyclerView) findViewById(R.id.done_deadlines) ;

        upcomingListAdapter = new UpcomingListAdapter(deadlineList);
        doneListAdapter = new DoneListAdapter(deadlineList);

        upcomingRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        upcomingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        upcomingRecyclerView.setAdapter(upcomingListAdapter);


        doneRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        doneRecyclerView.setItemAnimator(new DefaultItemAnimator());
        doneRecyclerView.setAdapter(doneListAdapter);

        prepareData();
    }

    void prepareData(){
//        Deadline deadline = new Deadline(1, "assignment ", "CS402", new Date(), false);
//        deadlineList.add(deadline);

        //TODO remove
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Insert Data
                deadlineList.add(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().deadlineDao().findById(1));

                upcomingListAdapter.notifyDataSetChanged();
            }
        });
    }
}
