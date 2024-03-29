package com.example.attendance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class recyclerView_studentsList_adapter extends RecyclerView.Adapter<recyclerView_studentsList_adapter.ViewHolder> {
    private List<Attendance> attendeesData;
    private Context applicationContext;
    private LayoutInflater mInflater;
    private String date;


    // ViewHolder used to store each row in our recycler view to be easily cashed and accessed
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_TextView;
        TextView ID_TextView;
        Button buttonAction;
        ViewHolder(View itemView) {
            super(itemView);
            name_TextView = itemView.findViewById(R.id.tvStudentName);
            ID_TextView = itemView.findViewById(R.id.tvStudentID);
            buttonAction = itemView.findViewById(R.id.buttonRow);
        }
    }


    // data is passed into the constructor
    recyclerView_studentsList_adapter(Context context, List<Attendance> data, String date) {
        this.mInflater = LayoutInflater.from(context);
        this.applicationContext = context;
        this.date = date;
        this.attendeesData = data;
    }

    // creates the holder inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.studentslist_rv_row, parent, false);
        return new ViewHolder(view);
    }

    // to set the view attributes based on the data
    // binds the data to the TextView in each row
    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Attendance attendance = attendeesData.get(position);

        // Set item views based on your views and data model
        holder.ID_TextView.setText(String.valueOf(attendance.getUserId())+" - "+ attendance.getUserGroup());
        holder.name_TextView.setText(attendance.getName());
        Button button = holder.buttonAction;
        button.setEnabled(true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAPI userAPI = APIClient.getClient().create(UserAPI.class);

                Call<Attendance> call = userAPI.setAbsence(attendance.getCourseCode(), attendance.getUserGroup(), date, attendance.getUserId(), true, true);
                call.enqueue(new Callback<Attendance>() {
                    @Override
                    public void onResponse(Call<Attendance> call, Response<Attendance> response) {
                        Attendance test = response.body();
                        if (response.code() != 200) {
                            Toast.makeText(applicationContext, "an error occurred", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(applicationContext, test.getUserId()+" removed", Toast.LENGTH_SHORT).show();
                            removeItem(position);
                        }
                    }
                    @Override
                    public void onFailure(Call<Attendance> call, Throwable t) {
                        Toast.makeText(applicationContext, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                });
                }
            }

        );
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return attendeesData.size();
    }


    // convenience method for getting data at click position
    String getItem(int id) {
        return attendeesData.get(id).getName();
    }

    public void removeItem(int position) {
        attendeesData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, attendeesData.size());
        notifyDataSetChanged();
    }

    public void addItem(Attendance newList) {
        attendeesData.add(newList);
        notifyDataSetChanged();
    }

}