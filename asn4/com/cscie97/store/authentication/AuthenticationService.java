package com.cscie97.store.authentication;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.cscie97.ledger.Ledger;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.Observer;
import com.cscie97.store.model.StoreModelServiceInterface;

import java.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * AuthenticationService - concrete class
 *
 * {@inheritDoc}
 *
 * @author Matthew Thomas
 */

public class AuthenticationService implements AuthenticationServiceInterface {
    private static AuthenticationService instance = null;

    private Map<String, Visitor> visitorMap;

    private Map<String, User> userMap;

    private Map<String, Permission> permissionMap;

    private Map<String, Role> roleMap;

    private Map<String, Resource> resourceMap;



    private AuthenticationService() {
        this.visitorMap = new HashMap<String, Visitor>();

        Visitor access = new AuthVisitor();
        Visitor inventory = new InventoryVisitor();
        this.visitorMap.put("access", access);
        this.visitorMap.put("inventory", inventory);

        this.userMap = new HashMap<String, User>();
        this.permissionMap = new HashMap<String, Permission>();
        this.roleMap = new HashMap<String, Role>();
        this.resourceMap = new HashMap<String, Resource>();

    }

    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    public User getUser(String userId) throws AuthenticationException {
        User user = this.userMap.get(userId);

        if (user == null) {
            throw new AuthenticationException(
                "get user",
                String.format("A user with id '%s' is not registered with the AuthenticationService.", userId)
            );
        }

        return user;
    }
    public void acceptVisitor() {
        return;
    };

    public void addEntitlementToUser(String userId, Entitlement entitlement) {
        return;
    };

    public void addPermissionToRole(Permission permission, Role role) {
        return;
    };

    public Credential addUserCredential(String userId, String credentialType, String credential) throws AuthenticationException{
        User user = getUser(userId);
        Credential newCredential = null;
        if (credentialType.equals("voice_print")) {
            String voiceCheck = "--voice:" + user.getName() + "--";

            if (credential.equals(voiceCheck)) {
                newCredential = new Credential(credential);
                user.setVoicePrint(newCredential);
            } else {
                throw new AuthenticationException(
                    "define credential",
                    "Voice print does not match format --voice:<username>--"
                );
            }
        } else if (credentialType.equals("face_print")) {
            String faceCheck = "--face:" + user.getName() + "--";

            if (credential.equals(faceCheck)) {
                newCredential = new Credential(credential);
                user.setFacePrint(newCredential);
            } else {
                throw new AuthenticationException(
                    "define credential",
                    "Face print does not match format --voice:<username>--"
                );
            }
        } else if (credentialType.equals("password")) {
            byte[] toHash = credential.getBytes();

            String hashedPassword = hashToString(toHash);

            newCredential = new Credential(hashedPassword);
            user.setPassword(newCredential);
        } else {
            throw new AuthenticationException(
                "define credential",
                "Unknown credential type"
            );
        }


        return newCredential;
    };

    /**
     * Generate a hash and convert to a String. Use SHA-256.
     *
     * Citations:
     * src: https://stackoverflow.com/questions/5531455/how-to-hash-some-string-with-sha256-in-java
     * src: https://www.baeldung.com/sha-256-hashing-java
     *
     * @param   toHash  The byte array to hash.
     * @return          String representation of hash.
     */
    private String hashToString(byte[] toHash) {
        // Initialize a SHA-256 hasher
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.toString());
        }

        // Hash the byte-ified Block object
        byte[] encodedHash = digest.digest(toHash);

        // Converting byte[] to String
        // src: https://howtodoinjava.com/array/convert-byte-array-string-vice-versa/
        return Base64.getEncoder().encodeToString(encodedHash);
    }

    public AuthToken authenticateCredential(String user, String credential) {
        return new AuthToken();
    };

    public Permission definePermission(String id, String name, String description) throws AuthenticationException {
        // All User information must be present
        if (id == null || name == null || description == null) {
            throw new AuthenticationException(
                    "define permission",
                    "Required Permission information is missing."
            );
        }

        // Check if User already exists
        if (this.permissionMap.containsKey(id)) {
            throw new AuthenticationException(
                    "define permission",
                    "A Permission with id " + id + " already exists."
            );
        }

        // Create new User
        Permission newPermission = new Permission(id, name, description);

        // Add to map
        this.permissionMap.put(id, newPermission);
        return newPermission;
    };

    public Resource defineResource(String id, String description) throws AuthenticationException {
        // All User information must be present
        if (id == null || description == null) {
            throw new AuthenticationException(
                "define resource",
                "Required Resource information is missing."
            );
        }

        // Check if User already exists
        if (this.resourceMap.containsKey(id)) {
            throw new AuthenticationException(
                "define resource",
                "A Resource with id " + id + " already exists."
            );
        }

        // Create new User
        Resource newResource = new Resource(id, description);

        // Add to map
        this.resourceMap.put(id, newResource);

        return newResource;
    };

    public ResourceRole defineResourceRole(String id, String name, String description, String resourceId) throws AuthenticationException {
        // All User information must be present
        if (id == null || name == null || description == null || resourceId == null) {
            throw new AuthenticationException(
                "define resource role",
                "Required Resource Role information is missing."
            );
        }

        // Check if User already exists
        if (this.roleMap.containsKey(id)) {
            throw new AuthenticationException(
                "define resource role",
                "A Role with id " + id + " already exists."
            );
        }

        Resource resource = this.resourceMap.get(resourceId);

        if (resource == null) {
            throw new AuthenticationException(
                "define resource role",
                "The Resource with id " + id + " does not exist."
            );
        }

        // Create new User
        ResourceRole newResourceRole = new ResourceRole(id, name, description, resource);

        // Add to map
        this.roleMap.put(id, newResourceRole);

        return newResourceRole;
    };

    public Role defineRole(String id, String name, String description) throws AuthenticationException {
        // All User information must be present
        if (id == null || name == null || description == null) {
            throw new AuthenticationException(
                    "define role",
                    "Required Role information is missing."
            );
        }

        // Check if User already exists
        if (this.roleMap.containsKey(id)) {
            throw new AuthenticationException(
                "define role",
                "A Role with id " + id + " already exists."
            );
        }

        // Create new User
        Role newRole = new Role(id, name, description);

        // Add to map
        this.roleMap.put(id, newRole);

        return newRole;
    };

    public User defineUser(String id, String name) throws AuthenticationException {
        System.out.println("AUTH: define user - " + id + " " + name);
        // All User information must be present
        if (id == null || name == null) {
            throw new AuthenticationException(
                "define user",
                "Required User information is missing."
            );
        }

        // Check if User already exists
        if (this.userMap.containsKey(id)) {
            throw new AuthenticationException(
                "define user",
                "A User with id " + id + " already exists."
            );
        }

        // Create new User
        User newUser = new User(id, name);

        // Add to map
        this.userMap.put(id, newUser);

        return newUser;
    };

    public void invalidateToken(AuthToken authToken) {
        if (authToken != null) {
            authToken.invalidate();
        }
        return;
    };

    public User validateToken(AuthToken authToken) {
        return this.userMap.get("userId");
    };
}