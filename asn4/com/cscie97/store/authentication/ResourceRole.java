package com.cscie97.store.authentication;


public class ResourceRole extends Role {

    private Resource resource;

    public ResourceRole (String id, String name, String description, Resource resource) {
        super(id, name, description);
        this.resource = resource;
    }

    public void acceptVisitor(Visitor visitor) {

    };

    public boolean hasResource(Permission permission, Resource resource) {
        return false;
    };
}