package example.gifmanager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ScreenFive extends MainActivity{
    private Camera mCamera;
    private CameraPreview mPreview;
    private int cameraId;
    private static final String TAG = "ScreenFive";
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private static final int RESULT_CAPTURE = 0;
    private static final int FAIRPLAY_CAPTURE = 1;
    private static final int GENERATE_PDF_CODE = 0;
    private static final int SHOW_PDF_CODE = 1;
    private static final int SEND_EMAIL_CODE = 2;
    private static int CAPTURE_MODE;
    private ImageView resultView;
    private ImageView fairplayView;
    private Boolean cameraReady;
    private ProgressBar mProgressBar;
    private Button mFinalize;
    private Boolean reportGenerated = false;


    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_five);



        cameraReady = false;
        cameraId = findBackFacingCamera();
        if (cameraId < 0) {
            Toast.makeText(this, "No front facing camera found.",
                    Toast.LENGTH_LONG).show();
        } else {
            mCamera = Camera.open(cameraId);
            Camera.Parameters params = mCamera.getParameters();
            List<Camera.Size> cameraSizes = params.getSupportedPreviewSizes();
            params.setPreviewSize(cameraSizes.get(0).width, cameraSizes.get(0).height);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            mCamera.setParameters(params);
            mCamera.setDisplayOrientation(90);
        }


        mPreview = new CameraPreview(this, mCamera);
        final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.INVISIBLE);

        Button captureResult = (Button) findViewById(R.id.scanResult);
        Button captureFairplay = (Button) findViewById(R.id.scanFairplay);

        resultView = (ImageView) findViewById(R.id.resultView);
        fairplayView = (ImageView) findViewById(R.id.fairplayView);

        final Camera.PictureCallback mPicture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                File pictureFile = getOutputMediaFile(CAPTURE_MODE);
                if (pictureFile == null){
                    Log.d(TAG, "Error creating media file, check storage permissions: "); //+
                            //e.getMessage());
                    return;
                }


                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
                    bitmap = rotateImage(90, bitmap);
                    int width = (4*bitmap.getWidth())/5;
                    int height = bitmap.getHeight()/3;
                    int yOffset = bitmap.getHeight()/14;
                    int xOffset = bitmap.getWidth()/10;
                    int[] pixels = new int[width*height];//the size of the array is the dimensions of the sub-photo
                    bitmap.getPixels(pixels, 0, width, xOffset, yOffset, width, height);//the stride value is (in my case) the width value
                    bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);//ARGB_8888 is a good quality configuration
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//100 is the best quality possibe
                    byte[] square = bos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    //fos.write(data);
                    fos.write(square);
                    fos.close();

                    if(CAPTURE_MODE == RESULT_CAPTURE){
                        resultView.setImageBitmap(bitmap);
                    }else if(CAPTURE_MODE == FAIRPLAY_CAPTURE){
                        fairplayView.setImageBitmap(bitmap);
                    }

                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }
                mPreview.restartPreview();
                mProgressBar.setVisibility(View.INVISIBLE);
                cameraReady = true;
            }
        };


        captureResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cameraReady){
                    cameraReady = false;
                    mProgressBar.setVisibility(View.VISIBLE);
                    CAPTURE_MODE = RESULT_CAPTURE;
                    mCamera.takePicture(null, null, mPicture);
                    //captureResult.setActivated(true);
                }
            }
        });

        captureFairplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraReady){
                    cameraReady = false;
                    mProgressBar.setVisibility(View.VISIBLE);
                    CAPTURE_MODE = FAIRPLAY_CAPTURE;
                    mCamera.takePicture(null, null, mPicture);
                }
            }
        });

        cameraReady = true;

        mFinalize = (Button) findViewById(R.id.finalize);

        final Intent generatePDF = new Intent(this, PDFActivity.class);


        mFinalize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reportGenerated){
                    String[] sendTo = new String[1];
                    sendTo[0] = DataHolder.getInstance().getAdminEmail();
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Uri reportUri = Uri.parse("file://" + DataHolder.getInstance().getReportPath());
                    composeEmail(sendTo, "Match Report", reportUri);
                }else{
                    Toast.makeText(getApplicationContext(), "Generate report before sending email",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        mFinalize.setActivated(false);

        Button mGenerate = (Button) findViewById(R.id.generate);
        mGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DataHolder.getInstance().setReportPath(getReportPath());
                startActivityForResult(generatePDF, GENERATE_PDF_CODE);


            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //get result from data, do something with it

        if(requestCode == GENERATE_PDF_CODE){

            if(Build.VERSION.SDK_INT>=24){
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            //File dest = new File(Environment.getExternalStorageDirectory(), "GIFManager");
            //File pdfFile = new File(dest + File.separator +
            //        "DOC_"+ "result" + ".pdf");
            File pdfFile = new File(DataHolder.getInstance().getReportPath());
            final Intent showPDF = new Intent(Intent.ACTION_VIEW);
            //showPDF.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
            showPDF.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
            showPDF.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivityForResult(showPDF, SHOW_PDF_CODE);
        }else if(requestCode == SHOW_PDF_CODE){
            reportGenerated = true;
        }else{
            finish();
        }
    }


    public void composeEmail(String[] addresses, String subject, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, SEND_EMAIL_CODE);
        }
    }

    public String getReportPath(){

        String nr = DataHolder.getInstance().getNr();
        String currentDate = DataHolder.getInstance().getCurrentDate();
        String timeOfMatch = DataHolder.getInstance().getTimeOfMatch();
        String groupCode = DataHolder.getInstance().getGroupCode();

        String reportPath = Environment.getExternalStorageDirectory() + File.separator +
                "GIFManager" + File.separator + "MATCH_"+ nr + "-" + currentDate + "-" +
                timeOfMatch + "-" + groupCode + ".pdf";
        return reportPath;
    }

    public Bitmap rotateImage(int angle, Bitmap bitmapSrc) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmapSrc, 0, 0,
                bitmapSrc.getWidth(), bitmapSrc.getHeight(), matrix, true);
    }



    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }


    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        //        Environment.DIRECTORY_PICTURES), "MyCameraApp");

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "GIFManager");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("GIFManager", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        File mediaFile;
        if (type == RESULT_CAPTURE){
            mediaFile = new File(DataHolder.getInstance().getResultImagePath());
        } else if(type == FAIRPLAY_CAPTURE){
            mediaFile = new File(DataHolder.getInstance().getFairplayImagePath());
        } else{
            return null;
        }

        return mediaFile;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("permission","granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.uujm
                    Toast.makeText(ScreenFive.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();

                    //app cannot function without this permission for now so close it...
                    onDestroy();
                }
                return;
            }

            // other 'case' line to check fosr other
            // permissions this app might request
        }
    }

    public void loadDummyData(){
        String currentDate = "20182205";
        String adminCode = "1337";
        String adminEmail = "awesomeAdmin@email.com";
        //String nr = "13";
        //String groupCode = "A2";
        //String team1Name = "Gammelstads IF";
        //String team2Name = "Porsön IF";
        //String timeOfMatch = "13:37";
        ArrayList<String> team1Members = new ArrayList<>();
        ArrayList<String> team2Members = new ArrayList<>();
        String resultImagePath = Environment.getExternalStorageDirectory() + File.separator +
                "GIFManager" + File.separator + "IMG_result.jpg";
        String fairplayImagePath = Environment.getExternalStorageDirectory() + File.separator +
                "GIFManager" + File.separator + "IMG_fairplay.jpg";
        String team1SignaturePath = Environment.getExternalStorageDirectory() + File.separator +
                "GIFManager" + File.separator + "Signatures" + File.separator + "signature_home.png";
        String team2SignaturePath = Environment.getExternalStorageDirectory() + File.separator +
                "GIFManager" + File.separator + "Signatures" + File.separator + "signature_visit.png";

        //String reportPath = Environment.getExternalStorageDirectory() + File.separator + "GIFManager" + File.separator + "DOC_result.pdf";

        //File dest = new File(Environment.getExternalStorageDirectory(), "GIFManager");
        //File pdfFile = new File(dest + File.separator +
        //        "MATCH_"+ nr + "-" + currentDate + "-" + timeOfMatch + "-" + groupCode + ".pdf");
        //String reportPath = Uri.fromFile(pdfFile).toString();


        for(int i = 0; i < 15; i++){
            team1Members.add("Team1 member" + Integer.toString(i));
            team2Members.add("Team2 member" + Integer.toString(i));
        }

        team1Members.remove(14);

        DataHolder.getInstance().setCurrentDate(currentDate);
        DataHolder.getInstance().setAdminCode(adminCode);
        DataHolder.getInstance().setAdminEmail(adminEmail);
        //DataHolder.getInstance().setNr(nr);
        //DataHolder.getInstance().setGroupCode(groupCode);
        //DataHolder.getInstance().setTimeOfMatch(timeOfMatch);
        //DataHolder.getInstance().setTeam1Members(team1Members);
        //DataHolder.getInstance().setTeam2Members(team2Members);
        //DataHolder.getInstance().setTeam1Name(team1Name);
        //DataHolder.getInstance().setTeam2Name(team2Name);
        DataHolder.getInstance().setResultImagePath(resultImagePath);
        DataHolder.getInstance().setFairplayImagePath(fairplayImagePath);
        DataHolder.getInstance().setTeam1SignaturePath(team1SignaturePath);
        DataHolder.getInstance().setTeam2SignaturePath(team2SignaturePath);
        //DataHolder.getInstance().setReportPath(reportPath);
    }

    /** A basic Camera preview class */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;
        private static final String TAG = "CameraPreview";

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }


        public void pausPreview(){
            mCamera.stopPreview();
        }

        public void restartPreview(){
            mCamera.stopPreview();
            mCamera.startPreview();
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }
}
