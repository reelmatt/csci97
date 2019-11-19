package com.cscie97.store.authentication;

import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public class Role extends Entitlement {
    /** A list of Entitlements contained within the Role. */
    private List<Entitlement> entitlementList;

    /**
     * Role Constructor.
     *
     *
     *
     * @param id
     * @param name
     * @param description
     */
    public Role (String id, String name, String description) {
        super(id, name, description);
        this.entitlementList = new ArrayList<Entitlement>();
//        this.permissionList = new ArrayList<Permission>();
    }

    public void acceptVisitor(Visitor visitor) {

    };

    public void addEntitlement(Entitlement newEntitlement) {
        // If no entitlement, don't add
        if (newEntitlement == null) {
            return;
        }

        // Don't add a duplicate Permission
        for (Entitlement entitlement : getEntitlementList()) {
            if (entitlement.getId().equals(newEntitlement.getId())) {
                return;
            }
        }

        // Add Permission to the list
        this.entitlementList.add(newEntitlement);
        return;
    }

    public List<Entitlement> getEntitlementList() {
        return entitlementList;
    }

//    public List<Permission> getPermissionList() {
//        return permissionList;
//    }

    public boolean hasResource(Permission permission, Resource resource) {
        return false;
    };
}