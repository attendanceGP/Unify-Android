package com.example.attendance.Forums;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.Announcement.Announcement_Student_Activity;
import com.example.attendance.R;
import com.example.attendance.SessionManager;

import java.util.HashMap;
import java.util.List;

public class CoursesListAdapter extends RecyclerView.Adapter<CoursesListAdapter.ViewHolder> {

    private List<String> courses;
    private Context applicationContext;
    private LayoutInflater mInflater;
    private SessionManager sessionManager;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button courseCodeButton;
        public String courseCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseCodeButton =(Button) itemView.findViewById(R.id.forums_course_filter_button);
            courseCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("sss", "course clicked");

                    if (applicationContext instanceof ForumsActivity) {
                        ((ForumsActivity) applicationContext).onCourseFilterClick(getAdapterPosition(), courseCodeButton.getText().toString());
                    }
                        Log.i("sss", "course has position");
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

//    public HashMap<String, Integer> getAllPosition(){
//        HashMap<String,Integer> map = new HashMap<>();
//
//        for(String s: courses) {
//
//            map.put(s, i); // i is the position of adapter
//        }
//        return map;
//    }


}
