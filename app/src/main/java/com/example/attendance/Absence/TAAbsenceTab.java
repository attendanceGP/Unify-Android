package com.example.attendance.Absence;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.attendance.APIClient;
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.Home;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.TA_home;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TAAbsenceTab extends AppCompatActivity {
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_a_absence_tab);
        sessionManager = new SessionManager(getApplicationContext());
        RecyclerView rv = findViewById(R.id.rv_ta_recent);
        Call<TaRecent[]>getRecentTA = APIClient.getClient().create(AbsenceAPIs.class).getRecentTA(sessionManager.getId());
        getRecentTA.enqueue(new Callback<TaRecent[]>() {
            @Override
            public void onResponse(Call<TaRecent[]> call, Response<TaRecent[]> response) {
                TaRecent[] taRecents = response.body();
                TaRecentAdapter taRecentAdapter = new TaRecentAdapter(taRecents);
                rv.setAdapter(taRecentAdapter);
                rv.setLayoutManager(new LinearLayoutManager(TAAbsenceTab.this));

                TARecentRoom[] taRecentRooms = new TARecentRoom[taRecents.length];
                for (int i = 0; i <taRecentRooms.length ; i++) {
                    TaRecent taRecent = taRecents[i];
                    taRecentRooms[i] = new TARecentRoom(taRecent.getCourseCode(),taRecent.getDate(),taRecent.getAttended(),taRecent.getAbsent());
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
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        TARecentRoom[] taRecentRooms =Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"TARecent").build().absenceDAO().readAllTARecent();
                        TaRecent[]taRecents = new TaRecent[taRecentRooms.length];
                        for (int i = 0; i <taRecentRooms.length ; i++) {
                            TARecentRoom taRecentRoom = taRecentRooms[i];
                            taRecents[i] = new TaRecent(taRecentRoom.getCourseCode(),taRecentRoom.getDate(),taRecentRoom.getAttended(),taRecentRoom.getAbsent());
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TaRecentAdapter taRecentAdapter = new TaRecentAdapter(taRecents);
                                rv.setAdapter(taRecentAdapter);
                                rv.setLayoutManager(new LinearLayoutManager(TAAbsenceTab.this));
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
                            startActivity(new Intent(TAAbsenceTab.this, TA_home.class));
                        return true;

                    case R.id.action_announcements:
                        return true;

                    case R.id.action_forum:
                        return true;

                    case R.id.action_deadlines:
                        return true;
                }
                return false;
            }
        });
    }
}