package example.gifmanager;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;

    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/GIFmanager/Signatures/";
    String StoredPath = DIRECTORY + "signature.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //displays last saved signature
        mImageView = (ImageView) findViewById(R.id.sign_image);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(StoredPath));
    }

    public void openScreenFour(View view){
        Intent i = new Intent(getApplicationContext(), ScreenFour.class);
        startActivity(i);
    }

}
