package com.cscie97.store.authentication;


import java.util.List;
import java.util.ArrayList;


public class Role extends Entitlement {

    private List<Permission> permissionList;


    public Role (String id, String name, String description) {
        super(id, name, description);
    }

    public void acceptVisitor(Visitor visitor) {

    };

    public void addPermission(Permission permission) {

    }

    public boolean hasResource(Permission permission, Resource resource) {

    };
}