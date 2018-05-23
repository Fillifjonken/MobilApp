package com.example.marcusjohansson.homeexam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.content.SharedPreferences;

public class FieldActivity extends AppCompatActivity {

    TextView txt1,txt2;
    DataHolder dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);

        this.dh = new DataHolder().getInstance();

        txt1 =  findViewById(R.id.A);
        txt2 =  findViewById(R.id.B);

        /*String restoredText = this.dh.getAdminEmail();
        String restoredText2 = this.dh.getAdminCode();
        if (restoredText != null) {
            txt1.setText(restoredText);
        }
        if (restoredText != null) {
            txt2.setText(String.valueOf(restoredText2));
        }*/
    }
}
