package com.example.attendance.Deadline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
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

    private RecyclerView deadlineRecyclerView;

    private DeadlinesTAListAdapter deadlinesTAListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadline_ta);

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

//                Room.databaseBuilder(getApplicationContext(),
//                        AppDatabase.class, "attendance").build().deadlineDao().insertAll(new Deadline(1, "ass", "cs", new Date()));

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

    public void updateDate(int year, int month, int day, int hour, int minute){
        Log.d("datetest", "" + year + " " + month + " " + day + " " + hour + " " + minute);
        String newDueDate = "" + year + "-" + month + "-" + day + "T" + hour + ":" + minute;

        DeadlineAPI deadlineAPI = APIClient.getClient().create(DeadlineAPI.class);
        Call<Boolean> call = deadlineAPI.updateDueDate(newDueDate);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.code() == 200){
                    Toast.makeText(getApplicationContext(), "due date updated", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}