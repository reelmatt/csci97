package com.cscie97.store.authentication;


public interface EntitlementInterface {
    public void acceptVisitor(Visitor visitor);

    public boolean hasResource(Permission permission, Resource resource);
}