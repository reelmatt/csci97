package com.cscie97.store.authentication;

import java.util.Map;
import java.util.Iterator;

/**
 * InventoryVisitor - a concrete Visitor class.
 *
 * Traverses through all defined Authentication Service entities and construct
 * an inventory of these objects.
 *
 * @author Matthew Thomas
 */
public class InventoryVisitor implements Visitor {
    /** The inventory of Authentication Service objects. */
    private String inventory;

    /** The current indent level - used for formatting output. */
    private Integer indentLevel = 0;

    /** Token to make restricted API calls. */
    private AuthToken token = null;

    /**
     * InventoryVisitor Constructor.
     *
     * @param token The token for making restricted API calls.
     */
    public InventoryVisitor(AuthToken token) {
        this.token = token;

        // Start inventory with formatted header
        this.inventory = "========================\n";
        this.inventory += "| Auth Service Inventory\n";
        this.inventory += "========================\n";
    }

    /**
     * {@inheritDoc}
     *
     * Traverse the entire Authentication Service structure. Visit each of the
     * four maps that track defined entities within the Service. Visiting each
     * object may traverse further down the tree.
     */
    public void visitAuthenticationService(AuthenticationService authService) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {

        // Get inventory of all Users
        addInventoryHeader("Resources");
        Iterator<Map.Entry<String, Resource>> resources = authService.listResources(this.token);
        while( resources.hasNext() ) {
            Resource resource = resources.next().getValue();
            visitResource(resource);
        }

        // Get inventory of all Users
        addInventoryHeader("Users");
        Iterator<Map.Entry<String, User>> users = authService.listUsers();
        while( users.hasNext() ) {
            User user = users.next().getValue();
            visitUser(user);
        }

        // Get inventory of all Users
        addInventoryHeader("Roles");
        Iterator<Map.Entry<String, Role>> roles = authService.listRoles();
        while( roles.hasNext() ) {
            Role role = roles.next().getValue();
            visitRole(role);
        }

        // Get inventory of all Users
        addInventoryHeader("Permissions");
        Iterator<Map.Entry<String, Permission>> permissions = authService.listPermissions();
        while( permissions.hasNext() ) {
            Permission permission = permissions.next().getValue();
            visitPermission(permission);
        }


        System.out.println(this.inventory);
        return;
    };

    /**
     * {@inheritDoc}
     *
     * Roles can contain other Roles (or ResourceRoles) and Permissions. Iterate
     * through all objects in the list of Entitlements and visit each one.
     */
    public void visitRole(Role role) {
        this.inventory += indent() + role + "\n";

        indentLevel++;
        for (Entitlement entitlement : role.getEntitlementList()) {
            visitEntitlement(entitlement);
        }
        indentLevel--;

        return;
    };

    /**
     * {@inheritDoc}
     *
     * An Entitlement can either be a Permission (a leaf node) or a Role/ResourceRole.
     * If it is a Permission, visit it and then return back to the previous caller.
     * If it is a Role, visit the Role.
     */
    public void visitEntitlement(Entitlement entitlement) {
        if (entitlement instanceof Permission) {
            visitPermission((Permission) entitlement);
            return;
        }

        Role role = (Role) entitlement;
        visitRole(role);
        return;
    }

    /**
     * {@inheritDoc}
     *
     * Permissions are leaf nodes of the Entitlement composite pattern. All that
     * needs to happen is add the Permission to the inventory, and return.
     */
    public void visitPermission(Permission permission) {
        this.inventory += indent() + permission + "\n";
        return;
    };

    /**
     * {@inheritDoc}
     *
     * Resources are not part of the Entitlement composite pattern, so they do
     * not need to be traversed. Add to the inventory, and return.
     */
    public void visitResource(Resource resource) {
        this.inventory += indent() + resource + "\n";
        return;
    };

    /**
     * {@inheritDoc}
     *
     *
     */
    public void visitUser(User user) {
        this.inventory += indent() + user + "\n";
        this.inventory += indent() + "Face: " + user.getFacePrint() + "\n";
        this.inventory += indent() + "Voice: " + user.getVoicePrint() + "\n";
        this.inventory += indent() + "Password: " + user.getPassword() + "\n";


        indentLevel++;
        this.inventory += groupHeading("Entitlements");

        indentLevel++;
        for (Entitlement entitlement : user.getEntitlementList()) {
            visitEntitlement(entitlement);
        }
        indentLevel--;

        this.inventory += groupHeading("AuthToken");
        indentLevel++;
        this.inventory += indent() + user.getToken() + "\n";
        indentLevel--;

        indentLevel--;
        return;
    };

    /**
     * Helper method to format a heading to separate parts of the tree structure.
     */
    private String groupHeading(String title) {
        return (indent() + title + ":\n");
    }

    /**
     * Helper method to add indentation to inventory formatting.
     */
    private String indent() {
        String indent = "";

        for (int i = 0; i < this.indentLevel; i++) {
            indent += "\t";
        }

        return indent;
    }


    public boolean hasPermission() {
        return false;
    }


    private void addInventoryHeader(String title) {
        this.inventory += "------------------------\n";
        this.inventory += title + "\n";
        this.inventory += "------------------------\n";
    }
}