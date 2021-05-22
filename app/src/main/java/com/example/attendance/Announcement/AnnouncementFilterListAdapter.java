package com.example.attendance.Announcement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.R;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementFilterListAdapter extends RecyclerView.Adapter<AnnouncementFilterListAdapter.ViewHolder> {
    private ArrayList<String> courseCodes;
    private Context context;

    public AnnouncementFilterListAdapter(ArrayList<String> courseCodes, Context context) {
        this.courseCodes = courseCodes;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public Button courseCodeFilterButton;

        public String courseCode;

        public ViewHolder(View view){
            super(view);
            courseCodeFilterButton = (Button) view.findViewById(R.id.course_filter_button);
            courseCodeFilterButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (context instanceof Announcement_Student_Activity) {
                        ((Announcement_Student_Activity) context).filter(courseCodeFilterButton.getText().toString());
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public AnnouncementFilterListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.announcement_filter_item, parent, false);
        AnnouncementFilterListAdapter.ViewHolder viewHolder = new AnnouncementFilterListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementFilterListAdapter.ViewHolder holder, int position) {
        String courseCode = courseCodes.get(position);
        holder.courseCodeFilterButton.setText(courseCode);
        holder.courseCode = courseCode;
    }

    @Override
    public int getItemCount() {
        return courseCodes.size();
    }
}
