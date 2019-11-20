package com.cscie97.store.authentication;

/**
 * A ResourceRole in the Authentication Service.
 *
 * ResourceRoles are a specific type of Role, that tie associated Entitlements
 * with a specific Resource.
 *
 * @author Matthew Thomas
 */
public class ResourceRole extends Role {
    /** The Resource associated with the Role. */
    private Resource resource;

    /**
     * ResourceRole Constructor.
     *
     * @param id            The unique ResourceRole id.
     * @param name          The ResourceRole name.
     * @param description   The description of the ResourceRole.
     * @param resource      The Resource that is referenced.
     */
    public ResourceRole (String id, String name, String description, Resource resource) {
        super(id, name, description);
        this.resource = resource;
    }

    /**
     * Returns the Resource associated with the Role.
     */
    public Resource getResource() {
        return resource;
    }
}