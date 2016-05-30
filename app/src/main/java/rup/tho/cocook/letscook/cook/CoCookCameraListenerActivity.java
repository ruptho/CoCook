package rup.tho.cocook.letscook.cook;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import rup.tho.cocook.R;

/**
 * Created by thorsten on 5/30/16.
 */
public abstract class CoCookCameraListenerActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, SensorEventListener {

    private static final double SENSOR_THRESHOLD = 0.2;

    protected enum CameraMessageType {
        RIGHT(1),
        LEFT(2);

        public final int value;

        CameraMessageType(int value) {
            this.value = value;
        }
    }

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 13;
    private static final String TAG = CoCookCameraListenerActivity.class.getSimpleName();
    private double mPrevAvgIntensity = 0;
    private long mLastSwipe = 0;
    private double mPrevLeftAvgIntensity;
    private double mPrevRightIntensity;

    protected boolean mHandGesturesOn = false;
    protected JavaCameraView mOpenCvCameraView;
    private boolean mFirstMessage = true;

    private SensorManager mSensorManager;
    private Sensor mRotation;

    // variables for detecting the picking up of the phone
    private int mSensorEventsReceived = 0;
    private float mLastX = 0;
    private float mCurrX = 0;
    private boolean mWaitCalibrate = true;

    // defines after how many events the acceleration is checked for a change
    private static int SENSOR_EVENT_COUNT = 10;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
                    mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
                    mOpenCvCameraView.setMaxFrameSize(640, 480);
                    mOpenCvCameraView.setCvCameraViewListener(CoCookCameraListenerActivity.this);
                    mOpenCvCameraView.SetCaptureFormat(CameraBridgeViewBase.GRAY);
                    mOpenCvCameraView.setAlpha(0);
                }
                break;
                default: {
                    mHandGesturesOn = false;
                    makeBiggerSnackbar(R.string.camera_listener_no_open_cv, R.string.dos_donts_okay);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        /*
        no usage for this atm, stay with current phone
        CameraManager manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            Log.d("ORIENTATION:", "sensorOrientation:" + sensorOrientation);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }*/

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.camera_view);
        mOpenCvCameraView.disableView();

        // initalise sensor stuff
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (mRotation == null) {
            Log.e(TAG, "Acceleration Sensor not available");
        }
    }

    final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (mFirstMessage) { // first message is a false positive.
                mFirstMessage = false;
                return;
            }

            Log.d("HANDLER", "got message " + msg.what);
            if (msg.what == CameraMessageType.RIGHT.value) {
                onSwipeRight();
            } else if (msg.what == CameraMessageType.LEFT.value) {
                onSwipeLeft();
            }
            super.handleMessage(msg);
        }
    };


    public abstract void onSwipeLeft();

    public abstract void onSwipeRight();

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    public Mat detectMovement2(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat gray = inputFrame.gray();

        double avgIntensity = Core.mean(gray).val[0];
        double avgRightIntensity = Core.mean(gray.rowRange(0, gray.rows() / 2 - 1)).val[0];
        double avgLeftIntensity = Core.mean(gray.rowRange(gray.rows() / 2, gray.rows() - 1)).val[0];
        //  Log.d("INTENSITY", "left:" + avgIntensity + " right:" + avgRightIntensity);
        //  Log.d("INTENSITY", "is:" + avgIntensity);

        if (mLastSwipe < System.currentTimeMillis() - 1000 && Math.abs(mPrevAvgIntensity - avgIntensity) > 10 && mPrevAvgIntensity > 0) {
            Log.d("CHANGE", "began swipe prev:" + mPrevAvgIntensity + " new:" + avgIntensity);
            Log.d("CHANGE", "prev left:" + mPrevLeftAvgIntensity + " right:" + mPrevRightIntensity);
            Log.d("CHANGE", "now left:" + avgLeftIntensity + " right:" + avgRightIntensity);
            Message msg = handler.obtainMessage();

            if (Math.abs(mPrevLeftAvgIntensity - avgLeftIntensity) / mPrevLeftAvgIntensity > Math.abs(mPrevRightIntensity - avgRightIntensity) / mPrevRightIntensity) {
                Log.d("CHANGE", "SWIPED Right");
                msg.what = CameraMessageType.RIGHT.value;
            } else {
                Log.d("CHANGE", "SWIPED Left");
                msg.what = CameraMessageType.LEFT.value;
            }
            handler.sendMessage(msg);
            mLastSwipe = System.currentTimeMillis();
        }

        mPrevLeftAvgIntensity = avgLeftIntensity;
        mPrevRightIntensity = avgRightIntensity;
        mPrevAvgIntensity = avgIntensity;

        return gray;
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        detectMovement2(inputFrame);
        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mHandGesturesOn && mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
            mSensorManager.unregisterListener(this);
        }
        mFirstMessage = true;
        // Unregister sensor listeners here when you pause your app, or you'll drain battery

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHandGesturesOn) {
            initOpenCV();
            mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void initOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mHandGesturesOn && mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        mFirstMessage = true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length == 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (!OpenCVLoader.initDebug()) {
                        Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
                        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
                    } else {
                        Log.d(TAG, "OpenCV library found inside package. Using it!");
                        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.e(TAG, "Permissions not granted");
                    mHandGesturesOn = false;
                    makeBiggerSnackbar(R.string.camera_listener_no_permissions, R.string.dos_donts_okay);
                }
                return;
            }
        }
    }

    public void disableHandGestures() {
        mHandGesturesOn = false;
        mOpenCvCameraView.disableView();
        mFirstMessage = true; // ignore first message again
        mSensorManager.unregisterListener(this);
        onSetUIGestureDisabled();
    }


    public void enableHandGestures() {
        mHandGesturesOn = true;
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        } else {
            // permissions already granted, try to init Opencv
            initOpenCV();
            mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);
            onSetUIGestureEnabled();
        }
    }

    protected abstract void onSetUIGestureEnabled();
    protected abstract void onSetUIGestureDisabled();

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float matrixR[] = new float[9];

            // 1 = -X, from -pi/2 to pi/2 (X = axis defined as vector product Y.Z, roughly points east)
            float orientationResult[] = new float[3];

            // let the first events pass, for calibration!
            if(mWaitCalibrate && ++mSensorEventsReceived == SENSOR_EVENT_COUNT) {
                mSensorEventsReceived = 0;
                mWaitCalibrate = false;
                return;
            }
            // use androids provided functions to calculate the orientation matrix as mentioned above
            SensorManager.getRotationMatrixFromVector(matrixR, event.values);
            SensorManager.getOrientation(matrixR, orientationResult);
            mLastX += orientationResult[1];

            if (++mSensorEventsReceived == SENSOR_EVENT_COUNT) {
                mSensorEventsReceived = 0;
                if(Math.abs(mLastX/SENSOR_EVENT_COUNT - mCurrX/SENSOR_EVENT_COUNT) > SENSOR_THRESHOLD) {
                    disableHandGestures();
                    makeBiggerSnackbar(R.string.camera_listener_phone_moved, R.string.dos_donts_okay);
                    onResume();
                }

                mCurrX = mLastX;
                mLastX = 0;
            }
        } // else if(otherSensorEvents)...
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not needed
    }

    private void makeBiggerSnackbar(int textResId, int actionTextResId)
    {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), textResId, Snackbar.LENGTH_LONG)
                .setAction(actionTextResId, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                }).setActionTextColor(ContextCompat.getColor(CoCookCameraListenerActivity.this, R.color.bt_pressed_color));
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);  // show multiple line
        snackbar.show();
    }
}
