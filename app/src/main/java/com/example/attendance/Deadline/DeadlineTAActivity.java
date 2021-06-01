package com.example.attendance.Deadline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.attendance.APIClient;
import com.example.attendance.Absence.TAAbsenceTab;
import com.example.attendance.Announcement.Announcement_Student_Activity;
import com.example.attendance.Announcement.Announcement_TA_Activity;
import com.example.attendance.Database.AppDatabase;
import com.example.attendance.Forums.ForumsActivity;
import com.example.attendance.Home;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.TA_home;
import com.example.attendance.UserAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DeadlineTAActivity extends AppCompatActivity {
    List<Deadline> deadlinesList = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView deadlineRecyclerView;

    private TextView emptyView;

    private DeadlinesTAListAdapter deadlinesTAListAdapter;

    private Button addButton;

    private Context context;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadline_ta);

        context = this;

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

        // binding to the recycler view
        deadlineRecyclerView = (RecyclerView) findViewById(R.id.deadlines_you_posted);

        // binding the text that displays when rv is empty to its view
        emptyView = (TextView) findViewById(R.id.empty_view);

        // instantiating new list adapter for deadlines
        deadlinesTAListAdapter = new DeadlinesTAListAdapter(deadlinesList, context); // sending context as well

        deadlineRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        deadlineRecyclerView.setItemAnimator(new DefaultItemAnimator());
        deadlineRecyclerView.setAdapter(deadlinesTAListAdapter);

        // binding to the add button
        addButton = (Button) findViewById(R.id.add_button);


        // inflating the popup when clicked
        addButton.setOnClickListener(new View.OnClickListener() {
            // to hold the due date
            private Date date = null;

            @Override
            public void onClick(View view) {
                final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.add_deadline_popup, null);

                // to make the popup rounded
                dialogBuilder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // to add animation
                dialogBuilder.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                final EditText assignmentName = (EditText) dialogView.findViewById(R.id.deadline_name);
                TextView dueDate = (TextView) dialogView.findViewById(R.id.deadline_date);

                // date part --------------------------------------------------------------
                dueDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // calender object with set to the deadline date and time
                        final Calendar currentDate = Calendar.getInstance();;

                        // date picker to choose date
                        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                // time picker to choose time
                                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                        String dateString = getDateString(year, month+1, day, hour, minute);

                                        try {
                                            date = new SimpleDateFormat("dd-MM-yyyy HH:mm").parse(dateString);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                                        dueDate.setText(dateFormat.format(date));
                                    }
                                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
                                timePickerDialog.show();
                            }
                        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));

                        // set min date so that you can only extend an assignment's deadline
                        datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis() + 1000 * 60 * 60 * 24);

                        datePickerDialog.show();
                    }
                });
                //----------------------------------------------------------------------------------

                // spinner part --------------------------------------------------------------------
                Spinner selectCourse = (Spinner) dialogView.findViewById(R.id.deadline_course);

                ArrayAdapter<String> adapter;
                ArrayList<String> givenCourses = new ArrayList<String>();

                UserAPI userAPI = APIClient.getClient().create(UserAPI.class);

                Call<ArrayList<String>> call = userAPI.getTaughtCourses(sessionManager.getId());
                call.enqueue(new Callback<ArrayList<String>>() {
                    @Override
                    public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                        if (response.code() != 200) {
                            Toast.makeText(getApplicationContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            for(int i=0; i<response.body().size(); i++){
                                String courseId = response.body().get(i);
                                givenCourses.add(courseId);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "please check your connection", Toast.LENGTH_SHORT).show();
                    }

                });

                givenCourses.add("None");

                // use default spinner item to show options in spinner
                adapter = new ArrayAdapter<>(context, R.layout.course_spinner_item, givenCourses);
                selectCourse.setAdapter(adapter);
                adapter.setDropDownViewResource(R.layout.course_dropdown_item);
                selectCourse.setPrompt("Courses");
                selectCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String text = parent.getItemAtPosition(position).toString();
//                        Toast.makeText(parent.getContext(), "selected: " + text, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                //----------------------------------------------------------------------------------

                // add button
                Button addDeadline = (Button) dialogView.findViewById(R.id.add_deadline);

                // when clicked, check that all fields are valid and then send to the api
                addDeadline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String courseCode =selectCourse.getSelectedItem().toString();
                        String name = assignmentName.getText().toString();

                        if(name.equals("")){
                            Toast.makeText(context, "assignment name cannot be empty", Toast.LENGTH_SHORT).show();
                        }else if(courseCode.equals("None")){
                            Toast.makeText(context, "please select a course", Toast.LENGTH_SHORT).show();
                        }else if(date == null){
                            Toast.makeText(getApplicationContext(), "please select a date", Toast.LENGTH_SHORT).show();
                        }else{
                            postDeadline(sessionManager.getId(), name, courseCode, new Date(), date);
                            dialogBuilder.dismiss();
                        }
                    }
                });

                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
            }
        });

        updateData();

        refreshDeadlines();

        /**
         * code for the bottom navigation bar
         */
        //TODO add the rest of the activities to the bottom navv view when done
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_home:
                        if(sessionManager.getType().equals("student")){
                            startActivity(new Intent(DeadlineTAActivity.this, Home.class));
                        }else{
                            startActivity(new Intent(DeadlineTAActivity.this, TA_home.class));
                        }
                        return true;

                    case R.id.action_announcements:
                        if(sessionManager.getType().equals("student")){
                            startActivity(new Intent(DeadlineTAActivity.this, Announcement_Student_Activity.class));
                        }else{
                            startActivity(new Intent(DeadlineTAActivity.this, Announcement_TA_Activity.class));
                        }
                        return true;

                    case R.id.action_forum:
                        startActivity(new Intent(DeadlineTAActivity.this, ForumsActivity.class));
                        return true;

                    case R.id.action_absence:
                        startActivity(new Intent(DeadlineTAActivity.this, TAAbsenceTab.class));
                        return true;
                }
                return false;
            }
        });
    }

    private void postDeadline(int userId, String name, String courseCode, Date postedDate, Date dueDate) {
        DeadlineAPI deadlineAPI = APIClient.getClient().create(DeadlineAPI.class);

        Call<Integer> call = deadlineAPI.postDeadline(userId, name, courseCode,
                new SimpleDateFormat("yyyy-MM-dd").format(postedDate),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dueDate));

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Integer integer = response.body();

                if(response.code() != 200){
                    Toast.makeText(getApplicationContext(), "deadline added", Toast.LENGTH_SHORT).show();
                }

                refreshDeadlines();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getDateString(int year, int month, int day, int hour, int minute) {
        String date;

        date = "" + day + "-" + month + "-" + year + " " + hour + ":" + minute;

        return date;
    }

    // refreshes the data from the Room db
    private void updateData(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
//                Room.databaseBuilder(getApplicationContext(),
//                        AppDatabase.class, "attendance").build().deadlineDao().nukeTable();

                deadlinesList.clear();
                deadlinesList.addAll(Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "attendance").build().deadlineDao().getAll());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deadlinesTAListAdapter.notifyDataSetChanged();

                        if(deadlinesList.isEmpty()){
                            deadlineRecyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        }else{
                            deadlineRecyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }
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

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void updateDate(int deadlineId, int year, int month, int day, int hour, int minute){
        Log.d("datetest", "" + year + " " + month + " " + day + " " + hour + " " + minute);
        String newDueDate = "" + year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00";

        DeadlineAPI deadlineAPI = APIClient.getClient().create(DeadlineAPI.class);
        Call<Integer> call = deadlineAPI.updateDueDate(deadlineId, newDueDate);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.code() == 200){
                    Toast.makeText(getApplicationContext(), "due date updated", Toast.LENGTH_SHORT).show();
                    refreshDeadlines();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}