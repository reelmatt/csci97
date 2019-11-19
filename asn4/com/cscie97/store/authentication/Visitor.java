package com.cscie97.store.authentication;

/**
 *
 */
public interface Visitor {
    /**
     *
     * @param authService
     * @throws AuthenticationException
     * @throws AccessDeniedException
     * @throws InvalidAuthTokenException
     */
    public void visitAuthenticationService(AuthenticationService authService) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException;

    public void visitEntitlement(Entitlement entitlement);

    public void visitPermission(Permission permission);

    public void visitResource(Resource resource);

    public void visitRole(Role role);

    public void visitUser(User user);

    public boolean hasPermission();
}