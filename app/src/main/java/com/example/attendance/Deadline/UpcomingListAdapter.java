package com.example.attendance.Deadline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.attendance.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UpcomingListAdapter extends RecyclerView.Adapter<UpcomingListAdapter.ViewHolder>{
    private List<Deadline> deadlines;

    // to call the removeFromUpcoming() function from the deadline student avtivity
    private Context context;

    public UpcomingListAdapter(List<Deadline> deadlines, Context context) {
        this.deadlines = deadlines;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView assignmentName;
        public TextView courseCode;
        public TextView dueDate;
        public TextView remainingTime;

        public Deadline deadline;

        public ViewHolder(View view){
            super(view);
            assignmentName = (TextView) view.findViewById(R.id.assignment_name);
            courseCode = (TextView) view.findViewById(R.id.course_code);
            dueDate = (TextView) view.findViewById(R.id.due_date);
            remainingTime = (TextView) view.findViewById(R.id.time_remaining);

            // logic off the buttons in each row of the recycler view
            view.findViewById(R.id.deadline_done).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // used the DeadlineStudentActivity context to calll the function from it
                    if (context instanceof DeadlineStudentActivity) {
                        ((DeadlineStudentActivity) context).removeFromUpcoming(deadline.getId());
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.upcoming_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingListAdapter.ViewHolder holder, int position) {
        Deadline deadline = deadlines.get(position);
        holder.assignmentName.setText(deadline.getAssignmentName());
        holder.courseCode.setText(deadline.getCourseCode());
        holder.dueDate.setText(getDueDateStringFromDate(deadline.getDueDate()));
        holder.remainingTime.setText(getRemainingTime(deadline.getDueDate()));
        holder.deadline = deadline;
    }

    @Override
    public int getItemCount() {
        return deadlines.size();
    }

    private String getRemainingTime(Date dueDate){
        String remainingTimeString;

        Date currentDate = new Date();
        long diff = dueDate.getTime() - currentDate.getTime();

        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = (int) (diff / (24 * 60 * 60 * 1000));

        if(diffMinutes < 0){
            remainingTimeString = "0";
        }else if(diffDays > 0){
            remainingTimeString = "" + diffDays + "D";
        }else if(diffHours == 0){
            remainingTimeString = "" + diffMinutes + "M";
        }else{
            remainingTimeString = "" + diffHours + "H";
        }

        return remainingTimeString;
    }

    private String getDueDateStringFromDate(Date date){
        String result = "";
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(date);

        result = "Due on " + strDate;

        dateFormat = new SimpleDateFormat("hh:mm a");
        strDate = dateFormat.format(date);

        result = result + " at " + strDate;
        return result;
    }
}
