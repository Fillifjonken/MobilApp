package example.gifmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;

public class FieldActivity extends AppCompatActivity {

    TextView txt1,txt2;
    DataHolder dh;
    ImageButton A11, B11, C17, C27, C35, C45, D7, E1, E2, E3, S1, R1, R2, R3, R4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);

        this.dh = new DataHolder().getInstance();

        //txt1 =  findViewById(R.id.A);
        //txt2 =  findViewById(R.id.B);

        //Assigns all field-buttons to a variable each
        A11 = (ImageButton) findViewById(R.id.bA11);
        B11 = (ImageButton) findViewById(R.id.bB11);
        C17 = (ImageButton) findViewById(R.id.bC17);
        C27 = (ImageButton) findViewById(R.id.bC27);
        C35 = (ImageButton) findViewById(R.id.bC35);
        C45 = (ImageButton) findViewById(R.id.bC45);
        D7 = (ImageButton) findViewById(R.id.bD7);
        E1 = (ImageButton) findViewById(R.id.bE1);
        E2 = (ImageButton) findViewById(R.id.bE2);
        E3 = (ImageButton) findViewById(R.id.bE3);
        S1 = (ImageButton) findViewById(R.id.bS1);
        R1 = (ImageButton) findViewById(R.id.bR1);
        R2 = (ImageButton) findViewById(R.id.bR2);
        R3 = (ImageButton) findViewById(R.id.bR3);
        R4 = (ImageButton) findViewById(R.id.bR4);

        /*String restoredText = this.dh.getAdminEmail();
        String restoredText2 = this.dh.getAdminCode();
        if (restoredText != null) {
            txt1.setText(restoredText);
        }
        if (restoredText != null) {
            txt2.setText(String.valueOf(restoredText2));
        }*/
    }

    public void launchThree(View v){
        String field = "A0";

        //Checks which button that was pressed
        if(v == A11){field = "A%2011-manna%20(Gstad)";}
        if(v == B11){field = "B%2011-manna%20(Gstad)";}
        if(v == C17){field = "C1%207-manna%20(Gstad)";}
        if(v == C27){field = "C2%207-manna%20(Gstad)";}
        if(v == C35){field = "C3%205-manna%20(Gstad)";}
        if(v == C45){field = "C4%205-manna%20(Gstad)";}
        if(v == D7){field = "D%207-manna%20(Gstad)";}
        if(v == E1){field = "E1%207-manna%20(Gstad)";}
        if(v == E2){field = "E2%205-manna%20(Gstad)";}
        if(v == E3){field = "E3%205-manna%20(Gstad)";}
        if(v == S1){field = "S1%2011-manna%20(Sunderbyn)";}
        if(v == R1){field = "R1%205-manna%20(Rutvik)";}
        if(v == R2){field = "R2%207-manna%20(Rutvik)";}
        if(v == R3){field = "R3%207-manna%20(Rutvik)";}
        if(v == R4){field = "R4%207-manna%20(Rutvik)";}

        //Stores the corresponding fieldname in DataHolder
        DataHolder.getInstance().setFieldNumber(field);
        DataHolder.getInstance().setParceUrl();
        Toast.makeText(getApplicationContext(), field , Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), ScreenThree.class);
        startActivity(intent);

    }
}
