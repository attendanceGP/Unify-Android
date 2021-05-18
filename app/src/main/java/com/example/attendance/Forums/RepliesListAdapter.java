package com.example.attendance.Forums;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.APIClient;
import com.example.attendance.R;
import com.example.attendance.SessionManager;
import com.example.attendance.UserAPI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepliesListAdapter extends RecyclerView.Adapter<RepliesListAdapter.ViewHolder>{
    private List<Reply> replies;
    private Context applicationContext;
    private LayoutInflater mInflater;
    private SessionManager sessionManager;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView date;
        public TextView content;
        public Button delete;
        public Reply reply;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.username = itemView.findViewById(R.id.reply_user_name);
            this.date = itemView.findViewById(R.id.reply_date_time);
            this.content = itemView.findViewById(R.id.reply_content);
            this.delete = itemView.findViewById(R.id.delete_reply_button);

        }
    }

    public RepliesListAdapter(List<Reply> replies, Context applicationContext) {
        this.mInflater = LayoutInflater.from(applicationContext);
        this.replies = replies;
        this.applicationContext = applicationContext;
        this.sessionManager = new SessionManager(applicationContext);
    }

    public RepliesListAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.reply_recyclerview, parent, false);
        return new RepliesListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reply reply = replies.get(position);
        holder.username.setText(reply.getUserName());
        holder.date.setText(getDateStringFromDate(reply.getDate()));
        holder.content.setText(reply.getDescription());
        holder.reply = reply;

        Button deleteButton = (Button) holder.delete;

        if(reply.getUserId().equals(sessionManager.getId())){
            deleteButton.setVisibility(View.VISIBLE);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (applicationContext instanceof PostActivity) {
                    ((PostActivity) applicationContext).deleteReply(holder.reply.getId());
                    removeItem(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    // convenience method for getting data at click position
    Reply getItem(int id) {
        return replies.get(id);
    }

    public void removeItem(int position) {
        replies.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, replies.size());
        notifyDataSetChanged();
    }

    public void addItem(Reply newList) {
        replies.add(newList);
        notifyDataSetChanged();
    }

    private String getDateStringFromDate(Date date){
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
