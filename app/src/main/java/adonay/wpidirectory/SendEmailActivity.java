package adonay.wpidirectory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class SendEmailActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText subject_in;
    private EditText body_in;
    private ArrayList<String> email_to;
    private String subject;
    private String body;
    private Button sendMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Write your message");

        subject_in = (EditText) findViewById(R.id.subject_input);
        body_in = (EditText) findViewById(R.id.mail_message_input);
        sendMessage = (Button) findViewById(R.id.send_message_button);

        sendMessage.setOnClickListener(this);

        Intent intent = getIntent();
        email_to = intent.getStringArrayListExtra(SearchResultActivity.INPUT_DATA2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message_button:
                subject = subject_in.getText().toString();
                body = body_in.getText().toString();

                Intent sendEmail = new Intent(Intent.ACTION_SEND);
                sendEmail.setType("message/rfc822");
                for(int i = 0; i < email_to.size(); i++){
                    sendEmail.putExtra(android.content.Intent.EXTRA_EMAIL,
                            email_to.toArray(new String[email_to.size()]));
                }
                /*
                i.putExtra(Intent.EXTRA_EMAIL  , email_to); // new String[]{STRING HERE} - to send only a single string*/
                sendEmail.putExtra(Intent.EXTRA_SUBJECT, subject);
                sendEmail.putExtra(Intent.EXTRA_TEXT   , body);
                try {
                    startActivity(Intent.createChooser(sendEmail, "Send mail"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SendEmailActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
