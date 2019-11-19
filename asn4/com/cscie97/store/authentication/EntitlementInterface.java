package com.cscie97.store.authentication;

/**
 *
 */
public interface EntitlementInterface {
    /**
     *
     * @param visitor
     */
    public void acceptVisitor(Visitor visitor);

    /**
     *
     * @param permission
     * @param resource
     * @return
     */
    public boolean hasResource(Permission permission, Resource resource);
}