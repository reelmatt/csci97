package com.cscie97.store.authentication;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class AuthVisitor implements Visitor {
    private AuthToken token;

    private Resource resource;

    private Permission permission;

    private boolean foundAuthToken = false;

    private boolean hasPermission = false;


    public AuthVisitor(AuthToken token, Permission permission, Resource resource) {
        this.token = token;
        this.permission = permission;
        this.resource = resource;
    }

    public void visitAuthenticationService(AuthenticationService authService) {
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

    public void visitEntitlement(Entitlement entitlement) {
        if (entitlement instanceof Permission) {
            visitPermission((Permission) entitlement);
            return;
        }

        Role role = (Role) entitlement;
        for(Entitlement subEntitlement : role.getEntitlementList()) {
            visitEntitlement(subEntitlement);
        }

//        if (entitlement instanceof Role) {
//            visitRole((Role) entitlement);
//        }




        return;
    }

    public void visitRole(Role role) {
        for (Entitlement entitlement : role.getEntitlementList()) {
            if (entitlement instanceof Role) {
                visitRole((Role) entitlement);
            }

            if (entitlement instanceof Permission) {
                visitPermission((Permission) entitlement);
            }

            if (hasPermission) {
                break;
            }


            return;
        }
    };


    public void visitPermission(Permission permission) {
        if (this.permission.getId().equals(permission.getId())) {
            this.hasPermission = true;
        }

        return;
    };

    public void visitResource(Resource resource) {
        return;
    };

    public void visitUser(User user) {
        AuthToken userToken = user.getToken();

        if (this.token == null || userToken == null) {
            return;
        }
//        System.out.println("AUTH: visit user " + user.getId() + " has token " + userToken.getId());

        if (this.token.getId().equals(userToken.getId())) {
//            System.out.println("TOKEN belongs to user " + user.getId());
            this.foundAuthToken = true;
        } else {
            return;
        }

        for (Entitlement entitlement : user.getEntitlementList()) {
            visitEntitlement(entitlement);

            if (hasPermission) {
                break;
            }

//
//            if (entitlement.hasResource(this.permission, this.resource)) {
//                this.hasPermission = true;
//            }
        }


        return;
    };

    public boolean hasPermission() {
//        System.out.println("VISITOR: authToken == " + this.foundAuthToken);
        return hasPermission;
    }
}