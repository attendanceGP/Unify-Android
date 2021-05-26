package com.example.attendance.Forums;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.Course;
import com.example.attendance.R;
import com.example.attendance.SessionManager;

import java.util.List;

public class CoursesListAdapter extends RecyclerView.Adapter<CoursesListAdapter.ViewHolder> {

    private List<String> courses;
    private List<String> selectedCourses;
    private Context applicationContext;
    private LayoutInflater mInflater;
    private SessionManager sessionManager;

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button courseCodeButton;
        String courseCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseCodeButton = itemView.findViewById(R.id.course_filter_button);
            courseCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    System.out.println(" course clicked ");
                    Log.i("sss", "course clicked");
                    if(position != RecyclerView.NO_POSITION) {
                        String course = courses.get(position);
                        ((ForumsActivity) applicationContext).onCourseFilterClick(position, course);
                        System.out.println(" course has position ");
                        Log.i("sss", "course has position");
                    }
                }
            });
        }
    }

    CoursesListAdapter(){}
    CoursesListAdapter(Context context, List<String>courseCodes){
        this.mInflater = LayoutInflater.from(context);
        this.applicationContext = context;
        this.courses = courseCodes;
        this.sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.course_filter_item, parent, false);
        return new CoursesListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String courseCode = courses.get(position);
        holder.courseCodeButton.setText(courseCode);
        holder.courseCode = courseCode;
        Button courseButt = holder.courseCodeButton;

        if(courseCode.equals("All")){
            courseButt.setBackgroundResource(R.drawable.course_filter_button_selected);
        }

    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

}
