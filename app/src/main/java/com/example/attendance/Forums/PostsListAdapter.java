package com.example.attendance.Forums;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendance.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PostsListAdapter extends RecyclerView.Adapter<PostsListAdapter.ViewHolder> {
    private List<Post> posts;
    private Context applicationContext;
    private LayoutInflater mInflater;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView courseCode;
        public TextView date;
        public TextView publisherName;
        public TextView description;
        public ToggleButton starred;
        public Post post;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.forum_title);
            courseCode = itemView.findViewById(R.id.course_code);
            date = itemView.findViewById(R.id.date);
            publisherName = itemView.findViewById(R.id.user_name);
            description = itemView.findViewById(R.id.description);
            starred = itemView.findViewById(R.id.favourite_toggleButton);
        }
    }


    PostsListAdapter(){}

    PostsListAdapter(Context context, List<Post> posts){
        this.mInflater = LayoutInflater.from(context);
        this.applicationContext = context;
        this.posts = posts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.forums_homepage_rv_row, parent, false);
        return new PostsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.title.setText(post.getTitle());
        holder.publisherName.setText(post.getUserName());
        holder.courseCode.setText(post.getCourseCode());
        holder.date.setText(getDateStringFromDate(post.getDate()));
        holder.description.setText(post.getContent());
        holder.post = post;
        ToggleButton starButton = (ToggleButton) holder.starred;


        if (holder.post.isStarred())
            starButton.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.forum_favourite_true));
        else
            starButton.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.forum_favourite_false));
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.post.isStarred()) {
                    starButton.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.forum_favourite_false));
                    if (applicationContext instanceof ForumsActivity) {
                        System.out.println("instance");
                        ((ForumsActivity) applicationContext).removeFromStarred(holder.post.getId());
                    }
                    System.out.println("unstarred");
                } else {
                    starButton.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.forum_favourite_true));
                    if (applicationContext instanceof ForumsActivity) {
                        ((ForumsActivity) applicationContext).addToStarred(holder.post.getId());
                        System.out.println("starred");
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return posts.get(id).getTitle();
    }

    public void removeItem(int position) {
        posts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, posts.size());
        notifyDataSetChanged();
    }

    public void addItem(Post newList) {
        posts.add(newList);
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
