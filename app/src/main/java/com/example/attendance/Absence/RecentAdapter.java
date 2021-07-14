package com.example.attendance.Absence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.R;


public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.ViewHolder>{
    Recent[] mrecent;

    public RecentAdapter(Recent[] mrecent) {
        this.mrecent = mrecent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recent_items,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Recent recent = mrecent[position];
    TextView courseCode = holder.courseCodeText;
    TextView date = holder.dateText;
    TextView ta = holder.TAText;
    TextView pen = holder.penText;
    courseCode.setText(recent.getCourseCode());
    date.setText("Recorded on " +recent.getDate());
    ta.setText("TA "+recent.getTaName());
    if (recent.isPen())pen.setVisibility(View.VISIBLE);
    else pen.setVisibility(View.INVISIBLE);
    }


    @Override
    public int getItemCount() {
        return mrecent.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView courseCodeText,TAText,dateText,penText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseCodeText = itemView.findViewById(R.id.recent_course);
            dateText = itemView.findViewById(R.id.recorded_on);
            TAText = itemView.findViewById(R.id.recent_ta);
            penText = itemView.findViewById(R.id.p);
        }
    }

}
