package com.cscie97.store.model;

public enum ApplianceType {
    ROBOT,
    SPEAKER,
    TURNSTILE;

    /**
     * Helper method to convert String into Appliance type.
     * @param   type    The ApplianceType to look for.
     * @return          If the type matches an ApplianceType option, return
     *                  that value. Otherwise, null.
     */
    public static ApplianceType getType(String type) {
        for (ApplianceType appliance : ApplianceType.values()) {
            if (type.equals(appliance.toString().toLowerCase())) {
                return appliance;
            }
        }

        return null;
    }
}