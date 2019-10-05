package com.cscie97.store.model;

public enum Temperature {
    FROZEN,
    REFRIGERATED,
    AMBIENT,
    WARM,
    HOT;

    public static Temperature getType(String choice) {
        for (Temperature temperature : Temperature.values()) {
            if (choice.equals(temperature.toString().toLowerCase())) {
                return temperature;
            }
        }

        return null;
    }
}