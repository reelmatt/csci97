package com.cscie97.store.authentication;

/**
 * The main interface that is used to implement the Composite Pattern.
 *
 * Entitlements are created by the Authentication Service, with a Role being a
 * composite of Permissions. Permissions are the leaf nodes of the system.
 * ResourceRoles are a specific kind of Role that is associated with a Resource.
 *
 * No methods are defined by the Entitlement interface, but to adhere to the
 * Dependency Inversion Principle, an interface is defined to allow for easier
 * expandability in the future.
 *
 * @author Matthew Thomas
 */
public interface EntitlementInterface { }