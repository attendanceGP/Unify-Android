package com.example.attendance.Deadline;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.attendance.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeadlinesTAListAdapter extends RecyclerView.Adapter<DeadlinesTAListAdapter.ViewHolder>{
    private List<Deadline> deadlines;

    // to call the updateDate() function from the deadline student avtivity
    private Context context;

    public DeadlinesTAListAdapter(List<Deadline> deadlines, Context context) {
        this.deadlines = deadlines;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView assignmentName;
        public TextView courseCode;
        public TextView dueDate;

        public Deadline deadline;

        public ViewHolder(View view){
            super(view);
            assignmentName = (TextView) view.findViewById(R.id.assignment_name);
            courseCode = (TextView) view.findViewById(R.id.course_code);
            dueDate = (TextView) view.findViewById(R.id.due_date);

            // logic of the buttons in each row of the recycler view
            view.findViewById(R.id.edit_deadline_date).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // calender object with set to the deadline date and time
                    final Calendar currentDate = new GregorianCalendar();
                    currentDate.setTime(deadline.getDueDate());

                    // date picker to choose date
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            // time picker to choose time
                            TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                    // when both date and time are chosen, call the update function with the context
                                    if (context instanceof DeadlineTAActivity) {
                                        ((DeadlineTAActivity) context).updateDate(deadline.getId(), year, month+1, day, hour, minute);
                                    }
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
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.deadlines_you_posted_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DeadlinesTAListAdapter.ViewHolder holder, int position) {
        Deadline deadline = deadlines.get(position);
        holder.assignmentName.setText(deadline.getAssignmentName());
        holder.courseCode.setText(deadline.getCourseCode());
        holder.dueDate.setText(getDueDateStringFromDate(deadline.getDueDate()));
        holder.deadline = deadline;
    }

    @Override
    public int getItemCount() {
        return deadlines.size();
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
