package com.example.attendance.Forums;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.R;
import com.example.attendance.SessionManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserPostsListAdapter extends RecyclerView.Adapter<UserPostsListAdapter.ViewHolder> {
    private List<Post> userPosts;
    private Context applicationContext;
    private LayoutInflater mInflater;
    private SessionManager sessionManager;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title;
        public TextView courseCode;
        public TextView date;
        public TextView publisherName;
        public TextView description;
        public Button deleteButton;
        public Post post;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.my_forums_list_title);
            courseCode = itemView.findViewById(R.id.my_forums_course_code);
            date = itemView.findViewById(R.id.my_forums_date);
            publisherName = itemView.findViewById(R.id.my_forums_user_name);
            description = itemView.findViewById(R.id.my_forums_description);
            deleteButton = itemView.findViewById(R.id.my_forum_delete_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                Post post = userPosts.get(position);
                ((ForumsActivity) applicationContext).onForumClick(position, post);
            }
        }
    }

    UserPostsListAdapter(){

    }

    UserPostsListAdapter(Context context, List<Post> posts){
        this.mInflater = LayoutInflater.from(context);
        this.applicationContext = context;
        this.userPosts = posts;
        this.sessionManager = new SessionManager(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.my_forums_rv_row, parent, false);
        return new UserPostsListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = userPosts.get(position);
        holder.title.setText(post.getTitle());
        holder.publisherName.setText(post.getUserName());
        holder.courseCode.setText(post.getCourseCode());
        holder.date.setText(getDateStringFromDate(post.getDate()));
        holder.description.setText(post.getContent());
        holder.post = post;
        Button deleteButton = (Button) holder.deleteButton;

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (applicationContext instanceof ForumsActivity) {
                    ((ForumsActivity) applicationContext).deleteUserPost(holder.post.getId());
                    removeItem(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    // convenience method for getting data at click position
    Post getItem(int id) {
        return userPosts.get(id);
    }

    public void removeItem(int position) {
        userPosts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, userPosts.size());
        notifyDataSetChanged();
    }

    public void addItem(Post newList) {
        userPosts.add(newList);
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
