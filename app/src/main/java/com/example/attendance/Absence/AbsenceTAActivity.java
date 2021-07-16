package com.example.attendance.Absence;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.attendance.API.APIClient;
import com.example.attendance.Announcement.Announcement_TA_Activity;
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.Deadline.DeadlineTAActivity;
import com.example.attendance.Forums.ForumsActivity;
import com.example.attendance.Home.HomeStudentActivity;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.Home.HomeTAActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbsenceTAActivity extends AppCompatActivity {
    SessionManager sessionManager;
    private TextView emptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_a_absence_tab);
        sessionManager = new SessionManager(getApplicationContext());
        emptyView = (TextView)findViewById(R.id.empty_view_ta_recent);
        RecyclerView rv = findViewById(R.id.rv_ta_recent);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // get the Recent from room database and add them to taRecents
                TARecentRoom[] taRecentRooms = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"TARecent").build().absenceDAO().readAllTARecent();
                TaRecent[]taRecents = new TaRecent[taRecentRooms.length];
                for (int i = 0; i <taRecentRooms.length ; i++) {
                    TARecentRoom taRecentRoom = taRecentRooms[i];
                    taRecents[i] = new TaRecent(taRecentRoom.getCourseCode(),taRecentRoom.getDate(),taRecentRoom.getAttended(),taRecentRoom.getAbsent(),taRecentRoom.getGroupNumber());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //add the data from room to the recyclerView
                        TaRecentAdapter taRecentAdapter = new TaRecentAdapter(taRecents);
                        rv.setAdapter(taRecentAdapter);
                        rv.setLayoutManager(new LinearLayoutManager(AbsenceTAActivity.this));
                        if(taRecents.length ==0){
                            rv.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        }else{
                            rv.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }
                        // call the api to get the new Recent and it will overwrite on the recylcerView if on response
                        Call<TaRecent[]>getRecentTA = APIClient.getClient().create(AbsenceAPIs.class).getRecentTA(sessionManager.getId());
                        getRecentTA.enqueue(new Callback<TaRecent[]>() {
                            @Override
                            public void onResponse(Call<TaRecent[]> call, Response<TaRecent[]> response) {
                                TaRecent[] taRecents = response.body();
                                TaRecentAdapter taRecentAdapter = new TaRecentAdapter(taRecents);
                                rv.setAdapter(taRecentAdapter);
                                rv.setLayoutManager(new LinearLayoutManager(AbsenceTAActivity.this));

                                if(taRecents.length ==0){
                                    rv.setVisibility(View.GONE);
                                    emptyView.setVisibility(View.VISIBLE);
                                }else{
                                    rv.setVisibility(View.VISIBLE);
                                    emptyView.setVisibility(View.GONE);
                                }

                                TARecentRoom[] taRecentRooms = new TARecentRoom[taRecents.length];
                                for (int i = 0; i <taRecentRooms.length ; i++) {
                                    TaRecent taRecent = taRecents[i];
                                    taRecentRooms[i] = new TARecentRoom(taRecent.getCourseCode(),taRecent.getDate(),taRecent.getAttended(),taRecent.getAbsent(),taRecent.getGroupNumber());
                                }
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"TARecent").build().absenceDAO().deleteTARecent();
                                        Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"TARecent").build().absenceDAO().insertAllToTARecent(taRecentRooms);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<TaRecent[]> call, Throwable t) {

                            }
                        });

                    }
                });
            }
        });

        //for the bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home:
                            startActivity(new Intent(AbsenceTAActivity.this, HomeTAActivity.class));
                        finish();
                        return true;

                    case R.id.action_announcements:
                        startActivity(new Intent(AbsenceTAActivity.this, Announcement_TA_Activity.class));
                        finish();
                        return true;

                    case R.id.action_forum:
                        startActivity(new Intent(AbsenceTAActivity.this, ForumsActivity.class));
                        finish();
                        return true;

                    case R.id.action_deadlines:
                        startActivity(new Intent(AbsenceTAActivity.this, DeadlineTAActivity.class));
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(sessionManager.getType().equals("student")){
            startActivity(new Intent(AbsenceTAActivity.this, HomeStudentActivity.class));
        }else{
            startActivity(new Intent(AbsenceTAActivity.this, HomeTAActivity.class));
        }
        finish();
    }
}