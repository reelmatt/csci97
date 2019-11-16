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
    /** Singleton instance of the Authentication Service. */
    private static AuthenticationService instance = null;

    /** Boolean to indicated whether root user has been created. */
    private boolean rootUser = false;

    /** A Map of all Users in the Authentication Service. */
    private Map<String, User> userMap;

    /** A Map of all AuthTokens in the Authentication Service. */
    private Map<Integer, AuthToken> tokenMap;

    /** A Map of all Permissions in the Authentication Service. */
    private Map<String, Permission> permissionMap;

    /** A Map of all Roles in the Authentication Service. */
    private Map<String, Role> roleMap;

    /** A Map of all Resources in the Authentication Service. */
    private Map<String, Resource> resourceMap;

    /** ID for admin permissions to call Authentication Service. */
    private static final String ADMIN_ACCESS = "user_admin";

    /**
     * AuthenticationService Constructor.
     */
    private AuthenticationService() {
        this.userMap = new HashMap<String, User>();
        this.permissionMap = new HashMap<String, Permission>();
        this.roleMap = new HashMap<String, Role>();
        this.resourceMap = new HashMap<String, Resource>();
        this.tokenMap = new HashMap<Integer, AuthToken>();

    }

    /**
     * Returns the instance of the AuthenticationService.
     *
     * If an instance has not been instantiate, create a new one. Otherwise,
     * return the instance already created. This follows the Singleton pattern
     * from Head First Design, Chapter 5.
     */
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

    /**
     * Add Entitlement to User.
     *
     *
     *
     * @param userId
     * @param entitlementId
     * @throws AuthenticationException
     */
    public void addEntitlementToUser(AuthToken token, String userId, String entitlementId) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        // Check the caller has ADMIN_ACCESS permission
        if (! hasPermission(token, ADMIN_ACCESS, null)) {
            throw new AccessDeniedException("add entitlement to user", "no admin access");
        }


        User user = getUser(userId);
        Entitlement entitlement = getEntitlement(entitlementId);

        if (user == null) {
            throw new AuthenticationException(
                "add entitlement to User",
                "User " + userId + " does not exist."
            );
        }
        if (entitlement == null) {
            throw new AuthenticationException(
                "add entitlement to User",
                "Entitlement " + entitlementId + " does not exist."
            );
        }

        user.addEntitlement(entitlement);
        return;
    };

    /**
     * Add Entitlement to Role.
     *
     *
     *
     * @param entitlementId
     * @param roleId
     */
    public void addEntitlementToRole(AuthToken token, String entitlementId, String roleId) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        // Check the caller has ADMIN_ACCESS permission
        if (! hasPermission(token, ADMIN_ACCESS, null)) {
            throw new AccessDeniedException("add entitlement to role", "no admin access");
        }

        Entitlement entitlement = getEntitlement(entitlementId);
        Role role = getRole(roleId);

        if (entitlement == null) {
            throw new AuthenticationException(
                "add entitlement to role",
                "Entitlement " + entitlementId + " does not exist."
            );
        }
        if (role == null) {
            throw new AuthenticationException(
                "add entitlement to role",
                "Role " + roleId + " does not exist."
            );
        }

        role.addEntitlement(entitlement);
        return;
    };


    public String addUserCredential(AuthToken token, String userId, String credentialType, String credential) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        // Check the caller has ADMIN_ACCESS permission
        if (! hasPermission(token, ADMIN_ACCESS, null)) {
            throw new AccessDeniedException("add user credential", "no admin access");
        }

        User user = getUser(userId);

        // Check voice print
        if (credentialType.equals("voice_print")) {

            String voiceCheck = "--voice:" + user.getName() + "--";

            if (! credential.equals(voiceCheck)) {
                throw new AuthenticationException(
                    "define credential",
                    "Voice print does not match format --voice:<username>--"
                );
            }

            user.setVoicePrint(credential);
            return credential;
        } else if (credentialType.equals("face_print")) {
            // Check face print
            String faceCheck = "--face:" + user.getName() + "--";

            if (! credential.equals(faceCheck)) {
                throw new AuthenticationException(
                    "define credential",
                    "Face print does not match format --voice:<username>--"
                );
            }

            user.setFacePrint(credential);
            return credential;
        } else if (credentialType.equals("password")) {
            // A password Credential, hash it before storing
            String hashedPassword = hashToString(credential.getBytes());

            // Store the hashed password
            user.setPassword(hashedPassword);
            return hashedPassword;
        } else {
            throw new AuthenticationException(
                "define credential",
                "Unknown credential type"
            );
        }
    };

    public void createRootUser(String userId, String password) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        if (this.rootUser) {
            throw new AuthenticationException("create root user", "A root user has already been created.");
        }

        User root = defineUser(null, userId, "root");

        String credential = addUserCredential(null, userId, "password", password);


//        Permission admin_permission = getPermission("user_admin");
        // Create new Permission
        Permission admin_permission = new Permission("user_admin", "User administrator", "Create, Update, Delete Users");

        // Add to map
        this.permissionMap.put("user_admin", admin_permission);
