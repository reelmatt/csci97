package com.cscie97.store.authentication;
import java.util.Map;
import java.util.Iterator;
/**
 *
 */
public interface AuthenticationServiceInterface {
//    public AuthenticationServiceInterface getInstance();

    public void acceptVisitor(Visitor visitor);

    public void addEntitlementToUser(AuthToken token, String userId, String entitlementId) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    public void addEntitlementToRole(AuthToken token, String entitlementId, String roleId) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    public String addUserCredential(AuthToken token, String userId, String credentialType, String credential) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    public boolean authenticateCredential(User user, String credential);

    public void createRootUser(String userId, String password) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    public Permission definePermission(AuthToken token, String id, String name, String description) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    public Resource defineResource(AuthToken token, String id, String description) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

//    public ResourceRole defineResourceRole(AuthToken token, String id, String name, String description, String resourceId) throws AuthenticationException;

    public Role defineRole(AuthToken token, String id, String name, String description, String resource) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    public User defineUser(AuthToken token, String id, String name) throws AuthenticationException,  AccessDeniedException, InvalidAuthTokenException;

    public void getInventory(AuthToken authToken) throws AuthenticationException;

    public boolean hasPermission(AuthToken token, String permissionId, Resource resource) throws AuthenticationException, InvalidAuthTokenException;

    public AuthToken login(String userId, String credential) throws AuthenticationException, AccessDeniedException;

    public void logout(AuthToken authToken);


    public User validateToken(AuthToken authToken) throws InvalidAuthTokenException;
}