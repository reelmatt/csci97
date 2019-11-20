package com.cscie97.store.authentication;

/**
 * A Visitor interface that follows the pattern described in the appendix of
 * Head First Design.
 *
 * The Visitor is constructed with knowledge of the Authentication Service and
 * contains methods to visit each of the various Authentication Service entities.
 * Traversal is performed by logic defined in concrete Visitor classes. Traversal
 * begins in each Visitor when passed through the acceptVisitor() method in the
 * Authentication Service.
 *
 * @author Matthew Thomas
 */
public interface Visitor {
    /**
     * Visit an Authentication Service. Performs action(s) defined in concrete class.
     *
     * @param   authService                 The AuthenticationService to visit.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If a restricted method is called for which the
     *                                      AuthToken does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public void visitAuthenticationService(AuthenticationService authService) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    /**
     * Visit an Entitlement. Performs action(s) defined in concrete class.
     *
     * @param entitlement   The Entitlement to visit.
     */
    public void visitEntitlement(Entitlement entitlement);

    /**
     * Visit a Permission. Performs action(s) defined in concrete class.
     *
     * @param permission   The Permission to visit.
     */
    public void visitPermission(Permission permission);

    /**
     * Visit a Resource. Performs action(s) defined in concrete class.
     *
     * @param resource   The Resource to visit.
     */
    public void visitResource(Resource resource);

    /**
     * Visit a Role. Performs action(s) defined in concrete class.
     *
     * @param role   The Role to visit.
     */
    public void visitRole(Role role);

    /**
     * Visit a User. Performs action(s) defined in concrete class.
     *
     * @param user   The User to visit.
     */
    public void visitUser(User user);

    /**
     * Returns whether the Permission was found or not. For AuthVisitor classes.
     */
    public boolean hasPermission();
}