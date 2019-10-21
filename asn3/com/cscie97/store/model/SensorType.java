package com.cscie97.store.model;

public enum SensorType {
    CAMERA,
    MICROPHONE;

    /**
     * Helper method to convert String into Sensor type.
     * @param   type    The SensorType to look for.
     * @return          If the type matches an SensorType option, return
     *                  that value. Otherwise, null.
     */
    public static SensorType getType(String type) {
        for (SensorType sensor : SensorType.values()) {
            if (type.equals(sensor.toString().toLowerCase())) {
                return sensor;
            }
        }

        return null;
    }
}