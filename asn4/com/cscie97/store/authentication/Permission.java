package com.cscie97.store.authentication;


public class Permission extends Entitlement {

    public Permission (String id, String name, String description) {
        super(id, name, description);
    }

    public void acceptVisitor(Visitor visitor) {

    };

    public boolean hasResource(Permission permission, Resource resource) {

    };
}