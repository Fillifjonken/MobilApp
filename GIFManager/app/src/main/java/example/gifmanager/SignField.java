package example.gifmanager;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.FileOutputStream;

public class SignField extends AppCompatActivity {
    Button mClear, mSave, mCancel;
    File file;
    RelativeLayout mContent;
    View view;
    signature mSignature;
    Bitmap bitmap;
    String team = "Lag";
    String StoredPath;


    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/GIFmanager/Signatures/";
    String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    //String StoredPath = DIRECTORY + "signature_" + pic_name + ".png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_field);

        //Fetches current team from Intent and declares corresponding filename for signature image
        Bundle b = getIntent().getExtras();
        if(b != null)
            team = b.getString("key");
        this.StoredPath = DIRECTORY + "signature.png";
        if (team.equals("Hemmalag")){
            this.StoredPath = DIRECTORY + "signature_home.png";
            DataHolder.getInstance().setTeam1SignaturePath(this.StoredPath);
        }
        if (team.equals("Bortalag")){
            this.StoredPath = DIRECTORY + "signature_visit.png";
            DataHolder.getInstance().setTeam2SignaturePath(this.StoredPath);
        }

        mContent = (RelativeLayout) findViewById(R.id.canvas_layout);
        mSignature = new signature(getApplicationContext(), null);
        //mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) findViewById(R.id.button_clear);
        mSave = (Button) findViewById(R.id.button_save);
        mSave.setEnabled(false);
        mCancel = (Button) findViewById(R.id.button_cancel);
        view = mContent;

        // Creates new directory if specified directory does not already exist
        file = new File(DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }

    }//End of onCreate()


    public void onClick(View v) {
        //Clears the drawing canvas
        if (v == mClear) {
            Log.v("log_tag", "Panel Cleared");
            mSignature.clear();
            mSave.setEnabled(false);
        }
        //Saves the signature into an image
        else if (v == mSave) {
            Log.v("log_tag", "Panel Saved");
            if (Build.VERSION.SDK_INT >= 23) {  //If SDK below 23 no need to ask for permission
                if (isStoragePermissionGranted() == true){
                    storeSignature();
                }
            } else {
                storeSignature();
            }
        }
        //Ends the signature activity without saving
        else if(v == mCancel){
            Log.v("log_tag", "Panel Canceled");
            setResult(RESULT_CANCELED);
            finish();
        }
    }//End of onClick

    //Stores the signature and ends the signature activity
    public void storeSignature(){
        view.setDrawingCacheEnabled(true);
        mSignature.save(view, StoredPath);
        Toast.makeText(getApplicationContext(), "Signatur sparad", Toast.LENGTH_LONG).show();
        //sets the flag for enabling the continue button in previous activity
        if (team.equals("Hemmalag")){
            ScreenFour.signatureFlag1 = true;
        }
        if (team.equals("Bortalag")){
            ScreenFour.signatureFlag2 = true;
        }
        //ScreenFour.signatureFlag = true;
        Intent intent = new Intent();
        setResult(ScreenFour.RESULT_OK, intent);
        finish();
    }

    //Checks permission for storage
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }//End of isStoragePermissionGranted

    //Automatically continues app when user answers permission request
    //Either proceeds to save signature or shows error message.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            storeSignature();
        }
        else
        {
            Toast.makeText(this, "Tillåtelse att spara signaturen medgavs ej. Signaturen kunde inte sparas.", Toast.LENGTH_LONG).show();
        }
    }

    //contains the functions for drawing on the canvas + clear + save
    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        //saves the signature as an image
        public void save(View v, String StoredPath) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                //Intent intent = new Intent(SignField.this, ScreenFour.class);
                //intent.putExtra("imagePath", StoredPath);
                //startActivity(intent);
                finish();
                mFileOutStream.flush();
                mFileOutStream.close();

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }

        }

        //clears the canvas
        public void clear() {
            path.reset();
            invalidate();
            mSave.setEnabled(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mSave.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

}//End of SignField Class
