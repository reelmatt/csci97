package com.cscie97.store.authentication;

/**
 *
 */
public interface AuthenticationServiceInterface {
//    public AuthenticationServiceInterface getInstance();

    public void acceptVisitor();

    public void addEntitlementToUser(String userId, Entitlement entitlement);

    public void addPermissionToRole(Permission permission, Role role);

    public Credential addUserCredential(String userId, String credentialType, String credential) throws AuthenticationException;

    public AuthToken authenticateCredential(String user, String credential);

    public Permission definePermission(String id, String name, String description) throws AuthenticationException;

    public Resource defineResource(String id, String description) throws AuthenticationException;

    public ResourceRole defineResourceRole(String id, String name, String description, String resourceId) throws AuthenticationException;

    public Role defineRole(String id, String name, String description) throws AuthenticationException;

    public User defineUser(String id, String name) throws AuthenticationException;

    public void invalidateToken(AuthToken authToken);

    public User validateToken(AuthToken authToken);
}