package com.example.smartdoorlockmyapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;

public class ViewLogsActivity extends AppCompatActivity {

    private RecyclerView logsRecyclerView;
    private LogsAdapter logsAdapter;
    private ArrayList<LogItem> logList;
    private TextView messageTextView;
    private Button logoutButton;
    private Handler handler = new Handler();
    private Runnable fetchLogsRunnable;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_logs);

        logsRecyclerView = findViewById(R.id.logsRecyclerView);
        messageTextView = findViewById(R.id.messageTextView);
        logoutButton = findViewById(R.id.logoutButton);

        // Setup RecyclerView
        logsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        logList = new ArrayList<>();
        logsAdapter = new LogsAdapter(logList);
        logsRecyclerView.setAdapter(logsAdapter);


        int fingerprintId = getIntent().getIntExtra("FINGERPRINT_ID", -1);

        if (fingerprintId != -1) {
            Log.d("ViewLogsActivity", "Received Fingerprint ID: " + fingerprintId);
            verifyFingerprint(fingerprintId); // Call API with received ID
        }

        logoutButton.setOnClickListener(v -> {
            Toast.makeText(ViewLogsActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();

            // Navigate back to MainActivity
            Intent intent = new Intent(ViewLogsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish(); // Finish the current activity
        });

        startAutoRefresh();

        // Fetch logs from API
//        verifyFingerprint(4);
        fetchLogsFromAPI();
    }


//    public void handleFingerprintId() {
//        int fingerprintId = getIntent().getIntExtra("FINGERPRINT_ID", -1);
//        if (fingerprintId != -1) {
//            Toast.makeText(this, "Received Fingerprint ID: " + fingerprintId, Toast.LENGTH_SHORT).show();
//            // Perform further actions, such as updating the UI
//        }
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        handleFingerprintId();
//    }










    public void verifyFingerprint(Integer fingerprintId) {

        System.out.println(fingerprintId);

        String url = "http://172.20.10.9:8070/user/verifyFingerprint/" + fingerprintId;  // API URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do nothing with the response
                    }
                },
                error -> {
                    // Handle error silently
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(stringRequest);
    }
    private void startAutoRefresh() {
        fetchLogsRunnable = new Runnable() {
            @Override
            public void run() {
                fetchLogsFromAPI();
                handler.postDelayed(this, 5000); // Refresh every 5 seconds
            }
        };
        handler.post(fetchLogsRunnable); // Start the periodic task
    }

    private void fetchLogsFromAPI() {
        logList.clear(); // Clear the existing list to avoid duplicatio
        String url = "http://172.20.10.9:8070/logs"; // Replace with your actual API URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse the response JSON
                            JSONArray logsArray = new JSONArray(response);
                            if (logsArray.length() > 0) {
                                for (int i = 0; i < logsArray.length(); i++) {
                                    JSONObject logObject = logsArray.getJSONObject(i);
                                    String username = logObject.getString("username");
                                    String verifiedAt = logObject.getString("verifiedAt");
                                    String formattedVerifiedAt = convertToReadableDate(verifiedAt);

                                    // Create the LogItem and add it to the list
                                    LogItem logItem = new LogItem(username, formattedVerifiedAt);
                                    logList.add(logItem);
                                }
                                logsAdapter.notifyDataSetChanged();
                            } else {
                                messageTextView.setText("No verified logs found.");
                                messageTextView.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            messageTextView.setText("Error parsing logs.");
                            messageTextView.setVisibility(View.VISIBLE);
                        }
                    }
                },
                error -> {
                    messageTextView.setText("Error fetching logs.");
                    messageTextView.setVisibility(View.VISIBLE);
                });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    // Method to convert the verifiedAt string to a readable format
    private String convertToReadableDate(String dateStr) {
        // Define the input date format (ISO 8601 format)
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());

        // Define the output format for the readable date
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault());

        try {
            // Parse the input date string
            Date date = inputFormat.parse(dateStr);

            // Format the date to the desired output format
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid Date";  // In case the date format is incorrect
        }
    }
}
