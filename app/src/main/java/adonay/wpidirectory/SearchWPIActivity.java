package adonay.wpidirectory;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.gms.actions.SearchIntents;
import com.google.firebase.crash.FirebaseCrash;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;

public class SearchWPIActivity extends AppCompatActivity implements OnClickListener {
   final static String INPUT_DATA = "adonay.wpidirectory.MESSAGE";

    private EditText p_in;
    private String person;
    private RadioGroup nORu;
    private boolean isName;
    private EditText t_in;
    private String title;
    private EditText d_in;
    private String department;
    private CheckBox checkStudent;
    private CheckBox checkAlumni;
    private CheckBox checkStaff;
    private CheckBox checkFaculty;
    private boolean isStudent;
    private boolean isAlumni;
    private boolean isStaff;
    private boolean isFaculty;

    private Context context;
    private Message msg = new Message();
    private Message msg2 = new Message();
    private Message msg3 = new Message();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_wpi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search WPI Directory");


        p_in = ((EditText) findViewById(R.id.name_input));
        t_in = ((EditText) findViewById(R.id.title_input));
        d_in = ((EditText) findViewById(R.id.department_input));
        nORu = ((RadioGroup) findViewById(R.id.radioGroup));
        checkStudent = ((CheckBox) findViewById(R.id.isStudents));
        checkAlumni = ((CheckBox) findViewById(R.id.isAlumni));
        checkStaff = ((CheckBox) findViewById(R.id.isStaff));
        checkFaculty = ((CheckBox) findViewById(R.id.isFaculty));

        Button search;
        search = ((Button) findViewById(R.id.search_button));

        search.setOnClickListener(this);


        Intent intent = getIntent();
        if(SearchIntents.ACTION_SEARCH.equals(intent.getAction())){
            p_in.setText(intent.getStringExtra(SearchManager.QUERY));
            onClick(search);
        }

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_wpi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent aboutWPIIntent = new Intent(this, AboutAppActivity.class);
            startActivity(aboutWPIIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.search_button:
                person = p_in.getText().toString();
                title = t_in.getText().toString();
                department = d_in.getText().toString();
                isStudent = checkStudent.isChecked();
                isAlumni = checkAlumni.isChecked();
                isStaff = checkStaff.isChecked();
                isFaculty = checkFaculty.isChecked();
                isName = whichRadio(nORu.getCheckedRadioButtonId());
                final Intent intent = new Intent(this, SearchResultActivity.class);

                context = SearchWPIActivity.this;

                final Handler handler = new Handler(new Handler.Callback() {

                    @Override
                    public boolean handleMessage(Message msg) {
                        if (msg.arg1 == 1) {
                            AlertDialog.Builder uDlg = new AlertDialog.Builder(SearchWPIActivity.this, R.style.WPIAlertDialog);

                            uDlg.setMessage("WPI Directory could not access the internet. Check your internet connection.");
                            uDlg.setTitle("Connection problem");
                            uDlg.setPositiveButton("OK", null);
                            uDlg.setCancelable(true);
                            uDlg.create().show();

                        }
                        if (msg.arg2 == 2) {

                            AlertDialog.Builder tDlg = new AlertDialog.Builder(SearchWPIActivity.this, R.style.WPIAlertDialog);

                            tDlg.setMessage("Your Internet Connection is either slow, blocked, or does not exist.");
                            tDlg.setTitle("Connection to WPI has timed out.");
                            tDlg.setPositiveButton("OK", null);
                            tDlg.setCancelable(true);
                            tDlg.create().show();

                        }
                        if (msg.arg1 == 3) {
                            AlertDialog.Builder uDlg = new AlertDialog.Builder(SearchWPIActivity.this, R.style.WPIAlertDialog);

                            uDlg.setMessage("Unknown Error has occurred.");
                            uDlg.setTitle("Unknown Error");
                            uDlg.setPositiveButton("OK", null);
                            uDlg.setCancelable(true);
                            uDlg.create().show();

                        }

                        return false;

                    }
                });

                final Handler handler2 = new Handler(new Handler.Callback() {

                    @Override
                    public boolean handleMessage(Message msg) {
                        if (msg.arg1 == 1) {
                            AlertDialog.Builder uDlg = new AlertDialog.Builder(SearchWPIActivity.this, R.style.WPIAlertDialog);

                            uDlg.setMessage("Unknown Error has occurred.");
                            uDlg.setTitle("Unknown Error");
                            uDlg.setPositiveButton("OK", null);
                            uDlg.setCancelable(true);
                            uDlg.create().show();

                        }

                        return false;
                    }
                });

                final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    private ProgressDialog progress;

                    @Override
                    protected void onPreExecute()
                    {
                        progress = new ProgressDialog(context);
                        progress.setTitle("Searching Directory");
                        progress.setMessage("Connecting to WPI");
                        progress.setCancelable(true);
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setOnCancelListener(new DialogInterface.OnCancelListener()

                        {
                            @Override
                            public void onCancel(DialogInterface progress)
                            {
                                cancel(false);
                            }
                        });

                        progress.show();

                    }

                    @Override
                    protected Void doInBackground(Void... params)
                    {
                        try {
                            msg = new Message();
                            msg2 = new Message();
                            msg3 = new Message();

                            String htmlCode = searchDirectory(person, title, department, isStudent, isAlumni, isStaff, isFaculty, isName);
                            /*String htmlCode = searchDirectory(person, isStudent, isAlumni, isStaff, isFaculty, isName);*/

                            intent.putExtra(INPUT_DATA, htmlCode);
                            progress.dismiss();
                            startActivity(intent);
                        } catch (UnknownHostException u) {
                            progress.dismiss();
                            msg.arg1 = 1;
                            handler.sendMessage(msg);
                        } catch (SocketTimeoutException t) {
                            progress.dismiss();
                            msg3.arg2 = 2;
                            handler.sendMessage(msg3);
                        } catch (Exception e) {
                            progress.dismiss();
                            msg2.arg1 = 1;
                            handler2.sendMessage(msg2);
                            Log.e("WPI Directory", "Unknown error has been detected!", e);
                            FirebaseCrash.report(e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result)
                    {
                        //called on ui thread
                        if (progress != null) {
                            progress.dismiss();
                        }
                    }

                    @Override
                    protected void onCancelled()
                    {
                        //called on ui thread
                        if (progress!= null) {
                            progress.dismiss();
                        }
                    }
                };

                try {
                    task.execute();
                } catch (Exception e) {
                    Log.e("WPI Directory", "Searching thread could not be started.", e);
                    FirebaseCrash.report(e);

                }
                break;
            default:
                break;
        }
    }

