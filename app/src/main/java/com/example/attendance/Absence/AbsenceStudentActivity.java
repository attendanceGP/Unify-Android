package com.example.attendance.Absence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.attendance.API.APIClient;
import com.example.attendance.Announcement.Announcement_Student_Activity;
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.Deadline.DeadlineStudentActivity;
import com.example.attendance.Forums.ForumsActivity;
import com.example.attendance.Home.HomeStudentActivity;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.Home.HomeTAActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbsenceStudentActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_tab);
        sessionManager = new SessionManager(getApplicationContext());
        emptyView = (TextView) findViewById(R.id.empty_view_recent);

        RecyclerView rv = findViewById(R.id.rv_absence);
        RecyclerView rvRecent = findViewById(R.id.rv_recent);

        // get the data from room to the overview and recent and if there is an internet connection they will update instantly
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                AbsenceRoom[] absenceRooms = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "Absence").build().absenceDAO().readAllAbsence();

                Absence[] absences = new Absence[absenceRooms.length];

                RecentRoom[] recentRooms = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "Recent").build().absenceDAO().readAllRecent();

                Recent[] recents = new Recent[recentRooms.length];

                for (int i = 0; i < absenceRooms.length; i++) {
                    AbsenceRoom absenceRoom = absenceRooms[i];
                    absences[i] = new Absence(absenceRoom.getPen(),
                            absenceRoom.getAbsCounter(), absenceRoom.getCourseCode());
                }
                for (int i = 0; i < recentRooms.length; i++) {
                    RecentRoom recentRoom = recentRooms[i];
                    recents[i] = new Recent(recentRoom.getCourseCode(),
                            recentRoom.getTaName(), recentRoom.getDate(), recentRoom.isPen());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // add the data from room to the recyclerView
                        AbsenceAdapter absenceAdapter = new AbsenceAdapter(absences);

                        rv.setAdapter(absenceAdapter);
                        rv.setLayoutManager(new LinearLayoutManager(AbsenceStudentActivity.this));

                        RecentAdapter recentAdapter = new RecentAdapter(recents);

                        rvRecent.setAdapter(recentAdapter);
                        rvRecent.setLayoutManager(new LinearLayoutManager(AbsenceStudentActivity.this));

                        if (recents.length == 0) {
                            rvRecent.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        } else {

                            rvRecent.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }
                        //call the Api that gets the overview and overwrite on the recyclerView if on response
                        Call<Absence[]> getAbsence = APIClient.getClient().create(AbsenceAPIs.class)
                                .getAbsence(sessionManager.getId());

                        getAbsence.enqueue(new Callback<Absence[]>() {
                            @Override
                            public void onResponse(Call<Absence[]> call, Response<Absence[]> response) {

                                Absence[] absences = response.body();
                                AbsenceAdapter absenceAdapter = new AbsenceAdapter(absences);
                                rv.setAdapter(absenceAdapter);
                                rv.setLayoutManager(new LinearLayoutManager(AbsenceStudentActivity.this));

                                //update the room data base with
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        AbsenceRoom[] absenceRooms = new AbsenceRoom[absences.length];

                                        for (int i = 0; i < absences.length; i++) {

                                            Absence absence = absences[i];
                                            absenceRooms[i] = new AbsenceRoom(absence.getCourseCode(),
                                                    absence.getAbsenceCounter(), absence.getPenCounter());
                                        }
                                        Room.databaseBuilder(getApplicationContext(), AppDatabase.class,
                                                "Absence").build().absenceDAO().deleteAbsence();

                                        Room.databaseBuilder(getApplicationContext(), AppDatabase.class,
                                                "Absence").build().absenceDAO().insertAllToAbsence(absenceRooms);

                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<Absence[]> call, Throwable t) {

                            }
                        });
                        // call the api that gets the recent and overwrite on the recyclerView if on response
                        Call<Recent[]> getRecent = APIClient.getClient().create(AbsenceAPIs.class).getRecent(sessionManager.getId());
                        getRecent.enqueue(new Callback<Recent[]>() {
                            @Override
                            public void onResponse(Call<Recent[]> call, Response<Recent[]> response) {

                                Recent[] recents = response.body();
                                RecentAdapter recentAdapter = new RecentAdapter(recents);

                                rvRecent.setAdapter(recentAdapter);
                                rvRecent.setLayoutManager(new LinearLayoutManager(AbsenceStudentActivity.this));

                                if (recents.length == 0) {

                                    rvRecent.setVisibility(View.GONE);
                                    emptyView.setVisibility(View.VISIBLE);
                                } else {
                                    rvRecent.setVisibility(View.VISIBLE);
                                    emptyView.setVisibility(View.GONE);
                                }
                                RecentRoom[] recentRooms = new RecentRoom[recents.length];
                                for (int i = 0; i < recents.length; i++) {

                                    Recent recent = recents[i];

                                    recentRooms[i] = new RecentRoom(recent.getCourseCode(),
                                            recent.getTaName(), recent.getDate(), recent.isPen());
                                }

                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Room.databaseBuilder(getApplicationContext(), AppDatabase.class,
                                                "Recent").build().absenceDAO().deleteRecent();

                                        Room.databaseBuilder(getApplicationContext(), AppDatabase.class,
                                                "Recent").build().absenceDAO().insertAllToRecent(recentRooms);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<Recent[]> call, Throwable t) {

                            }
                        });


                    }
                });
            }
        });

        //code for the bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        startActivity(new Intent(AbsenceStudentActivity.this, HomeStudentActivity.class));
                        finish();

                        return true;

                    case R.id.action_announcements:
                        startActivity(new Intent(AbsenceStudentActivity.this, Announcement_Student_Activity.class));
                        finish();
                        return true;

                    case R.id.action_forum:
                        startActivity(new Intent(AbsenceStudentActivity.this, ForumsActivity.class));
                        finish();
                        return true;

                    case R.id.action_deadlines:
                        startActivity(new Intent(AbsenceStudentActivity.this, DeadlineStudentActivity.class));
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (sessionManager.getType().equals("student")) {
            startActivity(new Intent(AbsenceStudentActivity.this, HomeStudentActivity.class));
            finish();
        } else {
            startActivity(new Intent(AbsenceStudentActivity.this, HomeTAActivity.class));
            finish();
        }
    }
}