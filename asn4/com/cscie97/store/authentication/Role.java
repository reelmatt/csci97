package com.cscie97.store.authentication;


import java.util.List;
import java.util.ArrayList;


public class Role extends Entitlement {

    private List<Permission> permissionList;

    private List<Entitlement> entitlementList;


    public Role (String id, String name, String description) {
        super(id, name, description);
        this.entitlementList = new ArrayList<Entitlement>();
        this.permissionList = new ArrayList<Permission>();
    }

    public void acceptVisitor(Visitor visitor) {

    };

    public void addPermission(Permission permission) {

    }

    public List<Entitlement> getEntitlementList() {
        return entitlementList;
    }

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public boolean hasResource(Permission permission, Resource resource) {
        return false;
    };
}