package com.example.attendance.Absence;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.APIClient;
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
            }

            @Override
            public void onFailure(Call<TaRecent[]> call, Throwable t) {
                Toast.makeText(TAAbsenceTab.this, "couldn't retrieve the data", Toast.LENGTH_SHORT).show();
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