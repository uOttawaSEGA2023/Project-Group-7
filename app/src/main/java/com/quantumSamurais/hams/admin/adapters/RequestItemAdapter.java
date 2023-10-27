package com.quantumSamurais.hams.admin.adapters;

import static com.quantumSamurais.hams.admin.activities.ViewRequestsActivity.getUserFromRequest;
import static com.quantumSamurais.hams.user.UserType.ADMIN;
import static com.quantumSamurais.hams.user.UserType.DOCTOR;
import static com.quantumSamurais.hams.user.UserType.PATIENT;

import android.content.Context;
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
import com.quantumSamurais.hams.user.UserWrappedDB;

import java.util.ArrayList;
import java.util.Iterator;


public class RequestItemAdapter extends RecyclerView.Adapter<RequestItemAdapter.RequestViewHolder>{
    private final RequestsActivityListener requestClickListener;
    private final ArrayList<Request> requests;
    private Iterator<Request> requestIterator;
    private final Context currentContext;

    public RequestItemAdapter(Context context, ArrayList<Request> requestsFromDatabase, RequestsActivityListener listener) {
        requests = requestsFromDatabase;
        currentContext = context;
        requestIterator = requests.iterator();
        requestClickListener = listener;
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        TextView name, emailAddress, userType;
        ImageButton accept, reject, moreInfo;
        RequestsActivityListener requestsActivityListener;

        public RequestViewHolder(@NonNull View itemView, RequestsActivityListener requestClickListener) {
            super(itemView);
            requestsActivityListener = requestClickListener;
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.deny);
            moreInfo = itemView.findViewById(R.id.showMore);
        }

        private void setData(User user, long id) throws Exception {
            TextView name = itemView.findViewById(R.id.name);
            TextView emailAddress = itemView.findViewById(R.id.emailAddress);
            TextView userType = itemView.findViewById(R.id.userType);
            TextView requestId = itemView.findViewById(R.id.requestId);

            //
            name.setText(user.getFirstName() + " " + user.getLastName());
            emailAddress.setText(user.getEmail());
            requestId.setText(Long.toString(id));

            //Since it's an ENUM, default is unneeded.
            if (user instanceof Doctor) {
                name.setText(R.string.doctor);
            } else if (user instanceof Patient) {
                name.setText(R.string.patient);
            } else if (user instanceof Administrator) {
                Log.d("Request Screen", "Someone managed to create an account as ADMIN... how?");
            }
            setOnClickListeners();

        }
        private void setOnClickListeners(){
            View.OnClickListener acceptListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (requestsActivityListener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            requestsActivityListener.onAcceptClick(position);
                        }

                    }
                }
            };
            View.OnClickListener rejectListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (requestsActivityListener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            requestsActivityListener.onAcceptClick(position);
                        }

                    }
                }
            };
            View.OnClickListener showMoreListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (requestsActivityListener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            requestsActivityListener.onAcceptClick(position);
                        }

                    }
                }
            };
            accept.setOnClickListener(acceptListener);
            reject.setOnClickListener(rejectListener);
            moreInfo.setOnClickListener(showMoreListener);
        }

    }

    @NonNull
    @Override
    public RequestItemAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(currentContext).inflate(R.layout.request_item,parent,false);
        return new RequestViewHolder(v, requestClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestItemAdapter.RequestViewHolder holder, int position) {
        try {
            holder.setData(getUserFromRequest(requests.get(position)), requests.get(position).getID());


        } catch (Exception e) {
            Log.d("Requests Screen", e.getMessage() + " " + e.getCause());
            throw new RuntimeException(e);
        }


    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
