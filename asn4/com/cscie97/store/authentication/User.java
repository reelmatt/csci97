package com.cscie97.store.authentication;

import java.util.List;
import java.util.ArrayList;

public class User {

    private AuthToken token;

    private Credential login;

    private Credential facePrint;

    private Credential voicePrint;

    private List<Entitlement> entitlementList;

    private String id;

    private String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Credential getFacePrint() {
        return this.facePrint;
    }

    public Credential getLogin() {
        return this.login;
    }

    public Credential getVoicePrint() {
        return this.voicePrint;
    }

    public AuthToken getToken() {
        return this.token;
    }

    public void setFacePrint(Credential facePrint) {
        this.facePrint = facePrint;
    }

    public void setVoicePrint(Credential voicePrint) {
        this.voicePrint = voicePrint;
    }

    public void setLogin(Credential login) {
        this.login = login;
    }
}