    private boolean whichRadio(int id) {
        if (id == R.id.name_radio) {
            return true;
        } else if (id == R.id.user_radio) {
            return false;
        }
        return false;
    }

    private String searchDirectory(String person, String title, String department, boolean isStudent, boolean isAlumni, boolean isStaff, boolean isFaculty, boolean isName) throws IOException {

        String studentValue;
        String alumniValue;
        String staffValue;
        String facultyValue;
        String isNameStringValue;


        URL url = new URL("https://web.wpi.edu/cgi-bin/ldap-html.cgi");
        URLConnection con = url.openConnection();
        con.setConnectTimeout(15000);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


        DataOutputStream printout = new DataOutputStream(con.getOutputStream());


        if (isStudent) {
            studentValue = ("&get_student=" + URLEncoder.encode("yes", "UTF-8"));
        } else {
            studentValue = "";
        }

        if (isAlumni) {
            alumniValue = ("&get_alum=" + URLEncoder.encode("yes", "UTF-8"));
        } else {
            alumniValue = "";
        }

        if (isStaff) {
            staffValue = ("&get_staff=" + URLEncoder.encode("yes", "UTF-8"));
        } else {
            staffValue = "";
        }

        if (isFaculty) {
            facultyValue = ("&get_faculty=" + URLEncoder.encode("yes", "UTF-8"));
        } else {
            facultyValue = "";
        }

        if (isName) {
            isNameStringValue = "no";
        } else {
            isNameStringValue = "yes";
        }


        String parameters = ("name=" + URLEncoder.encode(person, "UTF-8")) + "&"
                + ("login=" + URLEncoder.encode(isNameStringValue, "UTF-8")) + "&" + ("title=" + URLEncoder.encode(title, "UTF-8"))
                + "&" + ("department=" + URLEncoder.encode(department, "UTF-8")) + studentValue + alumniValue + staffValue
                + facultyValue;

        printout.writeBytes(parameters);
        printout.flush();
        printout.close();
        DataInputStream input = new DataInputStream(con.getInputStream());
        String line;
        String htmlCode = "";
        while ((line = input.readLine()) != null) {
            htmlCode += line;
        }
        return htmlCode;
    }
/*

    private String searchDirectory(String person, boolean isStudent, boolean isAlumni, boolean isStaff, boolean isFaculty, boolean isName) throws IOException {

        String studentValue;
        String alumniValue;
        String staffValue;
        String facultyValue;
        String isNameStringValue;

        URL url = new URL("https://web.wpi.edu/cgi-bin/ldap-html.cgi");
        URLConnection con = url.openConnection();
        con.setConnectTimeout(15000);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


        DataOutputStream printout = new DataOutputStream(con.getOutputStream());


        if (isStudent) {
            studentValue = ("&get_student=" + URLEncoder.encode("yes", "UTF-8"));
        } else {
            studentValue = "";
        }

        if (isAlumni) {
            alumniValue = ("&get_alum=" + URLEncoder.encode("yes", "UTF-8"));
        } else {
            alumniValue = "";
        }

        if (isStaff) {
            staffValue = ("&get_staff=" + URLEncoder.encode("yes", "UTF-8"));
        } else {
            staffValue = "";
        }

        if (isFaculty) {
            facultyValue = ("&get_faculty=" + URLEncoder.encode("yes", "UTF-8"));
        } else {
            facultyValue = "";
        }

        if (isName) {
            isNameStringValue = "no";
        } else {
            isNameStringValue = "yes";
        }

        String parameters = ("name=" + URLEncoder.encode(person, "UTF-8")) + "&"
                + ("login=" + URLEncoder.encode(isNameStringValue, "UTF-8")) + studentValue + alumniValue + staffValue
                + facultyValue;

        printout.writeBytes(parameters);
        printout.flush();
        printout.close();
        DataInputStream input = new DataInputStream(con.getInputStream());
        String line;
        String htmlCode = "";
        while ((line = input.readLine()) != null) {
            htmlCode += line;
        }
        return htmlCode;
    }
*/


}
