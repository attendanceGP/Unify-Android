package com.example.attendance.Absence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.R;

public class AbsenceAdapter extends RecyclerView.Adapter<AbsenceAdapter.ViewHolder> {
    Absence[] mabsence;

    public AbsenceAdapter(Absence[] absence1){
        mabsence = absence1;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView code,abs,pen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.course_abse);
            abs = itemView.findViewById(R.id.counter_id);
            pen = itemView.findViewById(R.id.pen_id);
        }
    }

    @NonNull
    @Override
    public AbsenceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View absenceView = inflater.inflate(R.layout.absence_items,parent,false);
        ViewHolder viewHolder = new ViewHolder(absenceView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AbsenceAdapter.ViewHolder holder, int position) {
    Absence absence = mabsence[position];
    TextView codeText = holder.code;
    codeText.setText(absence.getCourseCode());
    TextView absText = holder.abs;
    absText.setText(Integer.toString(absence.getAbsenceCounter()));
    TextView penText = holder.pen;
    penText.setText(Integer.toString(absence.getPenCounter()) );
    }

    @Override
    public int getItemCount() {
        return mabsence.length;
    }
}