//        if (admin_permission == null) {
//            admin_permission = definePermission(null, "user_admin", "User administrator", "Create, Update, Delete Users");
//        }

        addEntitlementToUser(null, userId, "user_admin");
        this.rootUser = true;
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


    public boolean authenticateCredential(User user, String credential) {
        if (credential.equals(user.getVoicePrint())) {
            return true;
        }

        if (credential.equals(user.getFacePrint())) {
            return true;
        }

        String login = user.getPassword();
        String hashedPassword = hashToString(credential.getBytes());

        if (login != null && login.equals(hashedPassword)) {
            return true;
        }


//        if (credentialType.equals("password")) {
//            String login = user.getPassword();
//            String hashedPassword = hashToString(credential.getBytes());
//
//            if (login.equals(hashedPassword)) {
//
//                return true;
//
//            }
//        } else if (credentialType.equals("voice_print")) {
//            if (credential.equals(user.getVoicePrint())) {
//                return true;
//            }
//
//        } else if (credentialType.equals("face_print")) {
//            if (credential.equals(user.getFacePrint())) {
//                return true;
//            }
//        }


        return false;
    };

    public Permission definePermission(AuthToken token, String id, String name, String description) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        if (! hasPermission(token, ADMIN_ACCESS, null)) {
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

    public Resource defineResource(AuthToken token, String id, String description) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        // Check the caller has ADMIN_ACCESS permission
        if (! hasPermission(token, ADMIN_ACCESS, null)) {
            throw new AccessDeniedException("define resource", "no admin access");
        }

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



    public Role defineRole(AuthToken token, String id, String name, String description, String resourceId) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        // Check the caller has ADMIN_ACCESS permission
        if (! hasPermission(token, ADMIN_ACCESS, null)) {
            throw new AccessDeniedException("define role", "no admin access");
        }

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

        Role newRole;

        // A Resource is provided, so associate Resource with Role
        if (resourceId != null) {
            Resource resource = getResource(resourceId);

            if (resource == null) {
                throw new AuthenticationException(
                    "define resource role",
                    "The resource " + resourceId + " does not exist. Cannot create resource role."
                );
            }

            newRole = new ResourceRole(id, name, description, resource);
        } else {
            newRole = new Role(id, name, description);
        }

        // Add to map
        this.roleMap.put(id, newRole);

        return newRole;
    };

    public User defineUser(AuthToken token, String id, String name) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        // Check the caller has ADMIN_ACCESS permission
        if (! hasPermission(token, ADMIN_ACCESS, null)) {
            throw new AccessDeniedException("define user", "no admin access");
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

    public AuthToken login(String userId, String credential) throws AuthenticationException, AccessDeniedException {
        System.out.println("AUTH: userId = " + userId + " && credential == " + credential);
        User user = getUser(userId);

        if (authenticateCredential(user, credential)) {
            Integer tokenId = this.tokenMap.size() + 1;
            AuthToken newToken = new AuthToken(String.valueOf(tokenId));

            user.setToken(newToken);
            this.tokenMap.put(tokenId, newToken);

            System.out.println("AUTH: token == " + newToken);
            return newToken;
        }


//        if (credentialType.equals("password")) {
//            String login = user.getPassword();
//
//            byte[] toHash = credential.getBytes();
//
//            String hashedPassword = hashToString(toHash);
//
//            if (login.equals(hashedPassword)) {
//                Integer tokenId = this.tokenMap.size() + 1;
//                AuthToken newToken = new AuthToken(String.valueOf(tokenId));
//
//                user.setToken(newToken);
//                this.tokenMap.put(tokenId, newToken);
//
//                return newToken;
//
//            }
//        }

        throw new AccessDeniedException("login", "login credential is not recognized for " + userId);
    }

    public void logout(AuthToken authToken) {
        if (authToken != null) {
            authToken.invalidate();
        }
        return;
    };

    public void getInventory(AuthToken authToken) throws AuthenticationException {
        Visitor inventory = new InventoryVisitor();
        acceptVisitor(inventory);


        return;
    }

    public Entitlement getEntitlement(String entitlementId) {
        Iterator<Map.Entry<String, Role>> roles = listRoles();
        while (roles.hasNext()) {
            Role role = roles.next().getValue();
            if (entitlementId.equals(role.getId())) {
                return role;
            }
        }

        Iterator<Map.Entry<String, Permission>> permissions = listPermissions();
        while (permissions.hasNext()) {
            Permission permission = permissions.next().getValue();
            if (entitlementId.equals(permission.getId())) {
                return permission;
            }
        }


        return null;
    }
    public Iterator<Map.Entry<String, User>> listUsers() {
        return userMap.entrySet().iterator();
    }

    public Iterator<Map.Entry<String, Resource>> listResources() {
        return resourceMap.entrySet().iterator();
    }

    public Resource getResource(String id) {
        return resourceMap.get(id);
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

    public Role getRole(String id) {
        return roleMap.get(id);
    }

    public boolean hasPermission(AuthToken authToken, String permissionId, Resource resource) throws AuthenticationException, InvalidAuthTokenException {
//        // If a root user has been created, a token is required
//        if (this.rootUser && token == null) {
//            throw new InvalidAuthTokenException("define user", "No token provided.");
//        }
        // If a root user has been created, permissions must be checked
        if (this.rootUser) {
            validateToken(authToken);

            if (permissionId == null) {
                throw new AuthenticationException("has permission", "Missing required permission.");
            }

            Permission permission = getPermission(permissionId);

            Visitor access = new AuthVisitor(authToken, permission, resource);
            acceptVisitor(access);

            System.out.println("AUTH: visitor done, access is " + access.hasPermission());
            return access.hasPermission();
        }

        return true;
    }

    public User validateToken(AuthToken authToken) throws InvalidAuthTokenException {


        if (authToken == null) {
            throw new InvalidAuthTokenException("validate token", "No token provided.");
        }

        if ( ! authToken.isActive() ) {
            throw new InvalidAuthTokenException("validate token", "no longer active");
        }

        // check token hasn't timed out




        // find user

        return null;
//        return this.userMap.get("userId");
    };
}