package com.example.attendance.Absence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.R;

public class TaRecentAdapter extends RecyclerView.Adapter<TaRecentAdapter.ViewHolder>{
    TaRecent[] mTaRecent;

    public TaRecentAdapter(TaRecent[] mTaRecent) {
        this.mTaRecent = mTaRecent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ta_recent_items,parent,false);
        TaRecentAdapter.ViewHolder viewHolder = new TaRecentAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaRecent tarecent = mTaRecent[position];
        TextView courseCode = holder.courseCodeText;
        TextView date = holder.dateText;
        TextView totalAttended = holder.totalAttendedText;

        courseCode.setText(tarecent.getCourseCode());
        date.setText("Recorded on "+tarecent.getDate());
        totalAttended.setText(Integer.toString(tarecent.getAttended())+" attended, "+ Integer.toString(tarecent.getAbsent())+"were absent");
    }

    @Override
    public int getItemCount() {
        return mTaRecent.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView courseCodeText,dateText,totalAttendedText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseCodeText = itemView.findViewById(R.id.ta_recent_course);
            dateText = itemView.findViewById(R.id.ta_recorded_on);
            totalAttendedText = itemView.findViewById(R.id.ta_num_of_attendance);

        }
    }

}
