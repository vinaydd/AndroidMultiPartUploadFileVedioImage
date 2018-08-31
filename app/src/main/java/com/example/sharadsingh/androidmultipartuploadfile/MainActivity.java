package com.example.sharadsingh.androidmultipartuploadfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	// LogCat tag
	private static final String TAG = MainActivity.class.getSimpleName();


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_FILE = 3;
    private static final int UPLOAD_FILE_REQUEST_CODE = 10;

    private Uri fileUri; // file url to store image/video
    
    private Button btnCapturePicture, btnRecordVideo,btPdfFile;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        
        // Changing action bar background color
        // These two lines are not needed

 
        btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);
        btnRecordVideo = (Button) findViewById(R.id.btnRecordVideo);
        btPdfFile = (Button) findViewById(R.id.btPdfFile);

        /**
         * Capture image button click event
         */
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                // capture picture
                try {
                    captureImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
 
        /**
         * Record video button click event
         */
        btnRecordVideo.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                // record video
                try {
                    recordVideo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btPdfFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // record video
                try {
                   uploadFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }

    private void uploadFile() throws IOException {

        Intent intent = new Intent();
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_FILE);
        intent.setType("application/doc");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), UPLOAD_FILE_REQUEST_CODE);


    }

    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
 
    /**
     * Launching camera app to capture image
     */
    private void captureImage() throws IOException {
         Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
         fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
         intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
         startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);



    }
    
    /**
     * Launching camera app to record video
     */
    private void recordVideo() throws IOException {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
 
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
 
        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
 
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
                                                            // name
 
        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }
 
    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
 
        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }
 
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                // fileUri = getOutputMediaFileUri(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                launchUploadActivity(true,fileUri);

            	
            } else if (resultCode == RESULT_CANCELED) {
                
            	// user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {



            	// video successfully recorded
                // launching upload activity
            	launchUploadActivity(false);
            
            } else if (resultCode == RESULT_CANCELED) {
                
            	// user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }


        else if (requestCode == UPLOAD_FILE_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                 Uri  uri  = data.getData();
                 String pdfPathString = FilePath.getPath(this, uri);
                launchUploadActivityPDfFile(false,pdfPathString);

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }


        }
    }

    private void launchUploadActivity(boolean isImage, Uri fileUri) {
        Intent i = new Intent(MainActivity.this, UploadActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);
    }

    private void launchUploadActivity(boolean isImage){
    	Intent i = new Intent(MainActivity.this, UploadActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);
    }

    private void launchUploadActivityPDfFile(boolean isImage,String pdfPathString){
        Intent i = new Intent(MainActivity.this, UploadActivity.class);
        i.putExtra("filePath",pdfPathString);
        i.putExtra("isImage", isImage);
        startActivity(i);
    }



    public Uri getOutputMediaFileUri(int type) throws IOException {
        return Uri.fromFile(getOutputMediaFile(type));
    }
 

    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), Config.IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getAbsolutePath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getAbsolutePath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        }else if (type == MEDIA_TYPE_FILE) {
            mediaFile = new File(mediaStorageDir.getAbsolutePath() + File.separator
                    + "file_" + timeStamp + ".pdf");
        }
        else {
            return null;
        }
        return mediaFile;
    }


    public File createDirectoryAndSaveFile(Context context, int i) throws IOException {
        File file = null;
        Fragment fragment = null;
        File thumbFile = null;
        FileOutputStream outThumb = null;
        String[] paths = new String[2];
        File direct = new File(Environment.getExternalStorageDirectory() + "/"
                + context.getResources().getString(R.string.app_name).replace(" ", "_").trim());// +
        if (direct.exists()) {
            direct.delete();
        }
        try {
            direct.mkdir();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            file = new File(direct.getAbsolutePath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");

            FileOutputStream out = new FileOutputStream(file);



            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (file != null)
            paths[0] = file.getAbsolutePath();
        if (file != null && thumbFile != null)
            paths[1] = thumbFile.getAbsolutePath();
        return file;
        //return file;
    }







}