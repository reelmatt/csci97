package com.cscie97.store.authentication;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.cscie97.ledger.Ledger;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.Observer;
import com.cscie97.store.model.StoreModelServiceInterface;

/**
 * AuthenticationService - concrete class
 *
 * {@inheritDoc}
 *
 * @author Matthew Thomas
 */

public class AuthenticationService implements AuthenticationServiceInterface, Singleton {
    private static AuthenticationService instance = null;

    private Map<String, Visitor> visitorMap;

    private Map<String, User> userMap;

    private Map<String, Permission> permissionMap;

    private Map<String, Role> roleMap;

    private Map<String, Resource> resourceMap;



    private AuthenticationService() {
        this.visitorList = new HashMap<String, Visitor>();

        Visitor access = new AuthVisitor();
        Visitor inventory = new InventoryVisitor();
        visitorList.put("access", access);
        visitorList.put("inventory", inventory);

        this.userMap = new HashMap<String, User>();
        this.permissionMap = new HashMap<String, Permission>();
        this.roleMap = new HashMap<String, Role>();
        this.resourceList = new HashMap<String, Resource>();

    }

    public static getInstance() {
        if (this.instance == null) {
            this.instance = new AuthenticationService();
        }
        return this.instance;
    }

    public void acceptVisitor() {
        return;
    };

    public void addEntitlementToUser(String userId, Entitlement entitlement) {
        return;
    };

    public void addPermissionToRole(Permission permission, Role role) {
        return;
    };

    public void addUserCredential(String userId, String credential) {
        return;
    };

    public AuthToken authenticateCredential(String user, String credential) {

    };

    public Permission definePermission(String id, String name, String description) {
        // All User information must be present
        if (id == null || name == null || description = null) {
            throw new AuthenticationExcpetion(
                    "define permission",
                    "Required Permission information is missing."
            );
        }

        // Check if User already exists
        if (this.permissionMapMap.containsKey(id)) {
            throw new AuthenticationException(
                    "define permission",
                    "A Permission with id " + id + " already exists."
            )
        }

        // Create new User
        Permission newPermission = new Permission(id, name, description);

        // Add to map
        this.permissionMap.put(id, newPermission);
        return newPermission;
    };

    public Resource defineResource(String id, String description) {
        // All User information must be present
        if (id == null || description = null) {
            throw new AuthenticationExcpetion(
                "define resource",
                "Required Resource information is missing."
            );
        }

        // Check if User already exists
        if (this.resourceMap.containsKey(id)) {
            throw new AuthenticationException(
                "define resource",
                "A Resource with id " + id + " already exists."
            )
        }

        // Create new User
        Resource newResource = new Resource(id, description);

        // Add to map
        this.resourceMap.put(id, newResource);

        return newResource;
    };

    public ResourceRole defineResourceRole(String id, String name, String description, String resourceId) {
        // All User information must be present
        if (id == null || name = null || description == null || resourceId == null) {
            throw new AuthenticationExcpetion(
                "define resource role",
                "Required Resource Role information is missing."
            );
        }

        // Check if User already exists
        if (this.roleMap.containsKey(id)) {
            throw new AuthenticationException(
                "define resource role",
                "A Role with id " + id + " already exists."
            )
        }

        Resource resource = this.resourceMap.get(resourceId);

        if (resource == null) {
            throw new AuthenticationException(
                "define resource role",
                "The Resource with id " + id + " does not exist."
            )
        }

        // Create new User
        ResourceRole newResourceRole = new ResourceRole(id, name, description, resource);

        // Add to map
        this.roleMap.put(id, newResourceRole);

        return newResourceRole;
    };

    public Role defineRole(String id, String name, String description) {
        // All User information must be present
        if (id == null || name = null || description == null) {
            throw new AuthenticationExcpetion(
                    "define role",
                    "Required Role information is missing."
            );
        }

        // Check if User already exists
        if (this.roleMap.containsKey(id)) {
            throw new AuthenticationException(
                "define role",
                "A Role with id " + id + " already exists."
            )
        }

        // Create new User
        Role newRole = new Role(id, name, description);

        // Add to map
        this.roleMapMap.put(id, newRole);

        return newRole;
    };

    public User defineUser(String id, String name) {
        // All User information must be present
        if (id == null || name = null) {
            throw new AuthenticationExcpetion(
                "define user",
                "Required User information is missing."
            );
        }

        // Check if User already exists
        if (this.userMap.containsKey(id)) {
            throw new AuthenticationException(
                "define user",
                "A User with id " + id + " already exists."
            )
        }

        // Create new User
        User newUser = new User(id, name);

        // Add to map
        this.userMap.put(id, newUser);

        return newUser;
    };

    public void invalidateToken(AuthToken authToken) {
        if (authToken != null) {
            authToken.invalidate();
        }
        return;
    };

    public User validateToken(AuthToken authToken) {
        return this.userMap.get("userId");
    };
}