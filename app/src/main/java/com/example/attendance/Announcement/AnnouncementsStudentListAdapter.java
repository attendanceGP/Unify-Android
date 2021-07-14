package com.example.attendance.Announcement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AnnouncementsStudentListAdapter extends RecyclerView.Adapter<AnnouncementsStudentListAdapter.ViewHolder>  {
    private List<Announcement> announcements;
    private Context context;

    public AnnouncementsStudentListAdapter(List<Announcement> announcements, Context context) {
        this.announcements = announcements;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title_tv;
        public TextView group_tv;
        public TextView courseId_tv;
        public TextView postedDate_tv;
        public TextView postedBy_tv;
        public TextView description_tv;

        public Announcement announcement;

        public ViewHolder(View view){
            super(view);
            title_tv = (TextView) view.findViewById(R.id.student_announcement_title);
            group_tv = (TextView) view.findViewById(R.id.student_announcement_groups);
            courseId_tv = (TextView) view.findViewById(R.id.student_announcement_course_code);
            postedDate_tv = (TextView) view.findViewById(R.id.student_posted_date);
            postedBy_tv = (TextView) view.findViewById(R.id.student_posted_by);
            description_tv = (TextView) view.findViewById(R.id.student_announcement_description);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.student_announcement_item, parent, false);
        AnnouncementsStudentListAdapter.ViewHolder viewHolder = new AnnouncementsStudentListAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementsStudentListAdapter.ViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);
        holder.title_tv.setText(announcement.getTitle());
        holder.group_tv.setText(announcement.getAnnouncementGroups());
        holder.courseId_tv.setText(announcement.getCourseId());
        holder.postedDate_tv.setText(getPostedDateStringFromDate(announcement.getPostedDate()));
        holder.postedBy_tv.setText(announcement.getPostedBy());
        holder.description_tv.setText(announcement.getDescription());

        holder.announcement = announcement;
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    /**
     *
     * String getPostedDateStringFromDate(Date date)
     *
     * Summary of the getPostedDateStringFromDate function:
     *
     *    gets the string form of the given date.
     *
     * Parameters   : date:the date of the announcement.

     * Return Value : the modified string containing the date in our wanted format.
     *
     * Description:
     *
     *    This function is used change the date into a string and then split it up to pieces that are used to
     *    form another string to have the format"posted dd-MM-YY at hh:mm a" which is used in the postedDate
     *    TextView.
     *
     */
    private String getPostedDateStringFromDate(Date date){
        String result = "";
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(date);

        result = "Posted " + strDate;

        dateFormat = new SimpleDateFormat("hh:mm a");
        strDate = dateFormat.format(date);

        result = result + " at " + strDate;
        return result;
    }

}
