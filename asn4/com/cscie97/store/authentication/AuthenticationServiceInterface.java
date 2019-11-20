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
     * Traversal logic is defined within the concrete Visitor objects. The Visitor
     * begins at the AuthenticationService when passed into this acceptVisitor()
     * method.
     *
     * @param   visitor                     The Visitor that will perform an action.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If the Visitor tries to access a method for
     *                                      which it does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public void acceptVisitor(Visitor visitor)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

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
    public void addEntitlementToUser(AuthToken token, String userId, String entitlementId)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

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
    public void addEntitlementToRole(AuthToken token, String entitlementId, String roleId)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    /**
     * Authenticate the credential with a User's stored credentials.
     *
     * The credential passed through is compared against all stored credentials
     * for the specified User: voice print, face print, and hashed password.
     *
     * @param   user        The User to compare credentials with.
     * @param   credential  The credential to check.
     * @return              True, if the credential matches a stored value.
     *                      False otherwise.
     */
    public boolean authenticateCredential(User user, String credential);

    /**
     * Create a root user for the Authentication Service.
     *
     * To address the Authentication Service bootstrapping problem, as outlined
     * in Piazza post @267, the CommandProcessor accepts a "create auth_root_user"
     * to initialize a root user with admin credentials to create additional objects.
     *
     * The root user is created with a specified ID and password provided with the
     * command, and a "user_admin" Permission is created and associated with the User.
     * Once created, the "rootUser" property in the Authentication Service is set to
     * true which does two things: 1) prevents another root user from being initialized;
     * and 2) requires the "user_admin" Permission to be provided with subsequent API calls
     * to the Authentication Service.
     *
     * @param   userId                      The userId to give the root user.
     * @param   password                    The root user password.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If a restricted method is called for which the
     *                                      AuthToken does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public void createRootUser(String userId, String password)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

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
    public String defineCredential(AuthToken token, String userId, String credentialType, String credential)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

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
    public Permission definePermission(AuthToken token, String id, String name, String description)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

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
    public Resource defineResource(AuthToken token, String id, String description)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

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
    public Role defineRole(AuthToken token, String id, String name, String description, String resource)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

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
    public User defineUser(AuthToken token, String id, String name)
            throws AuthenticationException,  AccessDeniedException, InvalidAuthTokenException;

    /**
     * Construct an inventory of all Authentication Service objects.
     *
     * Create an InventoryVisitor that will traverse the Authentication Service structure
     * to compile and print an inventory of all objects created within the Service.
     *
     * @param   authToken                   AuthToken to validate request.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If a restricted method is called for which the
     *                                      AuthToken does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public void getInventory(AuthToken authToken)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    /**
     * Check the AuthToken for a specified Permission.
     *
     * Create an AuthVisitor to look for the User associated with the provided AuthToken.
     * If found, the Entitlements for that User are searched to check access for the provided
     * Permission, and optionally, Resource.
     *
     * @param   token                       The token associated with the User requesting access.
     * @param   permissionId                The Permission to search for.
     * @param   resource                    Optional resource, to restrict access further.
     * @return
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If a restricted method is called for which the
     *                                      AuthToken does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public boolean hasPermission(AuthToken token, String permissionId, Resource resource)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    /**
     * Login the User with provided credentials.
     *
     * The provided credential is authenticated with credentials stored with the User.
     * If credentials are valid, a new AuthToken is created and associated with the User
     * to authentication API calls.
     *
     * @param   userId                      The User to log in.
     * @param   credential                  The credential to match with the User.
     * @return                              Upon validation, a new AuthToken associated with the
     *                                      User. If validation fails, an Exception is thrown.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If a restricted method is called for which the
     *                                      AuthToken does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public AuthToken login(String userId, String credential)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    /**
     * Invalidate the AuthToken provided.
     *
     * @param authToken The token to invalidate.
     */
    public void logout(AuthToken authToken);

    /**
     * Validate the provided AuthToken.
     *
     * Checks whether a token is provided, whether that token is active, and if it
     * was issued within the TOKEN_TIMEOUT duration.
     *
     * @param   authToken                   The AuthToken to validate.
     * @throws  InvalidAuthTokenException   If the token fails one of the validation
     *                                      checks, this Exception is thrown.
     */
    public void validateToken(AuthToken authToken)
            throws InvalidAuthTokenException;
}