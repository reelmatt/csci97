package com.cscie97.store.model;

public enum SensorType {
    CAMERA,
    MICROPHONE;

    public static boolean isSensor(String type) {
        for (SensorType sensor : SensorType.values()) {
            if (type.equals(sensor.toString().toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}