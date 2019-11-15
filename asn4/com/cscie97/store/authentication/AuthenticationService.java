package com.cscie97.store.authentication;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.cscie97.ledger.Ledger;
import com.cscie97.store.model.Device;
import com.cscie97.store.model.Observer;
import com.cscie97.store.model.StoreModelServiceInterface;

import java.util.Iterator;
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

    private boolean rootUser = false;

    private Map<String, User> userMap;

    private Map<Integer, AuthToken> tokenMap;

    private Map<String, Permission> permissionMap;

    private Map<String, Role> roleMap;

    private Map<String, Resource> resourceMap;

    private static final String ADMIN_ACCESS = "user_admin";


    private AuthenticationService() {
        this.userMap = new HashMap<String, User>();
        this.permissionMap = new HashMap<String, Permission>();
        this.roleMap = new HashMap<String, Role>();
        this.resourceMap = new HashMap<String, Resource>();
        this.tokenMap = new HashMap<Integer, AuthToken>();

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
    public void acceptVisitor(Visitor visitor) {
        visitor.visitAuthenticationService(this);
        return;
    };

    public void addEntitlementToUser(String userId, Entitlement entitlement) throws AuthenticationException {
        User user = getUser(userId);
        user.addEntitlement(entitlement);

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

    public void createRootUser(String userId, String password) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        if (this.rootUser) {
            throw new AuthenticationException("create root user", "A root user has already been created.");
        }

        User root = defineUser(null, userId, "root");
        Credential credential = addUserCredential(userId, "password", password);
        this.rootUser = true;

//        Permission admin_permission = getPermission("user_admin");
        // Create new Permission
        Permission admin_permission = new Permission("user_admin", "User administrator", "Create, Update, Delete Users");

        // Add to map
        this.permissionMap.put("user_admin", admin_permission);
//        if (admin_permission == null) {
//            admin_permission = definePermission(null, "user_admin", "User administrator", "Create, Update, Delete Users");
//        }

        System.out.println("CREATE ROOT, perm == " + admin_permission);
        addEntitlementToUser(userId, admin_permission);

        return;
    }
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
        return new AuthToken("id");
    };

    public Permission definePermission(AuthToken token, String id, String name, String description) throws AuthenticationException, InvalidAuthTokenException, AccessDeniedException {
        validateToken(token);

        if (! hasPermission(token, getPermission(ADMIN_ACCESS), null)) {
            throw new AccessDeniedException("define permission", "no admin access");
        }

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

        // Create new Permission
        Permission newPermission = new Permission(id, name, description);

        // Add to map
        this.permissionMap.put(id, newPermission);
        return newPermission;
    };

    public Resource defineResource(AuthToken token, String id, String description) throws AuthenticationException {
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

    public ResourceRole defineResourceRole(AuthToken token, String id, String name, String description, String resourceId) throws AuthenticationException {
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

    public Role defineRole(AuthToken token, String id, String name, String description) throws AuthenticationException {
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

    public User defineUser(AuthToken token, String id, String name) throws AuthenticationException, InvalidAuthTokenException {



        // If a root user has been created, a token is required
        if (this.rootUser && token == null) {
            throw new InvalidAuthTokenException("define user", "No token provided.");
        }

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

    public AuthToken login(String userId, String credentialType, String credential) throws AuthenticationException, AccessDeniedException {
        User user = getUser(userId);

        if (credentialType.equals("password")) {
            Credential login = user.getLogin();

            byte[] toHash = credential.getBytes();

            String hashedPassword = hashToString(toHash);

            if (login.getValue().equals(hashedPassword)) {
                Integer tokenId = this.tokenMap.size() + 1;
                AuthToken newToken = new AuthToken(String.valueOf(tokenId));

                user.setToken(newToken);
                this.tokenMap.put(tokenId, newToken);

                return newToken;

            }
        }

        throw new AccessDeniedException("login", "password is not recognized for " + userId);
    }

    public void logout(AuthToken authToken) {
        if (authToken != null) {
            authToken.invalidate();
        }
        return;
    };

    public void getInventory(String authToken) throws AuthenticationException {
        Visitor inventory = new InventoryVisitor();
        acceptVisitor(inventory);


        return;
    }

    public Iterator<Map.Entry<String, User>> listUsers() {
        return userMap.entrySet().iterator();
    }

    public Iterator<Map.Entry<String, Resource>> listResources() {
        return resourceMap.entrySet().iterator();
    }

    public Permission getPermission(String id) {
        return permissionMap.get(id);
    }

    public Iterator<Map.Entry<String, Permission>> listPermissions() {
        return permissionMap.entrySet().iterator();
    }

    public Iterator<Map.Entry<String, Role>> listRoles() {
        return roleMap.entrySet().iterator();
    }

    public boolean hasPermission(AuthToken authToken, Permission permission, Resource resource) throws AuthenticationException, AccessDeniedException {
        if (authToken == null) {
            throw new AuthenticationException("has permission", "Missing auth token.");
        }

        if (permission == null) {
            throw new AuthenticationException("has permission", "Missing required permission.");
        }

        Visitor access = new AuthVisitor(authToken, permission, resource);
        acceptVisitor(access);

        return access.isHasPermission();
    }

    public User validateToken(AuthToken authToken) throws InvalidAuthTokenException {
        if ( authToken == null ) {
            throw new InvalidAuthTokenException("auth token", "none exists");
        }

        if ( ! authToken.isActive() ) {
            throw new InvalidAuthTokenException("auth token", "no longer active");
        }

        // check token hasn't timed out

        // find user






        return null;
//        return this.userMap.get("userId");
    };
}