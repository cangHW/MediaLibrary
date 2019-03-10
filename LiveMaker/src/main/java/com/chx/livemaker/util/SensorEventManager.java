package com.chx.livemaker.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 重力感应监听
 * Created by cangHX
 * on 2019/01/10  12:09
 */
public class SensorEventManager implements SensorEventListener {

    public interface onSensorEventChangedListener {
        void onSensorChanged(SensorEvent event);

        void onAccuracyChanged(Sensor sensor, int accuracy);
    }

    private static final LiveLogger mLogger = LiveLogger.create(SensorEventManager.class);
    private static final List<onSensorEventChangedListener> mListeners = new ArrayList<>();

    private SensorManager mManager;
    private Sensor mSensor;

    private static class Factory {
        private static final SensorEventManager mInstance = new SensorEventManager();
    }

    public static SensorEventManager getInstance() {
        return Factory.mInstance;
    }

    public void register(Context context) {
        if (mManager != null) {
            mLogger.dOnAll("SensorEventManager is loading");
            return;
        }
        mManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mManager == null) {
            mLogger.dOnAll("SensorEventManager create failed");
            return;
        }
        mSensor = mManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        boolean isSuccess = mManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (isSuccess) {
            mLogger.dOnAll("sensor register success");
        } else {
            mLogger.dOnAll("sensor register failed");
            try {
                mManager.unregisterListener(this, mSensor);
            } catch (Exception e) {
                e.toString();
            } finally {
                mManager = null;
            }
        }
    }

    public void unRegister() {
        if (mManager != null) {
            mManager.unregisterListener(this, mSensor);
            mManager = null;
            mLogger.dOnAll("sensor unregister success");
        }
    }

    public void addSensorEventChangedListener(onSensorEventChangedListener sensorEventChangedListener) {
        mListeners.add(sensorEventChangedListener);
    }

    public void removeSensorEventChangedListener(onSensorEventChangedListener sensorEventChangedListener) {
        mListeners.remove(sensorEventChangedListener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        for (onSensorEventChangedListener listener : mListeners) {
            listener.onSensorChanged(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        for (onSensorEventChangedListener listener : mListeners) {
            listener.onAccuracyChanged(sensor, accuracy);
        }
    }
}
