package com.cscie97.store.authentication;

/**
 * A Permission in the Authentication Service.
 *
 * Permissions represent logical permissions that grant access to a specific
 * resource or function.
 *
 * @author Matthew Thomas
 */
public class Permission extends Entitlement {
    /**
     * Permission Constructor.
     *
     * @param id            The unique Permission id.
     * @param name          The Permission name.
     * @param description   The description of the Permission.
     */
    public Permission (String id, String name, String description) {
        super(id, name, description);
    }
}