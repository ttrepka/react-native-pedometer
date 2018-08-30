package com.pedometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class PedometerModule extends ReactContextBaseJavaModule implements SensorEventListener {
  private final ReactApplicationContext mReactContext;
  private final Sensor mStepCounter;
  private final Sensor mStepDetector;
  private final SensorManager mSensorManager;

  // steps taken when start() was called
  private int mStartSteps = 0;
  // steps taken since start() was called
  private int mCurrentSteps = 0;
  // steps taken since the beginning (multiple starts and stops could be called)
  private int mStepCount = 0;

  public PedometerModule(ReactApplicationContext reactContext) {
    super(reactContext);

    mReactContext = reactContext;
    mSensorManager = (SensorManager) reactContext.getSystemService(reactContext.SENSOR_SERVICE);

    mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    mStepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
  }

  @Override
  public String getName() {
    return "Pedometer";
  }

  @ReactMethod
  public void start() {
    mStartSteps = 0;
    mCurrentSteps = 0;
    // TODO don't use SENSOR_DELAY_FASTEST for production? perhaps it would take a
    // lot of battery
    mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_FASTEST);
    mSensorManager.registerListener(this, mStepDetector, SensorManager.SENSOR_DELAY_FASTEST);
  }

  @ReactMethod
  public void stop() {
    mStepCount += mCurrentSteps;
    mSensorManager.unregisterListener(this);
    mSensorManager.unregisterListener(this);
  }

  private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
  }

  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // not needed
  }

  public void onSensorChanged(SensorEvent event) {
    int type = event.sensor.getType();
    WritableMap params = Arguments.createMap();

    switch (type) {
    case Sensor.TYPE_STEP_COUNTER:
      int currentStepCount = (int) event.values[0];
      if (mStartSteps == 0) {
        mStartSteps = currentStepCount;
      }
      mCurrentSteps = currentStepCount - mStartSteps;
      params.putInt("takenSteps", mStepCount + mCurrentSteps);
      this.sendEvent(mReactContext, "stepCount", params);
      break;
    case Sensor.TYPE_STEP_DETECTOR:
      this.sendEvent(mReactContext, "stepDetected", params);
      break;
    }
  }
}
