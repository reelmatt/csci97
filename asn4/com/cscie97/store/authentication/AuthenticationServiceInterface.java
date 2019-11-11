package com.cscie97.store.authentication;

/**
 *
 */
public interface AuthenticationServiceInterface extends Singleton {
    public void acceptVisitor();

    public void addEntitlementToUser(String userId, Entitlement entitlement);

    public void addPermissionToRole(Permission permission, Role role);

    public void addUserCredential(String userId, String credential);

    public AuthToken authenticateCredential(String user, String credential);

    public Permission definePermission(String id, String name, String description);

    public Resource defineResource(String id, String description);

    public ResourceRole defineResourceRole(String id, String name, String description, String resourceId);

    public Role defineRole(String id, String name, String description);

    public User defineUser(String id, String name);

    public void invalidateToken(AuthToken authToken);

    public User validateToken(AuthToken authToken);
}