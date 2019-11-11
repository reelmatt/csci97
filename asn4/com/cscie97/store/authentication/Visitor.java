package com.cscie97.store.authentication;

import java.util.List;
import java.util.ArrayList;

public interface Visitor {
    public void visitRole(Role role);

    public void visitPermission(Permission permission);

    public void visitResourceRole(ResourceRole role);

    public void visitUser(User user);
}