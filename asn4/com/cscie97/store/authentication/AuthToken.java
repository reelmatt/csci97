package com.cscie97.store.authentication;


public class AuthToken {
    private String id;

    private String lastUsed;

    private boolean active;

    public AuthToken(String id) {
        this.id = id;
        this.lastUsed = "to come";
        this.active = true;
    }

    public void invalidate() {
        this.active = false;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return "token" + id;
    }
}