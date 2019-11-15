package com.cscie97.store.authentication;


public abstract class Entitlement implements EntitlementInterface {
    private String id;

    private String name;

    private String description;

    public Entitlement(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void acceptVisitor(Visitor visitor) {
        return;
    };

    public boolean hasResource(Permission permission, Resource resource) {
        return false;
    };

    public String getId() {
        return id;
    }

    public String toString() {
        return this.id + ": " + this.name;
    }
}