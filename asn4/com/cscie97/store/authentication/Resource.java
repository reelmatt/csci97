package com.cscie97.store.authentication;

/**
 * A Resource in the Authentication Service.
 *
 * Resources represent physical entities in a system. In the context of the
 * Store 24X7 System, physical entities can include Store, Sensors, and
 * Appliances.
 *
 * @author Matthew Thomas
 */
public class Resource {
    /** The Resource identifier. */
    private String id;

    /** The Resource description. */
    private String description;

    /**
     * Resource Constructor.
     *
     * @param id            The Resource id.
     * @param description   The description of the Resource.
     */
    public Resource(String id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Overrides the default toString method.
     */
    public String toString() {
        return "Resource: " + this.id;
    }
}