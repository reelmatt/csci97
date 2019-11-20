package com.cscie97.store.authentication;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class InventoryVisitor implements Visitor {
    private String inventory;

    private Integer indentLevel = 0;

    private AuthToken token = null;

    public InventoryVisitor(AuthToken token) {
        this.token = token;

        this.inventory = "========================\n";
        this.inventory += "| Auth Service Inventory\n";
        this.inventory += "========================\n";
    }

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

    public void visitRole(Role role) {
        this.inventory += role + "\n";

        indentLevel++;
        for (Entitlement entitlement : role.getEntitlementList()) {
            visitEntitlement(entitlement);
        }
        indentLevel--;

        return;
    };

    public void visitEntitlement(Entitlement entitlement) {
        if (entitlement instanceof Permission) {
            visitPermission((Permission) entitlement);
            return;
        }

        Role role = (Role) entitlement;
        this.inventory += indent() + entitlement + "\n";

        indentLevel++;
        for (Entitlement subEntitlement : role.getEntitlementList()) {
            visitEntitlement(subEntitlement);
        }
        indentLevel--;
    }


    public void visitPermission(Permission permission) {
        this.inventory += indent() + permission + "\n";
        return;
    };

    public void visitResource(Resource resource) {
        this.inventory += indent() + resource + "\n";
        return;
    };

    public void visitUser(User user) {
        this.inventory += indent() + user + "\n";

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

    public String groupHeading(String title) {
        return (indent() + title + ":\n");
    }
    public String indent() {
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