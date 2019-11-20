package com.cscie97.store.authentication;

/**
 * An Entitlement in the Authentication Service.
 *
 * The abstract Entitlement defines properties that are similar to all types of
 * Entitlement classes: Role, ResourceRole, and Permission objects.
 *
 * @author Matthew Thomas
 */
public abstract class Entitlement implements EntitlementInterface {
    /** The Entitlement's unique ID. */
    private String id;

    /** The name of the Entitlement. */
    private String name;

    /** The description of the Entitlement. */
    private String description;

    /**
     * Entitlement Constructor.
     *
     * @param id            The unique Entitlement id.
     * @param name          The Entitlement name.
     * @param description   The description of the Entitlement.
     */
    public Entitlement(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the Entitlement's id.
     */
    public String getId() {
        return id;
    }

    /**
     * Overrides the default toString method.
     */
    public String toString() {
        return this.id + ": " + this.name;
    }
}