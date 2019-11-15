package com.cscie97.store.authentication;
import java.util.Map;
import java.util.Iterator;
/**
 *
 */
public interface AuthenticationServiceInterface {
//    public AuthenticationServiceInterface getInstance();

    public void acceptVisitor(Visitor visitor);

    public void addEntitlementToUser(String userId, Entitlement entitlement) throws AuthenticationException;

    public void addPermissionToRole(Permission permission, Role role);

    public Credential addUserCredential(String userId, String credentialType, String credential) throws AuthenticationException;

    public AuthToken authenticateCredential(String user, String credential);

    public void createRootUser(String userId, String password) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    public Permission definePermission(AuthToken token, String id, String name, String description) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    public Resource defineResource(AuthToken token, String id, String description) throws AuthenticationException;

    public ResourceRole defineResourceRole(AuthToken token, String id, String name, String description, String resourceId) throws AuthenticationException;

    public Role defineRole(AuthToken token, String id, String name, String description) throws AuthenticationException;

    public User defineUser(AuthToken token, String id, String name) throws AuthenticationException, InvalidAuthTokenException;

    public void getInventory(String authToken) throws AuthenticationException;

    public boolean hasPermission(AuthToken token, Permission permission, Resource resource) throws AuthenticationException, AccessDeniedException;

    public AuthToken login(String userId, String credentialType, String credential) throws AuthenticationException, AccessDeniedException;

    public void logout(AuthToken authToken);


    public User validateToken(AuthToken authToken) throws InvalidAuthTokenException;
}