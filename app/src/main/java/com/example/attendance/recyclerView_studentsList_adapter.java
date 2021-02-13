package com.example.attendance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.crypto.KeyAgreement;

public class recyclerView_studentsList_adapter extends RecyclerView.Adapter<recyclerView_studentsList_adapter.ViewHolder> {
    private List<Attendance> attendeesData;
    private LayoutInflater mInflater;

    // ViewHolder used to store each row in our recycler view to be easily cashed and accessed
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        Button buttonAction;
        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvStudentDetails);
            buttonAction = itemView.findViewById(R.id.buttonRow);
        }
    }


    // data is passed into the constructor
    recyclerView_studentsList_adapter(Context context, List<Attendance> data) {
        this.mInflater = LayoutInflater.from(context);
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
        holder.myTextView.setText(attendance.getUserId()+'\n'+attendance.getName());
        Button button = holder.buttonAction;
        button.setText("Remove");
        button.setBackgroundColor(Color.RED);
//        button.setText(attendance.isAbsent() ? "Add" : "Remove");
        button.setEnabled(true);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(, "clicked", Toast.LENGTH_SHORT).show();
//            }
//
//        });
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


}