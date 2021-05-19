package com.example.attendance.Absence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.attendance.APIClient;
import com.example.attendance.Home;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.TA_home;
import com.example.attendance.UserAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbsenceTab extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_tab);
        sessionManager = new SessionManager(getApplicationContext());
        RecyclerView rv =findViewById(R.id.rv_absence);
        RecyclerView rvRecent= findViewById(R.id.rv_recent);
        Call<Absence[]>getAbsence = APIClient.getClient().create(AbsenceAPIs.class).getAbsence(sessionManager.getId());
        getAbsence.enqueue(new Callback<Absence[]>() {
            @Override
            public void onResponse(Call<Absence[]> call, Response<Absence[]> response) {
                Absence[] absences=response.body();
                AbsenceAdapter absenceAdapter = new AbsenceAdapter(absences);
                rv.setAdapter(absenceAdapter);
                rv.setLayoutManager(new LinearLayoutManager(AbsenceTab.this));
            }

            @Override
            public void onFailure(Call<Absence[]> call, Throwable t) {
                Toast.makeText(AbsenceTab.this, "Couldn't retrieve the absences", Toast.LENGTH_SHORT).show();
            }
        });
        Call<Recent[]>getRecent=APIClient.getClient().create(AbsenceAPIs.class).getRecent(sessionManager.getId());
        getRecent.enqueue(new Callback<Recent[]>() {
            @Override
            public void onResponse(Call<Recent[]> call, Response<Recent[]> response) {
            Recent[] recents = response.body();
                RecentAdapter recentAdapter = new RecentAdapter(recents);
                rvRecent.setAdapter(recentAdapter);
                rvRecent.setLayoutManager(new LinearLayoutManager(AbsenceTab.this));
            }

            @Override
            public void onFailure(Call<Recent[]> call, Throwable t) {
                Toast.makeText(AbsenceTab.this, "Couldn't retrieve the data", Toast.LENGTH_SHORT).show();
                Log.d("the message is ", t.getMessage());
            }
        });

        //code for the bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home:
                            startActivity(new Intent(AbsenceTab.this, Home.class));

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