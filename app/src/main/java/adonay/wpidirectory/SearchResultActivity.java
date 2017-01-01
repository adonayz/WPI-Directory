package adonay.wpidirectory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchResultActivity extends AppCompatActivity implements View.OnLongClickListener{
    public final static String INPUT_DATA2 = "adonay.wpidirectory.MESSAGE2";

    private LinkedList<String> results;
    private ListView resultList;
    private Context context = SearchResultActivity.this;
    private String numOfResults;
    private ArrayList<String> selectedResults;
    private ArrayList<String> finalEmailsSelected;
    private Toolbar toolbar;
    private static boolean isInActionMode;
    private TextView sendMailToolbarTextView;
    private ResultBaseAdapter adapter;
    private int counter;
    private String messageReceived;

    public static boolean isInActionMode() {
        return isInActionMode;
        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize variable (A MUST)
        selectedResults = new ArrayList<String>();
        isInActionMode =false;
        counter = 0;


        sendMailToolbarTextView = (TextView) findViewById(R.id.sendMailToolbarText);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        messageReceived = intent.getStringExtra(SearchWPIActivity.INPUT_DATA);

        results = separator(extractResults(messageReceived));

        sendMailToolbarTextView.setText("Results " + "(" + numOfResults + " found)");


        resultList = (ListView) findViewById(R.id.resultList);

        adapter = new ResultBaseAdapter(context, results);
        resultList.setAdapter(adapter);
    }

    //if you want the send button to appear initially (without long click)

    /*public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.contextual_menu, menu);
        return true;
    }*/

    // produces pure text content from the htmlCode it consumes
    public static String extractText(String text) {
        return Jsoup.parse(text).text();
    }

    public LinkedList<String> separator(String text) {
        LinkedList<String> result = new LinkedList<String>();
        LinkedList<String> finalResult = new LinkedList<String>();

        String patternString = "<h2";
        Pattern pattern = Pattern.compile(patternString);

        String[] split = pattern.split(text);

        System.out.println("split.length = " + split.length + "\n");

        for (String element : split) {
            result.add("<h2" + element);
        }
        result.removeFirst();

        numOfResults = String.valueOf(result.size());

        if (result.isEmpty()){

            finalResult.add(extractResults(messageReceived));

        }else{
            for (String aResult: result){
                if(checkImage(aResult)){
                    String imgSrc = extractImageSrc(aResult);
                    finalResult.add(extractText(aResult) + " Picture " + imgSrc + " Search Again - Edit your Entry");
                }else{
                    finalResult.add(extractText(aResult) + " Search Again - Edit your Entry");
                }
            }
        }
        return finalResult;
    }

    public static boolean checkImage(String text) {
        return text.contains("http://web.wpi.edu/Campus/Photos");
    }

    public static String extractImageSrc(String text) {

        String htmlFragment = extractImages(text).get(0);

        Pattern pattern = Pattern.compile( "(?m)(?s)<img\\s+(.*)src\\s*=\\s*\"([^\"]+)\"(.*)" );
        Matcher matcher = pattern.matcher(htmlFragment);
        if (matcher.matches()) {
            return matcher.group(2);
        }else{
            return "no image detected";
        }
    }
    public static LinkedList<String> extractImages(String text){
        LinkedList<String> images = new LinkedList<String>();
        Elements elements = Jsoup.parse(text).getElementsByTag("img");

        for (Element element : elements) {
            images.add(element.toString());
        }

        return images;
    }

    public static String extractResults(String text){
        Elements elements = Jsoup.parse(text).getElementsByTag("blockquote");

        return elements.get(0).toString();

    }


    @Override
    public boolean onLongClick(View v) {
        sendMailToolbarTextView.setText(R.string.send_mail_header);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.contextual_menu);
        sendMailToolbarTextView.setVisibility(View.VISIBLE);
        isInActionMode = true;
        adapter.notifyDataSetChanged();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    public void prepareSelection(View view, int position){

        if(((CheckBox)view).isChecked()){
            selectedResults.add(results.get(position));
            counter+=1;
            updateTitle(counter);
        }else{
            selectedResults.remove(results.get(position));
            counter-=1;
            updateTitle(counter);
        }
    }

    public void updateTitle(int count){
        if (count == 0){
            sendMailToolbarTextView.setText(R.string.initial_selected);
        }else{
            String selectedItemsTextDisplay = count + " receiver(s) selected";
            sendMailToolbarTextView.setText(selectedItemsTextDisplay);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        finalEmailsSelected = new ArrayList<String>();

        if (id == R.id.select_mail_receivers) {
            for(String aResult: selectedResults){
                if(adapter.hasAttribute("Email", aResult)){
                    finalEmailsSelected.add(adapter.getEmail(aResult));
                }
            }
            if (finalEmailsSelected.size()>0) {
                Intent sendMailIntent = new Intent(this, SendEmailActivity.class);
                sendMailIntent.putExtra(INPUT_DATA2, finalEmailsSelected);


                startActivity(sendMailIntent);
            }else{
                AlertDialog.Builder Dlg = new AlertDialog.Builder(SearchResultActivity.this, R.style.WPIAlertDialog);

                Dlg.setMessage("The result you chose does not posses an email address.");
                Dlg.setTitle("Invalid Receiver");
                Dlg.setPositiveButton("OK", null);
                Dlg.setCancelable(true);
                Dlg.create().show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
