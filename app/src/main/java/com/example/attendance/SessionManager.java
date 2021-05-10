package com.example.attendance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private Editor editor;
    private Context context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "SessionPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_GPA = "gpa";
    private static final String KEY_LEVEL = "level";

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

    // saves the user info in shared preferences
    public void login(User user){
        editor.putString(KEY_NAME, user.getName());
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_TYPE, user.getType());
        editor.putString(KEY_TOKEN, user.getToken());
        if(user.getType().equals("student")) {
            editor.putInt(KEY_LEVEL, user.getLevel());
            editor.putFloat(KEY_GPA, user.getGpa());
        }
        editor.putBoolean(IS_LOGIN, true);

        editor.commit();

        subscribeToTopics();
    }

    // subscribe to topics: id, courses and all
    private void subscribeToTopics(){
        UserAPI userAPI = APIClient.getClient().create(UserAPI.class);

        Call<ArrayList<String>> call = userAPI.getTaughtCourses(getId());

        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(Call<ArrayList<String >> call, Response<ArrayList<String>> response) {
                for(int i =0; i<response.body().size(); i++){
                    FirebaseMessaging.getInstance().subscribeToTopic(response.body().get(i)) ;
                }

                FirebaseMessaging.getInstance().subscribeToTopic("all");
                FirebaseMessaging.getInstance().subscribeToTopic(getId().toString());
                Log.i("usertest", getId().toString());
            }

            @Override
            public void onFailure(Call<ArrayList<String >> call, Throwable t) {

            }
        });
    }

    public String getName(){
        return sharedPreferences.getString(KEY_NAME, null);
    }

    public String getToken(){
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public String getType(){
        return sharedPreferences.getString(KEY_TYPE, null);
    }

    public Integer getId(){
        return sharedPreferences.getInt(KEY_ID, 0);
    }

    public Integer getLevel(){
        return sharedPreferences.getInt(KEY_LEVEL, 0);
    }

    public Float getGPA(){
        return sharedPreferences.getFloat(KEY_GPA, 0);
    }

    public void logout(){
        editor.clear();
        editor.commit();
    }
}
