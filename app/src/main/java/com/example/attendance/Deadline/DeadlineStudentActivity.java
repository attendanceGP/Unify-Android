package com.example.attendance.Deadline;

import android.os.Bundle;

import com.example.attendance.R;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        Deadline deadline = new Deadline("assignment ", "CS402", "Due 27-7-2012");
        deadlineList.add(deadline);

        deadline = new Deadline("assignment ", "CS402", "Due 27-7-2012");
        deadlineList.add(deadline);

        deadline = new Deadline("assignment ", "CS402", "Due 27-7-2012");
        deadlineList.add(deadline);

        deadline = new Deadline("assignment ", "CS402", "Due 27-7-2012");
        deadlineList.add(deadline);

        deadline = new Deadline("assignment ", "CS402", "Due 27-7-2012");
        deadlineList.add(deadline);

        deadline = new Deadline("assignment ", "CS402", "Due 27-7-2012");
        deadlineList.add(deadline);

        deadline = new Deadline("assignment ", "CS402", "Due 27-7-2012");
        deadlineList.add(deadline);

        deadline = new Deadline("assignment ", "CS402", "Due 27-7-2012");
        deadlineList.add(deadline);

        deadline = new Deadline("assignment ", "CS402", "Due 27-7-2012");
        deadlineList.add(deadline);

        deadline = new Deadline("assignment ", "CS402", "Due 27-7-2012");
        deadlineList.add(deadline);

        upcomingListAdapter.notifyDataSetChanged();
    }
}
