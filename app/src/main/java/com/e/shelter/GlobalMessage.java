package com.e.shelter;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.e.shelter.utilities.Global;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;

public class GlobalMessage extends Global {
    Button sendBtn;
    EditText txtMessage;
    EditText subjectTxt;
    String message;
    String subject;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_message);
        sendBtn = (Button) findViewById(R.id.sendSmsButton);
        subjectTxt =(EditText) findViewById(R.id.subject_text);
        txtMessage = (EditText) findViewById(R.id.messageText);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    sendEmail();
                } catch (FirebaseAuthException e) {
                    e.printStackTrace();
                }
            }
        });
    }

        protected void sendEmail() throws FirebaseAuthException {
            String ToMails="";

            ListUsersPage page = firebaseAuth.listUsers(null);
            for (ExportedUserRecord user : page.iterateAll()) {
                System.out.println("User: " + user.getUid());
            }

            subject =subjectTxt.getText().toString();
            message =txtMessage.getText().toString();
            Log.i("Send email", "");

            String[] TO = ToMails.split(",");
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT,message);
            emailIntent.setType("message/rfc882");

            try {
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                finish();
                Log.i("Finished sending email...", "");
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(GlobalMessage.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }
        }


}
