package com.example.attendance.Absence;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AbsenceAPIs {
    @GET("ta/getAbsence")
    Call<Absence[]> getAbsence(@Query("studentID") Integer studentID);
    @GET("ta/getRecent")
    Call<Recent[]>getRecent(@Query("studentId") Integer studentId);
    @GET("ta/getRecentTA")
    Call<TaRecent[]>getRecentTA(@Query("TAId")Integer TAId);
}
