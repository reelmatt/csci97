package com.cscie97.store.authentication;

import java.util.List;
import java.util.ArrayList;

public class User {

    private AuthToken token;

    private String password;

    private String facePrint;

    private String voicePrint;

    private List<Entitlement> entitlementList;

    private String id;

    private String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.entitlementList = new ArrayList<Entitlement>();
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return this.password;
    }

    public String getFacePrint() {
        return this.facePrint;
    }
    public String getVoicePrint() {
        return this.voicePrint;
    }

    public AuthToken getToken() {
        return this.token;
    }

    public void addEntitlement(Entitlement newEntitlement) {
        // If no permission, don't add
        if (newEntitlement == null) {
            return;
        }

        // Don't add a duplicate Entitlement
        for (Entitlement entitlement : getEntitlementList()) {
            if (entitlement.getId().equals(newEntitlement.getId())) {
                return;
            }
        }

        // Add Entitlement to the list
        this.entitlementList.add(newEntitlement);
        return;
    }

    public void setFacePrint(String facePrint) {
        this.facePrint = facePrint;
    }

    public void setVoicePrint(String voicePrint) {
        this.voicePrint = voicePrint;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Entitlement> getEntitlementList() {
        return entitlementList;
    }

    public void setToken(AuthToken token) {
        this.token = token;
    }

    public String toString() {
        return this.id + ": " + this.name;
    }
}