package example.gifmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ScreenFour extends AppCompatActivity {
    public static boolean signatureFlag;    //Signifies that the user has entered its signature
    static final int REQUEST_CODE = 1;
    String team = "Lag"; // or other values
    TextView teamName;
    Button mConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        if(b != null)
            this.team = b.getString("key"); //Fetches the team from Intent
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.screen_four);
        teamName = (TextView) findViewById(R.id.textView_teamname);
        teamName.setText(team);
        mConfirm = (Button) findViewById(R.id.confirm_button);
        checkSignatureFlag();
    }

    //Opens SignField activity with result request
    //(this enables updating the current activity after SignField has finished
    public void openSignature(View view){
        Intent intent = new Intent(getApplicationContext(), SignField.class);
        Bundle b = new Bundle();
        b.putString("key", team); //Declares which team for next Intent
        intent.putExtras(b); //Puts team to the next Intent
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    //On return from other activity (SignField), button availability is updated
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        checkSignatureFlag();
    }

    //Checks if SignatureFlag is true (meaning user has entered signature)
    public void checkSignatureFlag(){
        if (signatureFlag) {
            mConfirm.setEnabled(true); //Enables "confirm"-button
        } else {
            mConfirm.setEnabled(false); //Disables "confirm"-button
        }
    }
}
