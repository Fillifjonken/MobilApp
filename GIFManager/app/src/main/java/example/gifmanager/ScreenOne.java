package example.gifmanager;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ScreenOne extends AppCompatActivity {


    private String pass = "admin";
    private DataHolder dh;

    private Date dDate;
    private String sDate;
    Button button1;
    EditText email1,email2,p1,p2,date1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_one);

        this.dh = new DataHolder().getInstance();

        this.date1 = (EditText) findViewById(R.id.date);
        email1 = (EditText) findViewById(R.id.input_email1);
        email2 = (EditText) findViewById(R.id.input_email2);
        p1 = (EditText) findViewById(R.id.input_password1);
        p2 = (EditText) findViewById(R.id.input_password2);

        button1 = (Button) findViewById(R.id.button);

        //Set date-----------------
        dDate = Calendar.getInstance().getTime();

        SimpleDateFormat n = new SimpleDateFormat("yy");
        sDate = n.format(dDate).toString();
        date1.setText(sDate); //Här krashar det
        //---------------

        //-----Test------
        email2.setText("t@gmail.com");
        email1.setText("t@gmail.com");
        p2.setText("admin");
        p1.setText("admin");
        //------------------


        this.button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });
    }
    private void checkLogin(){
        String email2 = this.email2.getText().toString();
        String email1 = this.email1.getText().toString();
        String date = this.date1.getText().toString();
        String pass1 = this.p1.getText().toString();
        String pass2 = this.p2.getText().toString();

        if (isEqual(pass1, pass2)) {
            if (isEqual(email1, email2) && isEmailValid(email1)){
                if(isEqual(pass1, this.pass)){
                    createSession(date,email1);
                }
                else{
                    this.p1.getText().clear();
                    this.p2.getText().clear();
                    this.p1.setError("Wrong password!");
                    this.p2.setError("Wrong password!");
                }
            }
            else{
                this.email1.setError("No matching email");
                this.email2.setError("No matching email");
            }
        }
        else{
            this.p1.setError("No matching password");
            this.p2.setError("No matching password");
        }
    }

    private static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private boolean isEqual(String x , String y){
        return x.equals(y);
    }
    private boolean isEmpty(String x){
        return x.isEmpty();
    }


    private void createSession(String date , String email ) {

        //Set variables
        this.dh.setAdminCode(this.pass);
        this.dh.setAdminEmail(email);
        this.dh.setCurrentDate("17");

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("date",date);
        editor.putString("email", email);
        editor.apply();
        Intent intent = new Intent(this, FieldActivity.class);
        startActivity(intent);
    }

}

