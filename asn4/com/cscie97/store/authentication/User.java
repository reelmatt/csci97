package com.cscie97.store.authentication;

import java.util.List;
import java.util.ArrayList;

/**
 * A User in the Authentication Service.
 *
 * A User is a representation of a person and can be associated with an
 * AuthToken and a list of Entitlements allowing access to restrcited API
 * methods. In relation to the Store 24X7 System, Users correspond to Customers
 * in the Store Model Service and an Account in the Ledger Service.
 *
 * @author Matthew Thomas
 */
public class User {
    /** The User identifier. */
    private String id;

    /** The name of the User. */
    private String name;

    /** The AuthToken associated with the User. */
    private AuthToken token;

    /** A hashed password credential. */
    private String password;

    /** A face print credential, matching the form --face:<username>--. */
    private String facePrint;

    /** A voice print credential, matching the form --voice:<username>--. */
    private String voicePrint;

    /** A list of Entitlements associated with the User. */
    private List<Entitlement> entitlementList;

    /**
     * User Constructor.
     *
     * @param id    The User's id.
     * @param name  The User's name.
     */
    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.entitlementList = new ArrayList<Entitlement>();
    }

    /**
     * Returns the User ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the User's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the User's hashed password..
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the User's Face Print.
     */
    public String getFacePrint() {
        return this.facePrint;
    }

    /**
     * Returns the User's Voice Print.
     */
    public String getVoicePrint() {
        return this.voicePrint;
    }

    /**
     * Returns the User's AuthToken.
     */
    public AuthToken getToken() {
        return this.token;
    }

    /**
     * Add Entitlement to User.
     *
     * @param newEntitlement    The Entitlement to grant the User.
     */
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

    /**
     * Set the User's Face Print.
     *
     * @param facePrint     The User's face print.
     */
    public void setFacePrint(String facePrint) {
        this.facePrint = facePrint;
    }

    /**
     * Set the User's Voice Print.
     *
     * @param facePrint     The User's voice print.
     */
    public void setVoicePrint(String voicePrint) {
        this.voicePrint = voicePrint;
    }

    /**
     * Set the User's password.
     *
     * @param password     The User's hashed password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the list of Entitlements associated with the User.
     */
    public List<Entitlement> getEntitlementList() {
        return entitlementList;
    }

    /**
     * Set the User's AuthToken.
     *
     * @param token The AuthToken assigned to the User.
     */
    public void setToken(AuthToken token) {
        this.token = token;
    }

    /**
     * Overrides the default toString method.
     */
    public String toString() {
        return this.id + ": " + this.name;
    }
}