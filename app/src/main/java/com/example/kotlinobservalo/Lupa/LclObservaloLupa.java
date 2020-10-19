package com.example.kotlinobservalo.Lupa;

import android.app.ActionBar;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kotlinobservalo.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class LclObservaloLupa extends AppCompatActivity implements View.OnClickListener {

    private static  final int FOCUS_AREA_SIZE= 300;

    public boolean pausa = false;
    public boolean guardar = false;

    private Camera mCamera;
    private CameraPreview mPreview;

    private boolean linterna;

    private byte[] foto;

    private int zoom;
    private int maxZoom;
    private byte hayZoom = 0;

    byte efectoActual;
    boolean negPerm;
    boolean monPerm;

    //Void para sacar fotos:
    private PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            foto = data;
            mCamera.stopPreview();
            pausa = true;
        }
    };

    private void guardarArchivo(byte[] photo){
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            Log.d("minga", "Error creating media file, check storage permissions");
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(photo);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("minga", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("minga", "Error accessing file: " + e.getMessage());
        }
    }

    Camera.Parameters params;
    //definir los botones:
    Button pausarBtn;
    Button guardarBtn;
    Button masBtn;
    Button menosBtn;
    Button efectosBtn;
    Button flashBtn;
    ////////////////////////////////////////////////////////ON CREATE////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.lupa_activity_main);

        //definir los botones:
        Button pausarBtn = (Button) findViewById(R.id.button_capture);
        Button guardarBtn = (Button) findViewById(R.id.guardar);
        Button masBtn = (Button) findViewById(R.id.mas);
        Button menosBtn = (Button) findViewById(R.id.menos);
        Button flashBtn = (Button) findViewById(R.id.flash);
        Button efectosBtn = (Button) findViewById(R.id.efectos);
        pausarBtn.setOnClickListener(this);
        guardarBtn.setOnClickListener(this);
        masBtn.setOnClickListener(this);
        menosBtn.setOnClickListener(this);
        flashBtn.setOnClickListener(this);
        efectosBtn.setOnClickListener(this);

        //crear la camara:
        mCamera = getCameraInstance();

        //crear la preview view de la camara y setearla como parte de la Act
        mPreview = new CameraPreview(this, mCamera);

        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

        preview.addView(mPreview);

        params = mCamera.getParameters();

        //activar el botón de flash si se puede:
        List<String> flashModes = params.getSupportedFlashModes();
        if (flashModes != null) {
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                flashBtn.setVisibility(View.VISIBLE);
            } else {
                flashBtn.setVisibility(View.GONE);
            }
        }

        //hacer focus:
        mPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    focusOnTouch(event);
                }
                return true;
            }
        });

        Log.d("minga", "zoom0");
        //activar el botón de zoom si se puede:
        if (params.isZoomSupported() && params.isSmoothZoomSupported()) {
            //most phones
            maxZoom = params.getMaxZoom();
            hayZoom = 1;
        } else if (params.isZoomSupported() && !params.isSmoothZoomSupported()){
            //stupid HTC phones
            hayZoom = 2;
            maxZoom = params.getMaxZoom();
        }else{
            //no zoom on phone
            hayZoom = 0;
            masBtn.setVisibility(View.GONE);
            menosBtn.setVisibility(View.GONE);
        }

        mCamera.setParameters(params);

    }

    //botones:
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            //El boton de pausa:
            case R.id.button_capture:
            // get an image from the camera
                if (pausa == false) {
                    mCamera.takePicture(null, null, mPicture);
                } else if (pausa == true) {
                    mCamera.startPreview();
                    pausa = false;
                }
                break;
            //El boton de guardar img:
            case R.id.guardar:
                if (pausa == true) {
                    guardar = true;
                    guardarArchivo(foto);
                    guardar = false;
                }
                break;
            //El boton del flash:
            case R.id.flash:
                if (mCamera != null) {
                    try {
                        params = mCamera.getParameters();
                        mCamera.setParameters(params);
                        if(linterna == false) {
                            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            mCamera.setParameters(params);
                            linterna = true;
                        }else{
                            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            mCamera.setParameters(params);
                            linterna = false;
                        }
                    } catch (Exception e) {
                    }
                }
                break;
            //El boton del +:
            case R.id.mas:
                Log.d("minga", "zoom2");
                if (mCamera != null) {
                    try {
                        if (zoom <= maxZoom-10) {
                            switch (hayZoom) {
                                case 0:
                                    break;
                                case 1:
                                    zoom+=10;
                                    mCamera.startSmoothZoom(zoom);
                                    break;
                                case 2:
                                    zoom+=10;
                                    params.setZoom(zoom);
                                    break;
                            }
                            mCamera.setParameters(params);
                        }
                    } catch (Exception e) {
                    }
                }
                break;
            //El boton del -:
            case R.id.menos:
                if (mCamera != null) {
                    try {
                        if (zoom >= 10) {
                            switch (hayZoom) {
                                case 0:
                                    break;
                                case 1:
                                    zoom-=10;
                                    mCamera.startSmoothZoom(zoom);
                                    break;
                                case 2:
                                    zoom-=10;
                                    params.setZoom(zoom);
                                    break;
                            }
                            mCamera.setParameters(params);
                        }
                    } catch (Exception e) {
                    }
                }
                break;
            //El boton de efectos:
            case R.id.efectos:
                if (mCamera != null) {
                    try {
                        switch(efectoActual){
                            case 0:
                                params.setColorEffect(Camera.Parameters.EFFECT_NONE);
                                mCamera.setParameters(params);
                                efectoActual++;
                                break;
                            case 1:
                                params.setColorEffect(Camera.Parameters.EFFECT_AQUA);
                                mCamera.setParameters(params);
                                efectoActual++;
                                break;
                            case 2:
                                params.setColorEffect(Camera.Parameters.EFFECT_BLACKBOARD);
                                mCamera.setParameters(params);
                                efectoActual++;
                                break;
                            case 3:
                                params.setColorEffect(Camera.Parameters.EFFECT_MONO);
                                mCamera.setParameters(params);
                                efectoActual++;
                                break;
                            case 4:
                                params.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
                                mCamera.setParameters(params);
                                efectoActual++;
                                break;
                            case 5:
                                params.setColorEffect(Camera.Parameters.EFFECT_POSTERIZE);
                                mCamera.setParameters(params);
                                efectoActual++;
                                break;
                            case 6:
                                params.setColorEffect(Camera.Parameters.EFFECT_SEPIA);
                                mCamera.setParameters(params);
                                efectoActual=0;
                                break;
                            case 7:
                                params.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
                                mCamera.setParameters(params);
                                efectoActual++;
                                break;
                            case 8:
                                params.setColorEffect(Camera.Parameters.EFFECT_WHITEBOARD);
                                mCamera.setParameters(params);
                                efectoActual++;
                                break;
                        }
                        }
                    catch (Exception e) {
                    }
                }
                break;
        }
    }

    //Void para inicializar la cámara:
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    //void para hacer zoom
    private void focusOnTouch(MotionEvent event) {
        if (mCamera != null ) {

            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0){
                Log.i("minga","fancy !");
                Rect rect = calculateFocusArea(event.getX(), event.getY());

                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);

                mCamera.setParameters(parameters);
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            }else {
                mCamera.autoFocus(mAutoFocusTakePictureCallback);
            }
        }
    }
    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / mPreview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / mPreview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }
    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper)+focusAreaSize/2>1000){
            if (touchCoordinateInCameraReper>0){
                result = 1000 - focusAreaSize/2;
            } else {
                result = -1000 + focusAreaSize/2;
            }
        } else{
            result = touchCoordinateInCameraReper - focusAreaSize/2;
        }
        return result;
    }
    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
                Log.i("tap_to_focus","success!");
            } else {
                // do something...
                Log.i("tap_to_focus","fail!");
            }
        }
    };

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Lupa");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("minga", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

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

}
