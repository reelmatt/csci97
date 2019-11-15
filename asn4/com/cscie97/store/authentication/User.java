package com.cscie97.store.authentication;

import java.util.List;
import java.util.ArrayList;

public class User {

    private AuthToken token;

    private Credential password;

    private Credential facePrint;

    private Credential voicePrint;

    private List<Entitlement> entitlementList;

    private String id;

    private String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.entitlementList = new ArrayList<Entitlement>();
    }

    public Credential getFacePrint() {
        return this.facePrint;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Credential getLogin() {
        return this.password;
    }

    public Credential getVoicePrint() {
        return this.voicePrint;
    }

    public AuthToken getToken() {
        return this.token;
    }

    public void addEntitlement(Entitlement entitlement) {
        entitlementList.add(entitlement);
    }

    public void setFacePrint(Credential facePrint) {
        this.facePrint = facePrint;
    }

    public void setVoicePrint(Credential voicePrint) {
        this.voicePrint = voicePrint;
    }

    public void setPassword(Credential password) {
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