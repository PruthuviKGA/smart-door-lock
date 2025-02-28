package com.example.smartdoorlockmyapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.LogViewHolder> {

    private ArrayList<LogItem> logList;

    public LogsAdapter(ArrayList<LogItem> logList) {
        this.logList = logList;
    }

    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_card, parent, false);
        return new LogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {
        LogItem logItem = logList.get(position);
        holder.usernameTextView.setText("Username: " + logItem.getUsername());
        holder.verifiedAtTextView.setText("Logged At: " + logItem.getVerifiedAt());
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    public class LogViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, verifiedAtTextView;

        public LogViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            verifiedAtTextView = itemView.findViewById(R.id.verifiedAtTextView);
        }
    }
}

