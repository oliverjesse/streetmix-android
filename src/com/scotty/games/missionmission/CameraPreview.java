package com.scotty.games.missionmission;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

// ----------------------------------------------------------------------

public class CameraPreview extends Activity implements SurfaceHolder.Callback {
    private ImageView mImageView;
	private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private boolean mPreviewRunning;
    
    private ImageButton mapViewButton;
    private ImageButton takePictureButton;
    private TextView debugText;
    private String debugString = "";
    
    private Button yesButton;
    private Button noButton;
    private TextView debugImageText;
    
    private Camera.PictureCallback mPictureCallbackRaw;
    private Camera.PictureCallback mPictureCallbackJpeg;
    private Camera.ShutterCallback mShutterCallback;
    
    private SimpleDateFormat timeStampFormat = 
        new SimpleDateFormat("yyyyMMddHHmmssSS");    
    
    private Intent data;    
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.camera_layout);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        data = getIntent();
        
        
        
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        debugText = (TextView) findViewById(R.id.debugtext);
        debugText.setText(debugString);
        
        mImageView = (ImageView) findViewById(R.id.captured_image);
        debugImageText = (TextView) findViewById(R.id.debugimagetext);
        
        mPictureCallbackRaw = new Camera.PictureCallback() {  
            public void onPictureTaken(byte[] camData, Camera c) {  
                Log.e(getClass().getSimpleName(), "PICTURE CALLBACK RAW: " + camData);  
                debugString = debugString + " Raw";
                debugText.setText(debugString);
                
                mCamera.startPreview();
                
                //TODO: Put in a cancel option here.
                data.putExtra("com.scotty.games.missionmission.tookPicture", true);
                setResult(RESULT_OK);
                finish();
                //setContentView(R.layout.image_preview);
                //yesButton = (Button) findViewById(R.id.yesbutton);
                //yesButton.setOnClickListener(new ImageView.OnClickListener() {
                //        @Override
                //        public void onClick(View v) {
                //        }
                //});
                //
                //noButton = (Button) findViewById(R.id.nobutton);
                //noButton.setOnClickListener(new ImageView.OnClickListener() {
                //        @Override
                //        public void onClick(View v) {
                //            setContentView(R.layout.camera_layout);
                //        }
                //});
            }  
        };  
        
        mPictureCallbackJpeg = new Camera.PictureCallback() {  
            public void onPictureTaken(byte[] camData, Camera c) {  
                Log.e(getClass().getSimpleName(), "PICTURE CALLBACK JPEG: data.length = " + camData);  
                debugString = debugString + " Jpeg";
                debugText.setText(debugString);
            }  
        };  
        
        mShutterCallback = new Camera.ShutterCallback() {  
            public void onShutter() {  
                Log.e(getClass().getSimpleName(), "SHUTTER CALLBACK");  
            }  
        };
        
        //The Camera View swap button.
        mapViewButton = (ImageButton) findViewById(R.id.mapViewButton);
        mapViewButton.setOnClickListener(new SurfaceView.OnClickListener() {
			@Override
			public void onClick(View v) {
			    setResult(RESULT_CANCELED);
				finish();
			}
        });
        
        
			
        
        //The 'Take-a-picture' button.
        takePictureButton = (ImageButton) findViewById(R.id.takePictureButton);
        takePictureButton.setOnClickListener(new SurfaceView.OnClickListener() {
			@Override
			public void onClick(View v) {
			    ImageCaptureCallback iccb = null;
			    
			    try {  
			        String filename = timeStampFormat.format(new Date());  
			        ContentValues values = new ContentValues();  
			        values.put(Media.TITLE, filename);  
			        values.put(Media.DESCRIPTION, "Image capture by camera");  
			        Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);  
			        iccb = new ImageCaptureCallback( getContentResolver().openOutputStream(uri));  
			    } catch(Exception ex ){  
			        ex.printStackTrace();  
			        Log.e(getClass().getSimpleName(), ex.getMessage(), ex);
			    }
			    
			    mCamera.takePicture(mShutterCallback, mPictureCallbackRaw, iccb);  

			    //mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);	
			    //Bitmap bm = (Bitmap) intent.getExtras().get("data");

            }
        });
    }
    
    
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it 
        // where to draw.
        mCamera = Camera.open();
        
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
            // TODO: add more exception handling logic here
        }
    }
    
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mCamera.stopPreview();
        mPreviewRunning = false;
        mCamera.release();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        
        if (mPreviewRunning) {
            mCamera.stopPreview();
        }
        
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(w, h);
        //Parameters parameters = camera.getParameters();
        //parameters.set("jpeg-quality", 100);
        //parameters.set("orientation", "portrait");
        //parameters.set("orientation", "landscape");
        //parameters.set("picture-size", "320X430");
        //parameters.set("rotation", 90);
        //parameters.setPictureFormat(PixelFormat.JPEG);

        mCamera.setParameters(parameters);
        try {            
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        mCamera.startPreview();
        mPreviewRunning = true;
    }
    
     
}


class ImageCaptureCallback implements PictureCallback  {  
    private OutputStream filoutputStream;  
    public ImageCaptureCallback(OutputStream filoutputStream) {  
        this.filoutputStream = filoutputStream;  
    }  
    
    @Override  
    public void onPictureTaken(byte[] camData, Camera camera) {  
        try {  
            Log.v(getClass().getSimpleName(), "onPictureTaken=" + camData + " length = " + camData.length);  
            filoutputStream.write(camData);  
            filoutputStream.flush();  
            filoutputStream.close();  
        } catch(Exception ex) {  
            ex.printStackTrace();  
        }  
    }  
}
