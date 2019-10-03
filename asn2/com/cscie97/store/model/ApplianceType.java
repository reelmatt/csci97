package com.cscie97.store.model;

public enum ApplianceType {
    ROBOT,
    SPEAKER,
    TURNSTILE;

    public static ApplianceType getType(String choice) {
        for (ApplianceType appliance : ApplianceType.values()) {
            if (choice.equals(appliance.toString().toLowerCase())) {
                return appliance;
            }
        }

        return null;
    }

//    public static boolean isAppliance(String type) {
//        for (ApplianceType appliance : ApplianceType.values()) {
//            if (type.equals(appliance.toString().toLowerCase())) {
//                return true;
//            }
//        }
//
//        return false;
//    }
}