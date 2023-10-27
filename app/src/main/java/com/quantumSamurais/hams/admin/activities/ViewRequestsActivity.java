package com.quantumSamurais.hams.admin.activities;

import static com.quantumSamurais.hams.database.RequestStatus.APPROVED;
import static com.quantumSamurais.hams.database.RequestStatus.REJECTED;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quantumSamurais.hams.R;
import com.quantumSamurais.hams.admin.adapters.RequestItemAdapter;
import com.quantumSamurais.hams.admin.listeners.RequestsActivityListener;
import com.quantumSamurais.hams.database.DatabaseUtils;
import com.quantumSamurais.hams.database.Request;
import com.quantumSamurais.hams.database.RequestStatus;
import com.quantumSamurais.hams.database.callbacks.RequestsResponseListener;
import com.quantumSamurais.hams.doctor.Doctor;
import com.quantumSamurais.hams.login.LoginActivity;
import com.quantumSamurais.hams.user.User;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import java.util.ArrayList;

public class ViewRequestsActivity extends AppCompatActivity implements RequestsActivityListener, RequestsResponseListener {
    DatabaseUtils tools = new DatabaseUtils();
    ArrayList<Request> requests;
    RequestItemAdapter requestsAdapter;
    RecyclerView requestsStack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
    }

    public void setup() {
        RecyclerView.LayoutManager requestsLayoutManger = new LinearLayoutManager(this);
        requestsStack = findViewById(R.id.requestsRecyclerView);

        // Setup RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        requestsAdapter = new RequestItemAdapter(this, null, this);
        requestsStack.setLayoutManager(layoutManager);
        requestsStack.setAdapter(requestsAdapter);

        //I call the refresh runnable method for the first time (instantly)
        //Will then be called periodically
        refreshHandler.post(refreshRunnable);
    }

    public void viewRegistrationRequests() {
        // a list of registration requests from Patients and Doctors
        tools.getSignUpRequests(this);
    }

    public void RefreshButtonClicked(View v){
        viewRegistrationRequests();
    }

    // This handler is needed to allow automatic refresh of the screen
    private Handler refreshHandler = new Handler(Looper.getMainLooper());

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            viewRegistrationRequests();
            refreshHandler.postDelayed(this, 5000);
        }
    };
    @Override
    public void onSuccess(ArrayList<Request> requests) {
        //show the requests
        runOnUiThread(() -> {
            requestsAdapter = new RequestItemAdapter(this, requests, this);
        });
        // Start the periodic data refresh
        refreshHandler.postDelayed(refreshRunnable, 5000);
    }
    private void stopDataRefresh() {
        refreshHandler.removeCallbacks(refreshRunnable);
    }

    @Override
    public void onFailure(Error error) {
        Log.d("admin view", "Something went off when trying to access the DB.");

    }

    @Override
    public void onAcceptClick(int position) {
        long idToAccept = requests.get(position).getID();
        tools.approveSignUpRequest(idToAccept);
        sendEmail(getUserFromRequest(requests.get(position)), APPROVED);
        refreshHandler.post(refreshRunnable);
    }

    @Override
    public void onRejectClick(int position) {
        long idToReject = requests.get(position).getID();
        tools.approveSignUpRequest(idToReject);
        sendEmail(getUserFromRequest(requests.get(position)), REJECTED);
        refreshHandler.post(refreshRunnable);


    }

    public static User getUserFromRequest(Request request){
        if (request == null){
            throw new NullPointerException("Please do not pass a null object to this method");
        }
        switch(request.getUserType()){
            case DOCTOR:
                return request.getDoctor();
            case PATIENT:
                return request.getPatient();
            case ADMIN:
                // We shouldn't get here
        }
        // We shouldn't get here either, since request shouldn't be null.
        return null;
    }

    @Override
    public void onShowMoreClick(int position, Intent showMore) {

        // Get the selected request from the list
        Request selectedRequest = requests.get(position);

        // Create an Intent to navigate to the "Show More" page
        Intent showMoreIntent = new Intent(this, ShowMoreActivity.class);

        // Pass the selected request's data to the "Show More" page
        //showMoreIntent.putExtra("selectedRequest", selectedRequest);


        // Start the "Show More" activity
        startActivity(showMoreIntent);

    }
    /* implementation of a sendEmail method meant to be used to send an email to users
    *   this confirms whether or not they have been granted access to the system.
    *   This implementation uses the an instance of the user and RequestStatus;
    *   however, these two variables can be swapped out given a change in the implementation.
    *
    *
    * @param */
    public void sendEmail(User user, RequestStatus status) {
        try {
            new Thread(() -> {
                // should be put into an on click for both the approve and reject buttons.
                final String username = "seg7quantumsamurais@gmail.com"; // template admin email for the time being.
                final String password = "P@ssW0rd!"; // ''password.
                String msgToSend = new String(), msgSbjct = new String(); // message to send and message subject.
                switch (status) {
                    case APPROVED:
                        msgToSend = "Your request to the health Management app has been approved.\n";
                        msgSbjct = "your account has been approved! :D";
                        break;
                    case REJECTED:
                        msgToSend = "Your request to the health Management app has been denied. \n ";
                        msgSbjct = "your account has been denied. >:0 ";
                        break;
                }
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com"); // set as smtp.gmail.com because of email domain.
                // change if we the email has another domain.
                props.put("mail.smtp.port", "587"); // 587 most common port for sending emails.


                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                try { // try block for sending the email.
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
                    // we set recipients of the email as the user that we verify or reject.
                    message.setSubject(msgSbjct);
                    message.setText(msgToSend);
                    Transport.send(message);
                    Toast.makeText(getApplicationContext(), "patient has been notified.", Toast.LENGTH_LONG).show();


                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        catch (Exception e){
            Log.d("View ", "So that happened: " + e.getStackTrace());
        }

    }

    public class ShowMoreActivity extends AppCompatActivity{
        @Override

        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_more);

            // Retrieve data passed
            Intent intent = getIntent();
            Request selectedRequest = intent.getParcelableExtra("selectedRequest");


        }
    }


}
