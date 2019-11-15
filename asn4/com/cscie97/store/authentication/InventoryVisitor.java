package com.cscie97.store.authentication;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class InventoryVisitor implements Visitor {
    private String inventory;

    private Map<String, User> userMap;

    private Map<String, Permission> permissionMap;

    private Map<String, Role> roleMap;

    private Map<String, Resource> resourceMap;

    public InventoryVisitor() {
        this.inventory = "========================\n";
        this.inventory += "| Auth Service Inventory\n";
        this.inventory += "========================\n";
    }

    public void visitAuthenticationService(AuthenticationService authService) {

        // Get inventory of all Users
        addInventoryHeader("Resources");
        Iterator<Map.Entry<String, Resource>> resources = authService.listResources();
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
        return;
    };

    public void visitPermission(Permission permission) {
        this.inventory += permission + "\n";
        return;
    };

    public void visitResource(Resource resource) {
        this.inventory += resource + "\n";
        return;
    };

    public void visitUser(User user) {
        this.inventory += user + "\n";

        for (Entitlement entitlement : user.getEntitlementList()) {
            this.inventory += "\t" + entitlement + "\n";
        }
        return;
    };

    public boolean isHasPermission() {
        return false;
    }


    private void addInventoryHeader(String title) {
        this.inventory += "------------------------\n";
        this.inventory += title + "\n";
        this.inventory += "------------------------\n";
    }
}