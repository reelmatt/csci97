package com.cscie97.store.authentication;


public class AuthVisitor implements Visitor {
    public void visitRole(Role role);

    public void visitPermission(Permission permission);

    public void visitResourceRole(ResourceRole role);

    public void visitUser(User user);
}