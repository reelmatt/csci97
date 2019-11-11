package com.cscie97.store.authentication;


public class AuthToken {
    private String id;

    private String lastUsed;

    private boolean active;

    public AuthToken() {

    }

    public void invalidate() {
        this.active = false;
    }
}