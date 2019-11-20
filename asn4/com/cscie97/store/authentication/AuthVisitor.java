package com.cscie97.store.authentication;

import java.util.Map;
import java.util.Iterator;

/**
 * AuthVisitor - a concrete Visitor class.
 *
 * Traverses through all Authentication Service Users to find who is associated
 * with a given AuthToken. If found, the Visitor then traverses that User's
 * Entitlements to find if they have access for a given Permission.
 *
 * @author Matthew Thomas
 */
public class AuthVisitor implements Visitor {
    /** The token to check Permissions of. */
    private AuthToken token;

    /** Optionally, a Resource to restrict the search by. */
    private Resource resource;

    /** The Permission to search for. */
    private Permission permission;

    /** Indicator for whether AuthToken is found. */
    private boolean foundAuthToken = false;

    /** Indicator for whether the AuthToken contains access to the Permission. */
    private boolean hasPermission = false;

    /**
     * AuthVisitor Constructor.
     *
     * @param   token                       The token to check Permissions of.
     * @param   permission                  The Permission to search for.
     * @param   resource                    Optionally, a Resource to restrict search by.
     * @throws  AuthenticationException     If there are missing parameters, or a requested
     *                                      object does not exist in the AuthenticationService.
     * @throws  AccessDeniedException       If a restricted method is called for which the
     *                                      AuthToken does not have permission.
     * @throws  InvalidAuthTokenException   If the AuthToken provided is invalid.
     */
    public AuthVisitor(AuthToken token, Permission permission, Resource resource) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        this.token = token;
        this.permission = permission;
        this.resource = resource;
    }

    /**
     * {@inheritDoc}
     *
     * For checking access, the AuthVisitor only cares about visiting the list of
     * Users. If the AuthToken is found, the search will stop at that User. Otherwise,
     * all Users will be visited. If no AuthToken or Permission was provided to look for,
     * no Users are visited.
     */
    public void visitAuthenticationService(AuthenticationService authService) {
        if (this.token == null || this.permission == null ) {
            return;
        }

        Iterator<Map.Entry<String, User>> users = authService.listUsers();

        while(users.hasNext()) {
            User user = users.next().getValue();
            visitUser(user);

            if(foundAuthToken) {
                return;
            }
        }

        return;
    };

    /**
     * {@inheritDoc}
     *
     * An Entitlement can either by a Permission (a leaf node) or a Role/ResourceRole.
     * If it is a Permission, visit it and then return back to the previous caller.
     * If it is a role, visit the Role.
     */
    public void visitEntitlement(Entitlement entitlement) {
        // Check if Entitlement is a Permission first
        if (entitlement instanceof Permission) {
            visitPermission((Permission) entitlement);
            return;
        }

        // If not a Permission, it is a Role
        Role role = (Role) entitlement;
        visitRole(role);
        return;
    }

    /**
     * {@inheritDoc}
     *
     * First, the Visitor must check if the Role is a ResourceRole, should a
     * Resource restriction be specified. If it is not a ResourceRole, or it is
     * and the Resources match, iterate over the Role's Entitlement list.
     */
    public void visitRole(Role role) {
        // Check for Resource restriction first
        // Resource must be specified and Role must be a ResourceRole
        if (resource != null && role instanceof ResourceRole) {
            // Cast as role
            ResourceRole resRole = (ResourceRole) role;

            // Compare the IDs of the Resources
            Resource resRoleResource = resRole.getResource();
            if (! resRoleResource.getId().equals(this.resource.getId())) {
                // If they do not match, Visitor should not continue search
                return;
            }
        }

        // Iterate through all of the Role's Entitlements
        for (Entitlement entitlement : role.getEntitlementList()) {
            visitEntitlement(entitlement);

            // If Permission was found, end the search
            if (hasPermission) {
                break;
            }
        }

        return;
    };

    /**
     * {@inheritDoc}
     *
     * The Visitor checks if the current Permission matches the one it is searching
     * for. If it does, it sets the "hasPermission" property to true.
     */
    public void visitPermission(Permission permission) {
        if (this.permission.getId().equals(permission.getId())) {
            this.hasPermission = true;
        }

        return;
    };

    /**
     * {@inheritDoc}
     *
     * Not needed for the AuthVisitor.
     */
    public void visitResource(Resource resource) {
        return;
    };

    /**
     * {@inheritDoc}
     *
     * If the User does not have an associated AuthToken, the search returns to
     * continue with the next User. Otherwise, the token IDs are compared to find
     * a match. If matched, the "foundAuthToken" property will be set to true and
     * the Visitor will search the list of Entitlements for the Permission stopping
     * once the Permission has been found or no more Entitlements exist.
     */
    public void visitUser(User user) {
        AuthToken userToken = user.getToken();

        // Check if the User has an associated AuthToken
        if (userToken == null) {
            return;
        }

        // Check if the AuthToken IDs match
        if (this.token.getId().equals(userToken.getId())) {
            this.foundAuthToken = true;
        } else {
            return;
        }

        // If AuthToken was found, search this User's Entitlements
        for (Entitlement entitlement : user.getEntitlementList()) {
            visitEntitlement(entitlement);

            // If Permission has been found, return early
            if (hasPermission) {
                break;
            }
        }

        return;
    };

    /**
     * Returns whether the Permission was found or not.
     */
    public boolean hasPermission() {
        return hasPermission;
    }
}