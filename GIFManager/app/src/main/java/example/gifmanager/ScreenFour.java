package example.gifmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ScreenFour extends AppCompatActivity {
    public static boolean signatureFlag;    //Signifies that the user has entered its signature
    static final int REQUEST_CODE = 1;
    Button mConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.screen_four);
        mConfirm = (Button) findViewById(R.id.confirm_button);
        checkSignatureFlag();
    }

    //Opens SignField activity with result request
    //(this enables updating the current activity after SignField has finished
    public void openSignature(View view){
        Intent intent = new Intent(getApplicationContext(), SignField.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    //The result return from SignField activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE) {
            // If the request was successful, then check the signature flag
            if (resultCode == RESULT_OK) {
                checkSignatureFlag();
            }
        }
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
