package com.example.attendance.Deadline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.attendance.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DoneListAdapter extends RecyclerView.Adapter<DoneListAdapter.ViewHolder>{
    private List<Deadline> deadlines;

    public DoneListAdapter(List<Deadline> deadlines) {
        this.deadlines = deadlines;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView assignmentName, courseCode, dueDate;

        public ViewHolder(View view){
            super(view);
            assignmentName = (TextView) view.findViewById(R.id.assignment_name);
            courseCode = (TextView) view.findViewById(R.id.course_code);
            dueDate = (TextView) view.findViewById(R.id.due_date);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.done_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DoneListAdapter.ViewHolder holder, int position) {
        Deadline deadline = deadlines.get(position);
        holder.assignmentName.setText(deadline.getAssignmentName());
        holder.courseCode.setText(deadline.getCourseCode());
        holder.dueDate.setText(deadline.getDueDate());
    }

    @Override
    public int getItemCount() {
        return deadlines.size();
    }
}
