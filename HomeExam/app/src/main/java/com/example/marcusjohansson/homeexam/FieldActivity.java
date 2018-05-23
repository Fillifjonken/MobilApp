package com.example.marcusjohansson.homeexam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.SharedPreferences;

public class FieldActivity extends AppCompatActivity {

    TextView txt1,txt2;
    DataHolder dh;
    ImageButton A11, B11, C17, C27, C35, C45, D7, E1, E2, E3, S1, S2, R1, R2, R3, R4, C25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);

        this.dh = new DataHolder().getInstance();

        txt1 =  findViewById(R.id.A);
        txt2 =  findViewById(R.id.B);

        A11 = (ImageButton) findViewById(R.id.bA11);
        B11 = (ImageButton) findViewById(R.id.bB11);
        C17 = (ImageButton) findViewById(R.id.bC1_7);
        C27 = (ImageButton) findViewById(R.id.bC2_7);
        C35 = (ImageButton) findViewById(R.id.bC3_5);
        C45 = (ImageButton) findViewById(R.id.bC4_5);
        D7 = (ImageButton) findViewById(R.id.bD7);
        E1 = (ImageButton) findViewById(R.id.bE1);
        E2 = (ImageButton) findViewById(R.id.bE2);
        E3 = (ImageButton) findViewById(R.id.bE3);
        S1 = (ImageButton) findViewById(R.id.bS1);
        S2 = (ImageButton) findViewById(R.id.bS2);
        R1 = (ImageButton) findViewById(R.id.bR1);
        R2 = (ImageButton) findViewById(R.id.bR2);
        R3 = (ImageButton) findViewById(R.id.bR3);
        R4 = (ImageButton) findViewById(R.id.bR4);
        C25 = (ImageButton) findViewById(R.id.bC2_5);


        /*String restoredText = this.dh.getAdminEmail();
        String restoredText2 = this.dh.getAdminCode();
        if (restoredText != null) {
            txt1.setText(restoredText);
        }
        if (restoredText != null) {
            txt2.setText(String.valueOf(restoredText2));
        }*/
    }

    public void launchThree(){

    }
}
