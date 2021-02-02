//package com.example.attendance;
//
//import android.text.TextUtils;
//import android.util.Log;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.nio.charset.Charset;
//
//public class QueryUtils{
//    private static String LOG_TAG = "log tag";
//
//    private static URL createUrl(String stringUrl) {
//        URL url = null;
//        try {
//            url = new URL(stringUrl);
//        } catch (MalformedURLException e) {
//            Log.e(LOG_TAG, "Problem building the URL ", e);
//        }
//        return url;
//    }
//
//    private static String makeHttpRequest(URL url) throws IOException {
//        String jsonResponse = "";
//
//        // If the URL is null, then return early.
//        if (url == null) {
//            return jsonResponse;
//        }
//
//        HttpURLConnection urlConnection = null;
//        InputStream inputStream = null;
//        try {
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setReadTimeout(10000 /* milliseconds */);
//            urlConnection.setConnectTimeout(15000 /* milliseconds */);
//            urlConnection.setRequestMethod("GET");
//            urlConnection.connect();
//
//            // If the request was successful (response code 200),
//            // then read the input stream and parse the response.
//            if (urlConnection.getResponseCode() == 200) {
//                inputStream = urlConnection.getInputStream();
//                jsonResponse = readFromStream(inputStream);
//            } else {
//                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
//            }
//        } catch (IOException e) {
//            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//            if (inputStream != null) {
//                // Closing the input stream could throw an IOException, which is why
//                // the makeHttpRequest(URL url) method signature specifies than an IOException
//                // could be thrown.
//                inputStream.close();
//            }
//        }
//        return jsonResponse;
//    }
//
//    private static String readFromStream(InputStream inputStream) throws IOException {
//        StringBuilder output = new StringBuilder();
//        if (inputStream != null) {
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
//            BufferedReader reader = new BufferedReader(inputStreamReader);
//            String line = reader.readLine();
//            while (line != null) {
//                output.append(line);
//                line = reader.readLine();
//            }
//        }
//        return output.toString();
//    }
//
//    private static User extractUserParamsFromJson(String userJSON){
//        // If the JSON string is empty or null, then return early.
//        if (TextUtils.isEmpty(userJSON)) {
//            return null;
//        }
//
//        //create an empty list to add books to
//        User user;
//
//        // Try to parse the JSON response string. If there's a problem with the way the JSON
//        // is formatted, a JSONException exception object will be thrown.
//        // Catch the exception so the app doesn't crash, and print the error message to the logs.
//        try {
//
//            // Create a JSONObject from the JSON response string
//            JSONObject baseJsonResponse = new JSONObject(userJSON);
//
//            Long id = baseJsonResponse.getLong("id");
//
//            String name = baseJsonResponse.getString("name");
//
//            String type = baseJsonResponse.getString("type");
//
//            if(type == "student"){
//                Double GPA = baseJsonResponse.getDouble("gpa");
//                Integer level = baseJsonResponse.getInt("level");
//
//                user = new User(id, type, name, GPA, level);
//                return user;
//            }else {
//                user = new User(id, type, name);
//                return user;
//            }
//
//
//
//        } catch (JSONException e) {
//            // If an error is thrown when executing any of the above statements in the "try" block,
//            // catch the exception here, so the app doesn't crash. Print a log message
//            // with the message from the exception.
//            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
//        }
//
//        // Return the list of earthquakes
//        return null;
//    }
//
//    public static User fetchUserData(String requestUrl){
//        //create url object
//        URL url = createUrl(requestUrl);
//
//        String jsonRespone = null;
//        try{
//            jsonRespone = makeHttpRequest(url);
//        }catch(IOException e){
//            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
//        }
//
//        User user = extractUserParamsFromJson(jsonRespone);
//
//        return user;
//    }
//}