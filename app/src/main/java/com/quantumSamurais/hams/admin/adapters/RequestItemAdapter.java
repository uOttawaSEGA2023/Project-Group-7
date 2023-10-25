package com.quantumSamurais.hams.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.database.Request;
import com.quantumSamurais.hams.doctor.adapters.CheckableItemAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;

public class RequestItemAdapter extends RecyclerView.Adapter<RequestItemAdapter.RequestViewHolder>{
    private final ArrayList<Request> requests;
    private Iterator<Request> requestIterator;
    private final Context currentContext;

    public RequestItemAdapter(Context context, ArrayList<Request> requestsFromDatabase) {
        requests = requestsFromDatabase;
        currentContext = context;
        requestIterator = requests.iterator();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        TextView name, emailAddress, userType;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        private void setData(){

        }



    }

    @NonNull
    @Override
    public RequestItemAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(currentContext).inflate(R.layout.request_item,parent,false);
        return new RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestItemAdapter.RequestViewHolder holder, int position) {

        RequestViewHolder.setData(requests[position].);

    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
