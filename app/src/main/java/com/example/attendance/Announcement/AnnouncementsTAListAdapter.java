package com.example.attendance.Announcement;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.attendance.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;


public class AnnouncementsTAListAdapter extends RecyclerView.Adapter<AnnouncementsTAListAdapter.ViewHolder> {

    private List<Announcement> announcements;
    private Context context;

    public AnnouncementsTAListAdapter(List<Announcement> announcements, Context context) {
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
            title_tv = (TextView) view.findViewById(R.id.ta_announcement_title);
            group_tv = (TextView) view.findViewById(R.id.ta_announcement_groups);
            courseId_tv = (TextView) view.findViewById(R.id.ta_announcement_course_code);
            postedDate_tv = (TextView) view.findViewById(R.id.ta_posted_date);
            postedBy_tv = (TextView) view.findViewById(R.id.ta_posted_by);
            description_tv = (TextView) view.findViewById(R.id.ta_announcement_description);
            view.findViewById(R.id.delete_announcement_button).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (context instanceof Announcement_TA_Activity) {
                        ((Announcement_TA_Activity) context).deleteAnnouncement(announcement.getId());
                    }
                }
            });

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.announcements_you_posted_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementsTAListAdapter.ViewHolder holder, int position) {
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
