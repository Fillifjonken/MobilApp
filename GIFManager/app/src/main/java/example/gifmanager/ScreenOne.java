package example.gifmanager;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScreenOne extends AppCompatActivity {


    //Clean up --Later
    byte[] imageAsBytes;
    private String pass;
    private DataHolder dh;
    private SpinnerAdapter spa;
    private Date dDate;
    private String sDate;
    private int playeryear;
    private SimpleDateFormat n;
    private ArrayList<String> ar ;
    private String[] playeryears;
    Spinner spinner,spinner2;
    Button button1;
    EditText email1,email2,p1,p2,date1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_one);

        //getWindow().setBackgroundDrawableResource(R.drawable.background_screen_one);




        //Set components
        this.pass = getString(R.string.pass);
        this.spinner = (Spinner) findViewById(R.id.spinner);
        this.spinner2 = (Spinner) findViewById(R.id.spinner2);
        this.email1 = (EditText) findViewById(R.id.input_email1);
        this.email2 = (EditText) findViewById(R.id.input_email2);
        this.p1 = (EditText) findViewById(R.id.input_password1);
        this.p2 = (EditText) findViewById(R.id.input_password2);

        //Dataholder init
        this.dh = DataHolder.getInstance();
        this.dh.initDataHolder();

        this.button1 = (Button) findViewById(R.id.button);

        
        //Handle spinner and year in form.
        //Also uggly but redo --Later
        String a[] = generateList();
        final ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(ScreenOne.this, android.R.layout.simple_spinner_dropdown_item, a);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner.setAdapter(myAdapter);
        this.spinner.setDropDownWidth(250);
        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dh.setCurrentDate(myAdapter.getItem(i));
                sDate = cutString(myAdapter.getItem(i));
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //Adapter2 for spinner
        final ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(ScreenOne.this, android.R.layout.simple_spinner_dropdown_item, playeryears);
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner2.setAdapter(myAdapter2);
        this.spinner2.setDropDownWidth(150);
        this.spinner2.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String temp = myAdapter2.getItem(i);
                        dh.setTarget_age(temp);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
        );


        //Set date-----------------
        this.dDate = Calendar.getInstance().getTime();
        String yearString = new SimpleDateFormat("yyyy").format(dDate);
        this.playeryear = Integer.parseInt(yearString);

        //---------------*/

        //-----Test------
        email2.setText("t@gmail.com");
        email1.setText("t@gmail.com");
        p2.setText("admin");
        p1.setText("admin");
        //------------------

        //Move to next activity after filling form
        this.button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });
    }
    //Uggly shit, works but redo. --Later
    private String[] generateList(){
        this.playeryears = new String[]
                        {"00","01","02","03"
                        ,"04","05","06","07"
                        ,"08","09","10","11","12"};

        String a[] = {"2018","2017","2016","2015"};
        return a;
    }
    /*private void generatePlayerYear(int numYear){
        for(int i = this.playeryear; i > this.playeryear - numYear ; i--){
            this.ar.add(Integer.toString(i));
        }
    }*/
    //Cut year to last 2 digits, this to make it easier to handle in DataHolder.
    private String cutString(String s){
        return s.substring(2);
    }

    //Some checks to make sure input is correct.
    private void checkLogin(){
        String email2 = this.email2.getText().toString();
        String email1 = this.email1.getText().toString();
        String pass1 = this.p1.getText().toString();
        String pass2 = this.p2.getText().toString();

        if (isEqual(pass1, pass2)) {
            if (isEqual(email1, email2) && isEmailValid(email1)){
                if(isEqual(pass1, this.pass)){
                    createSession(email1);
                }
                else{
                    this.p1.getText().clear();
                    this.p2.getText().clear();
                    this.p1.setError(getString(R.string.wrong_password));
                    this.p2.setError(getString(R.string.wrong_password));
                }
            }
            else{
                this.email1.setError(getString(R.string.no_match_email));
                this.email2.setError(getString(R.string.no_match_email));
            }
        }
        else{
            this.p1.setError(getString(R.string.wrong_password));
            this.p2.setError(getString(R.string.wrong_password));
        }
    }

    //Check if email has @ and .
    private static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private boolean isEqual(String x , String y){
        return x.equals(y);
    }

    //Form is valid and prepare for next activity.
    //Save data in DataHolder.
    private void createSession( String email ) {

        //Set variables in DataHolder
        this.dh.setAdminCode(this.pass);
        this.dh.setAdminEmail(email);
        this.dh.setCurrentDate(this.sDate);


        //clean Start. --Later--
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("date",this.sDate);
        editor.putString("email", email);
        editor.apply();
        //clean stop. --Later--

        //Start new activity
        Intent intent = new Intent(this, FieldActivity.class);
        startActivity(intent);
    }
}

