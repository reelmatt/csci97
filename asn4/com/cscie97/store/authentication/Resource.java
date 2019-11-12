package com.cscie97.store.authentication;


public class Resource {

    private String id;

    private String description;

    public Resource(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String toString() {
        return "Resource: " + this.id;
    }
}