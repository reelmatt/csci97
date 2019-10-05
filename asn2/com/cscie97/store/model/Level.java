package com.cscie97.store.model;

public enum Level {
    LOW,
    MEDIUM,
    HIGH;

    public static Level getType(String choice) {
        for (Level level : Level.values()) {
            if (choice.equals(level.toString().toLowerCase())) {
                return level;
            }
        }

        return null;
    }

}
