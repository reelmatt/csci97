package com.cscie97.store.authentication;

import java.util.List;
import java.util.ArrayList;

public interface Visitor {
    public void visitAuthenticationService(AuthenticationService authService);

    public void visitRole(Role role);

    public void visitPermission(Permission permission);

    public void visitResource(Resource resource);

    public void visitUser(User user);

    public boolean hasPermission();
}