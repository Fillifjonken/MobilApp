package example.gifmanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class ScreenFive extends MainActivity{
    private Camera mCamera;
    private CameraPreview mPreview;
    private static final String TAG = "ScreenFive";
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private static final int RESULT_CAPTURE = 0;
    private static final int FAIRPLAY_CAPTURE = 1;
    private static final int GENERATE_PDF_CODE = 0;
    private static final int SHOW_PDF_CODE = 1;
    private static final int SEND_EMAIL_CODE = 2;
    private static int CAPTURE_MODE;
    private Button captureResult;
    private Button captureFairplay;
    private ImageView resultView;
    private ImageView fairplayView;
    private int requestCode;
    private int grantResults[];
    private Boolean cameraReady;
    private ProgressBar mProgressBar;


    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_five);

        // check storage permission
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ) {
            //if you dont have required permissions ask for it (only required for API 23+)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            onRequestPermissionsResult(requestCode, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, grantResults);
        }

        cameraReady = false;
        int cameraId = findBackFacingCamera();
        if (cameraId < 0) {
            Toast.makeText(this, "No front facing camera found.",
                    Toast.LENGTH_LONG).show();
        } else {
            mCamera = Camera.open(cameraId);
            Camera.Parameters params = mCamera.getParameters();
            List<Camera.Size> cameraSizes = params.getSupportedPreviewSizes();
            params.setPreviewSize(cameraSizes.get(0).width, cameraSizes.get(0).height);

            mCamera.setParameters(params);
            mCamera.setDisplayOrientation(90);
        }
        // Create an instance of Camera
        //mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        final FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.INVISIBLE);

        captureResult = (Button) findViewById(R.id.scanResult);
        captureFairplay = (Button) findViewById(R.id.scanFairplay);

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

                if(Build.VERSION.SDK_INT>=24){
                    try{
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);
                    bitmap = rotateImage(90, bitmap);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight()/2;
                    int[] pixels = new int[width*height];//the size of the array is the dimensions of the sub-photo
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);//the stride value is (in my case) the width value
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

        Button mFinalize = (Button) findViewById(R.id.finalize);

        final Intent gereratePDF = new Intent(this, PDFActivity.class);


        loadDummyData(); //Remove for final version


        mFinalize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] sendTo = new String[1];
                sendTo[0] = DataHolder.getInstance().getAdminEmail();
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Uri reportUri = Uri.parse(DataHolder.getInstance().getReportPath());
                composeEmail(sendTo, "Match Report", reportUri);
            }
        });

        Button mGenerate = (Button) findViewById(R.id.generate);
        mGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(gereratePDF, GENERATE_PDF_CODE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //get result from data, do something with it

        if(requestCode == GENERATE_PDF_CODE){
            File dest = new File(Environment.getExternalStorageDirectory(), "GIFManager");
            File pdfFile = new File(dest + File.separator +
                    "DOC_"+ "result" + ".pdf");
            final Intent showPDF = new Intent(Intent.ACTION_VIEW);
            showPDF.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
            showPDF.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivityForResult(showPDF, SHOW_PDF_CODE);
        }else if(requestCode == SHOW_PDF_CODE){

        }else{
            //finish();
        }

    }

    protected void onResume() {
        super.onResume();

    }

    protected void onPause() {
        super.onPause();
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

    public Bitmap rotateImage(int angle, Bitmap bitmapSrc) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmapSrc, 0, 0,
                bitmapSrc.getWidth(), bitmapSrc.getHeight(), matrix, true);
    }

        /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;

        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
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
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String timeStamp = "result";
        File mediaFile;
        if (type == RESULT_CAPTURE){
            mediaFile = new File(mediaStorageDir + File.separator +
                    "IMG_"+ "result" + ".jpg");
        } else if(type == FAIRPLAY_CAPTURE){
            mediaFile = new File(mediaStorageDir + File.separator +
                    "IMG_"+ "fairplay" + ".jpg");
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
        Date currentDate = new Date();
        String adminCode = "1337";
        String adminEmail = "awesomeAdmin@email.com";
        int nr = 13;
        String groupCode = "A2";
        String team1Name = "Gammelstads IF";
        String team2Name = "Porsön IF";
        ArrayList<String> team1Members = new ArrayList<>();
        ArrayList<String> team2Members = new ArrayList<>();
        String resultImagePath = Environment.getExternalStorageDirectory() + File.separator + "GIFManager" + File.separator + "IMG_result.jpg";
        String fairplayImagePath = Environment.getExternalStorageDirectory() + File.separator + "GIFManager" + File.separator + "IMG_fairplay.jpg";
        String team1SignaturePath = Environment.getExternalStorageDirectory() + File.separator + "GIFManager" + File.separator + "IMG_signature1.jpg";
        String team2SignaturePath = Environment.getExternalStorageDirectory() + File.separator + "GIFManager" + File.separator + "IMG_signature2.jpg";
        //String reportPath = Environment.getExternalStorageDirectory() + File.separator + "GIFManager" + File.separator + "DOC_result.pdf";

        File dest = new File(Environment.getExternalStorageDirectory(), "GIFManager");
        File pdfFile = new File(dest + File.separator +
                "DOC_"+ "result" + ".pdf");
        String reportPath = Uri.fromFile(pdfFile).toString();


        for(int i = 0; i < 15; i++){
            team1Members.add("Team1 member" + Integer.toString(i));
            team2Members.add("Team2 member" + Integer.toString(i));
        }

        DataHolder.getInstance().setCurrentDate(currentDate);
        DataHolder.getInstance().setAdminCode(adminCode);
        DataHolder.getInstance().setAdminEmail(adminEmail);
        DataHolder.getInstance().setNr(nr);
        DataHolder.getInstance().setGroupCode(groupCode);
        DataHolder.getInstance().setTeam1Members(team1Members);
        DataHolder.getInstance().setTeam2Members(team2Members);
        DataHolder.getInstance().setTeam1Name(team1Name);
        DataHolder.getInstance().setTeam2Name(team2Name);
        DataHolder.getInstance().setResultImagePath(resultImagePath);
        DataHolder.getInstance().setFairplayImagePath(fairplayImagePath);
        DataHolder.getInstance().setTeam1SignaturePath(team1SignaturePath);
        DataHolder.getInstance().setTeam2SignaturePath(team2SignaturePath);
        DataHolder.getInstance().setReportPath(reportPath);
    }
/*
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    protected void onResume(){
        super.onResume();
        mCamera.startPreview();
    }
*/
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
