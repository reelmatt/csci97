package com.cscie97.store.authentication;

import java.util.Map;
import java.util.Iterator;

/**
 * Interface for an Authentication Service.
 *
 * The interface defines basic functionality which inlcudes creating, updating,
 * and accessing the state of Authentication entities, including Permission,
 * Resource, Role, and User objects.
 *
 * When implemented, a singleton is instantiated to provide access to the
 * Authentication Service through a public API.
 */
public interface AuthenticationServiceInterface {
    /**
     * Accept a visitor to traverse the Authentication Service.
     *
     *
     *
     * @param   visitor                     The Visitor that will perform an action.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If the Visitor tries to access a method for
     *                                      which it does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public void acceptVisitor(Visitor visitor) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    /**
     * Add an Entitlement to a User.
     *
     * Checks that the the caller has ADMIN_ACCESS permission for modifying Authentication
     * Service objects. The Entitlement and User referenced by ids are retrieved, and if
     * they exist, the Entitlement is added to the User's list of Entitlements.
     *
     * @param   token                       AuthToken to validate request.
     * @param   userId                      The User to add the Entitlement to.
     * @param   entitlementId               The Entitlement to add to the User.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If a restricted method is called for which the
     *                                      AuthToken does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public void addEntitlementToUser(AuthToken token, String userId, String entitlementId) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    /**
     * Add an Entitlement to a Role.
     *
     * Checks that the the caller has ADMIN_ACCESS permission for modifying Authentication
     * Service objects. The Entitlement and Role referenced by ids are retrieved, and if
     * they exist, the Entitlement is added to the Role's list of Entitlements.
     *
     * @param   token                       AuthToken to validate request.
     * @param   entitlementId               The Entitlement to add to the User.
     * @param   roleId                      The Role to add the Entitlement to.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If a restricted method is called for which the
     *                                      AuthToken does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public void addEntitlementToRole(AuthToken token, String entitlementId, String roleId) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;



    public boolean authenticateCredential(User user, String credential);

    public void createRootUser(String userId, String password) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    /**
     * Create a new Credential.
     *
     * Checks that the caller has ADMIN_ACCESS permission for creating Authentication
     * Service objects. Also check all required parameters are included and that the Permission
     * does not already exist (unique ID).
     *
     * @param   token                       AuthToken to validate request.
     * @param   id                          The Permission id.
     * @param   name                        The name of the Permission.
     * @param   description                 The description for the Permission.
     * @return                              The Permission created by the Authenticaiton Service.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If a restricted method is called for which the
     *                                      AuthToken does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public String defineCredential(AuthToken token, String userId, String credentialType, String credential) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    /**
     * Create a new Permission.
     *
     * Checks that the caller has ADMIN_ACCESS permission for creating Authentication
     * Service objects. Also check all required parameters are included and that the Permission
     * does not already exist (unique ID).
     *
     * @param   token                       AuthToken to validate request.
     * @param   id                          The Permission id.
     * @param   name                        The name of the Permission.
     * @param   description                 The description for the Permission.
     * @return                              The Permission created by the Authenticaiton Service.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If a restricted method is called for which the
     *                                      AuthToken does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public Permission definePermission(AuthToken token, String id, String name, String description) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    /**
     * Create a new Resource.
     *
     * Checks that the caller has ADMIN_ACCESS permission for creating Authentication
     * Service objects. Also check all required parameters are included and that the Resource
     * does not already exist (unique ID).
     *
     * @param   token                       AuthToken to validate request.
     * @param   id                          The Resource id.
     * @param   name                        The name of the Resource.
     * @param   description                 The description for the Resource.
     * @return                              The Resource created by the Authenticaiton Service.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If a restricted method is called for which the
     *                                      AuthToken does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public Resource defineResource(AuthToken token, String id, String description) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    /**
     * Create a new Role.
     *
     * Checks that the caller has ADMIN_ACCESS permission for creating Authentication
     * Service objects. Also check all required parameters are included and that the Role
     * does not already exist (unique ID). If a Resource ID is provided, that ID is checked,
     * and if a Resource exists, a ResourceRole is created.
     *
     * @param   token                       AuthToken to validate request.
     * @param   id                          The Role id.
     * @param   name                        The name of the Role.
     * @param   description                 The description for the Role.
     * @param   resourceId                  The Resource id (provided for ResourceRole, null
     *                                      otherwise).
     * @return                              The Role created by the Authenticaiton Service.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If a restricted method is called for which the
     *                                      AuthToken does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public Role defineRole(AuthToken token, String id, String name, String description, String resource) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    /**
     * Create a new User.
     *
     * Checks that the caller has ADMIN_ACCESS permission for creating Authentication
     * Service objects. Also check all required parameters are included and that the User
     * does not already exist (unique ID).
     *
     * @param   token                       AuthToken to validate request.
     * @param   id                          The User id.
     * @param   name                        The name of the User.
     * @param   description                 The description for the User.
     * @return                              The User created by the Authenticaiton Service.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If a restricted method is called for which the
     *                                      AuthToken does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public User defineUser(AuthToken token, String id, String name) throws AuthenticationException,  AccessDeniedException, InvalidAuthTokenException;

    public void getInventory(AuthToken authToken) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    public boolean hasPermission(AuthToken token, String permissionId, Resource resource) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    public AuthToken login(String userId, String credential) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    public void logout(AuthToken authToken);

    public void validateToken(AuthToken authToken) throws InvalidAuthTokenException;
}