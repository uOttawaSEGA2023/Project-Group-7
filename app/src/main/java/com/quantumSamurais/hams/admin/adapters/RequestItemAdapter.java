package com.quantumSamurais.hams.admin.adapters;

import static com.quantumSamurais.hams.database.Request.getUserFromRequest;
import static com.quantumSamurais.hams.database.RequestStatus.PENDING;
import static com.quantumSamurais.hams.database.RequestStatus.REJECTED;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.admin.Administrator;
import com.quantumSamurais.hams.admin.listeners.RequestsActivityListener;
import com.quantumSamurais.hams.database.Request;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.patient.Patient;
import com.quantumSamurais.hams.user.User;

import java.util.ArrayList;


public class RequestItemAdapter extends RecyclerView.Adapter<RequestItemAdapter.RequestViewHolder>{
    private final RequestsActivityListener requestClickListener;
    private ArrayList<Request> requests;
    private final Context currentContext;
    FragmentTab activeTab;

    public enum FragmentTab{
        ALL_REQUESTS,
        PENDING_REQUESTS,
        REJECTED_REQUESTS
    }

    public RequestItemAdapter(Context context, FragmentTab activeTab,ArrayList<Request> requestsFromDatabase, RequestsActivityListener listener) {
        Log.d("RequestItemAdapter", "Number of items in requests: " + requestsFromDatabase.size());
        this.activeTab = activeTab;
        setRequests(requestsFromDatabase);
        currentContext = context;
        requestClickListener = listener;
        Log.d("RequestItemAdapter", "Number of items in requests: " + requests.size());
    }

    public void setRequests(ArrayList<Request> requestsFromDatabase){
        // Filters the passed list, and makes it so it contains only the required info
        ArrayList<Request> tempRequest = requestsFromDatabase;
        switch(activeTab){
            case ALL_REQUESTS:
                break;
            case PENDING_REQUESTS:
                ArrayList<Request> pendingRequests = new ArrayList<>();
                for (Request request : requestsFromDatabase){
                    if (request.getStatus() == PENDING){
                        pendingRequests.add(request);
                    }
                }
                tempRequest = pendingRequests;
                break;

            case REJECTED_REQUESTS:
                ArrayList<Request> rejectedRequests = new ArrayList<>();
                for (Request request : requestsFromDatabase){
                    if (request.getStatus() == REJECTED){
                        rejectedRequests.add(request);
                    }
                }
                tempRequest = rejectedRequests;
                break;
        }
        requests = tempRequest;
        notifyDataSetChanged();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        TextView name, emailAddress, userType, requestId;
        ImageButton accept, reject, moreInfo;
        Request request;
        RequestsActivityListener requestsActivityListener;

        public RequestViewHolder(@NonNull View itemView, RequestsActivityListener requestClickListener) {
            super(itemView);
            requestsActivityListener = requestClickListener;
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.deny);
            moreInfo = itemView.findViewById(R.id.showMore);
        }

        private void setData(User user, long id) throws Exception {
            name = itemView.findViewById(R.id.nameRequest);
            emailAddress = itemView.findViewById(R.id.emailAddressRequest);
            userType = itemView.findViewById(R.id.userTypeRequest);
            requestId = itemView.findViewById(R.id.idRequest);

            //
            name.setText(user.getFirstName() + " " + user.getLastName());
            emailAddress.setText(user.getEmail());
            requestId.setText(Long.toString(id));

            //Since it's an ENUM, default is unneeded.
            if (user instanceof Doctor) {
                userType.setText(R.string.doctor);
            } else if (user instanceof Patient) {
                userType.setText(R.string.patient);
            } else if (user instanceof Administrator) {
                Log.d("Request Screen", "Someone managed to create an account as ADMIN... how?");
            }
            setOnClickListeners(request);




        }

        public void setRequest(Request request){
            this.request = request;
        }
        private void setOnClickListeners(Request request){
            View.OnClickListener acceptListener = view -> {
                if (requestsActivityListener != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        requestsActivityListener.onAcceptClick(position);
                    }

                }
            };
            View.OnClickListener rejectListener = view -> {
                if (requestsActivityListener != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        requestsActivityListener.onRejectClick(position);
                    }

                }
            };
            View.OnClickListener showMoreListener = view -> {
                if (requestsActivityListener != null) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent();

                    if (position != RecyclerView.NO_POSITION) {
                        requestsActivityListener.onShowMoreClick(position);
                    }

                }
            };
            //Always there.
            accept.setOnClickListener(acceptListener);
            moreInfo.setOnClickListener(showMoreListener);



            //It only makes sense to have X button for requests that are pending.
            if (request.getStatus() == REJECTED) {
                reject.setVisibility(View.INVISIBLE); // Hide the button
                reject.setEnabled(false); // Make the button uninteractable
            } else {
                //Since the view might be recycled from an object which was REJECTED
                //To make sure the reject button is accessible we have to set it the onclick
                reject.setVisibility(View.VISIBLE);
                reject.setEnabled(true);
                reject.setOnClickListener(rejectListener);


            }
        }

    }

    @NonNull
    @Override
    public RequestItemAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(currentContext).inflate(R.layout.request_item2,parent,false);
        return new RequestViewHolder(v, requestClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestItemAdapter.RequestViewHolder holder, int position) {
        try {
            holder.setRequest(requests.get(position));
            holder.setData(getUserFromRequest(requests.get(position)), requests.get(position).getID());
        } catch (Exception e) {
            Log.d("Requests Screen", e.getMessage() + " " + e.getCause());
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

}
