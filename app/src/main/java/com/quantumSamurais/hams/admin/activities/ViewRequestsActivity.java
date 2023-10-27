package com.quantumSamurais.hams.admin.activities;

import static com.quantumSamurais.hams.database.RequestStatus.APPROVED;
import static com.quantumSamurais.hams.database.RequestStatus.DENIED;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;
import javax.mail.MessagingException;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
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
import com.quantumSamurais.hams.user.User;


import java.util.ArrayList;


public class ViewRequestsActivity extends AppCompatActivity implements RequestsActivityListener, RequestsResponseListener {
    DatabaseUtils tools = new DatabaseUtils();
    ArrayList<Request> requests;
    RequestItemAdapter requestsAdapter;
    RecyclerView requestsStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_requests_view);
        setup();
    }

    public void setup() {
        requestsStack = findViewById(R.id.requestsRecyclerView);
        // Setup RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        requestsAdapter = new RequestItemAdapter(this, new ArrayList<Request>(), this);
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

    public void RefreshButtonClicked(View v) {
        refreshHandler.post(refreshRunnable);
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
        Log.d("admin view", "Something went off when trying to access the DB." + error);
    }

    @Override
    public void onAcceptClick(int position) {
        long idToAccept = requests.get(position).getID();
        tools.approveSignUpRequest(idToAccept);
        refreshHandler.post(refreshRunnable);
        //Send the email
        sendEmail(requests.get(position).getUser(), APPROVED);
    }

    @Override
    public void onRejectClick(int position) {
        long idToReject = requests.get(position).getID();
        tools.rejectSignUpRequest(idToReject);
        refreshHandler.post(refreshRunnable);
        //Send the email
        sendEmail(requests.get(position).getUser(), DENIED);
    }

    @Override
    public void onShowMoreClick(int position, Intent showMore) {
    }

    /* implementation of a sendEmail method meant to be used to send an email to users
     *   this confirms whether or not they have been granted access to the system.
     *   This implementation uses the an instance of the user and RequestStatus;
     *   however, these two variables can be swapped out given a change in the implementation.
     *
     *
     * @param */
    public void sendEmail(User user, RequestStatus status) {
        new Thread(() -> {
            // should be put into an on click for both the approve and reject buttons.
            final String username = "admin@gmail.com"; // template admin email for the time being.
            final String password = "12345678"; // '' password.
            String msgToSend = new String(), msgSbjct = new String(); // message to send and message subject.
            switch (status) {
                case APPROVED:
                    msgToSend = "Your request to the health Management app has been approved.\n";
                    msgSbjct = "your account has been approved! :D";
                    break;
                case DENIED:
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

}
