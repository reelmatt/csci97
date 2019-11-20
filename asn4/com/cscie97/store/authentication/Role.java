package com.cscie97.store.authentication;

import java.util.List;
import java.util.ArrayList;

/**
 * A Role in the Authentication Service.
 *
 * Roles are a composite of Entitlements and can contain additional Roles,
 * ResourceRoles, and Permissions.
 *
 * @author Matthew Thomas
 */
public class Role extends Entitlement {
    /** A list of Entitlements contained within the Role. */
    private List<Entitlement> entitlementList;

    /**
     * Role Constructor.
     *
     * @param id            The unique Role id.
     * @param name          The Role name.
     * @param description   The description of the Role.
     */
    public Role (String id, String name, String description) {
        super(id, name, description);
        this.entitlementList = new ArrayList<Entitlement>();
    }

    /**
     *
     * @param newEntitlement
     */
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

    /**
     * Returns the list of Entitlements the Role contains.
     */
    public List<Entitlement> getEntitlementList() {
        return entitlementList;
    }
}