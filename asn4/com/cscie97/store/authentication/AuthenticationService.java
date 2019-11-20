package com.cscie97.store.authentication;

import com.cscie97.store.model.Observer;
import com.cscie97.store.model.StoreModelServiceInterface;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

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
     * Time limit token is valid for before a new token needs to be issued (in miliseconds).
     * A longer duration, in minutes or hours, would be more appropriate in a production setting.
     * For purposes of testing and assignment submission, milliseconds are used to demonstrate
     * InvalidAuthTokenException due to timeout.
     */
    private static final long TOKEN_TIMEOUT = 27;

    /**
     * Private AuthenticationService Constructor. Adheres to Singleton Pattern.
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

    /**
     * {@inheritDoc}
     */
    public void acceptVisitor(Visitor visitor)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        visitor.visitAuthenticationService(this);
        return;
    };

    /**
     * {@inheritDoc}
     */
    public void addEntitlementToUser(AuthToken token, String userId, String entitlementId)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        // Check the caller has ADMIN_ACCESS permission
        if (! hasPermission(token, ADMIN_ACCESS, null)) {
            throw new AccessDeniedException("add entitlement to user", "no admin access");
        }

        // Retrieve specified User and Entitlement
        User user = getUser(userId);
        Entitlement entitlement = getEntitlement(entitlementId);

        // Check that User exists
        if (user == null) {
            throw new AuthenticationException(
                "add entitlement to User",
                "User " + userId + " does not exist."
            );
        }

        // Check that Entitlement exists
        if (entitlement == null) {
            throw new AuthenticationException(
                "add entitlement to User",
                "Entitlement " + entitlementId + " does not exist."
            );
        }

        // Add Entitlement to User
        user.addEntitlement(entitlement);
        return;
    };

    /**
     * {@inheritDoc}
     */
    public void addEntitlementToRole(AuthToken token, String entitlementId, String roleId)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        // Check the caller has ADMIN_ACCESS permission
        if (! hasPermission(token, ADMIN_ACCESS, null)) {
            throw new AccessDeniedException("add entitlement to role", "no admin access");
        }

        // Retrieve specified Entitlement and Role
        Entitlement entitlement = getEntitlement(entitlementId);
        Role role = getRole(roleId);

        // Check that Entitlement exists
        if (entitlement == null) {
            throw new AuthenticationException(
                "add entitlement to role",
                "Entitlement " + entitlementId + " does not exist."
            );
        }

        // Check that Role exists
        if (role == null) {
            throw new AuthenticationException(
                "add entitlement to role",
                "Role " + roleId + " does not exist."
            );
        }

        // Add Entitlement to Role
        role.addEntitlement(entitlement);
        return;
    };

    /**
     * {@inheritDoc}
     */
    public boolean authenticateCredential(User user, String credential) {
        // Check Voice Print
        if (credential.equals(user.getVoicePrint())) {
            return true;
        }

        // Check Face Print
        if (credential.equals(user.getFacePrint())) {
            return true;
        }

        // Check Password, compare hashes
        String login = user.getPassword();
        String hashedPassword = hashToString(credential.getBytes());

        if (login != null && login.equals(hashedPassword)) {
            return true;
        }

        return false;
    };

    /**
     * {@inheritDoc}
     */
    public void createRootUser(String userId, String password)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        // Check if root user has already been initialized
        if (this.rootUser) {
            throw new AuthenticationException("create root user", "A root user has already been created.");
        }

        // Create the root user with userId provided
        User root = defineUser(null, userId, "root");

        // Hash the password and store it with the user
        String credential = defineCredential(null, userId, "password", password);

        // Create new admin Permissions
        Permission admin_permission = new Permission(ADMIN_ACCESS, "User administrator", "Create, Update, Delete Users");

        // Add to map
        this.permissionMap.put(ADMIN_ACCESS, admin_permission);

        // Grant the root user the admin privileges
        addEntitlementToUser(null, userId, "user_admin");

        // Indicate root user has been created, prevent overwriting
        this.rootUser = true;
        return;
    }

    /**
     * {@inheritDoc}
     */
    public String defineCredential(AuthToken token, String userId, String credentialType, String credential)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        // Check the caller has ADMIN_ACCESS permission
        if (! hasPermission(token, ADMIN_ACCESS, null)) {
            throw new AccessDeniedException("add user credential", "no admin access");
        }

        // Check the userId exists
        User user = getUser(userId);

        // Check voice print
        if (credentialType.equals("voice_print")) {

            String voiceCheck = "--voice:" + user.getName() + "--";

            if (! credential.equals(voiceCheck)) {
                throw new AuthenticationException(
                        "define credential",
                        "Voice print '" +  credential + "' does not match format " + voiceCheck
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
                        "Face print does not match format " + faceCheck
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


    /**
     * {@inheritDoc}
     */
    public Permission definePermission(AuthToken token, String id, String name, String description)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        // Check the caller has ADMIN_ACCESS permission
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

    /**
     * {@inheritDoc}
     */
    public Resource defineResource(AuthToken token, String id, String description)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
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


    /**
     * {@inheritDoc}
     */
    public Role defineRole(AuthToken token, String id, String name, String description, String resourceId)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
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

    /**
     * {@inheritDoc}
     */
    public User defineUser(AuthToken token, String id, String name)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
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

    /**
     * {@inheritDoc}
     */
    public void getInventory(AuthToken authToken)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        // Create Visitor
        Visitor inventory = new InventoryVisitor(authToken);

        // Visitor traverses objects and prints Inventory once constructed
        acceptVisitor(inventory);
        return;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasPermission(AuthToken authToken, String permissionId, Resource resource)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        // If a root user has been created, permissions must be checked
        if (this.rootUser) {
            validateToken(authToken);

            // Check the requested Permission exists
            if (permissionId == null) {
                throw new AuthenticationException("has permission", "Missing required permission.");
            }

            Permission permission = getPermission(permissionId);

            // Create the Visitor
            Visitor access = new AuthVisitor(authToken, permission, resource);

            // Visitor traverses Users and Permissions
            acceptVisitor(access);

            // Check if Permission was found
            return access.hasPermission();
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public AuthToken login(String userId, String credential)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        User user = getUser(userId);

        // Check credential against stored values
        if (authenticateCredential(user, credential)) {
            // Credential valid, so create a new AuthToken
            Integer tokenId = this.tokenMap.size() + 1;
            AuthToken newToken = new AuthToken(String.valueOf(tokenId));

            // Track the AuthToken and associate with User
            user.setToken(newToken);
            this.tokenMap.put(tokenId, newToken);

            return newToken;
        }

        throw new AccessDeniedException("login", "login credential is not recognized for " + userId);
    }

    /**
     * {@inheritDoc}
     */
    public void logout(AuthToken authToken) {
        if (authToken != null) {
            authToken.invalidate();
        }
        return;
    };

    /**
     * {@inheritDoc}
     */
    public void validateToken(AuthToken authToken) throws InvalidAuthTokenException {
        // Check token is not null
        if (authToken == null) {
            throw new InvalidAuthTokenException(null, "No token provided.");
        }

        // Check token is still active
        if ( ! authToken.isActive() ) {
            throw new InvalidAuthTokenException(authToken.getId(), "no longer active");
        }

        // Check token issue time against TIMEOUT duration
        Date now = new Date();
        if ( (now.getTime() - authToken.getTimeIssued()) > TOKEN_TIMEOUT ) {
            throw new InvalidAuthTokenException(authToken.getId(), "Token has exceeded timeout duration of " + TOKEN_TIMEOUT);
        }

        return;
    };

    /**
     * Returns list of Permissions in the Authentication Service.
     */
    public Iterator<Map.Entry<String, Permission>> listPermissions() {
        return permissionMap.entrySet().iterator();
    }

    /**
     * Returns list of Resources in the Authentication Service.
     */
    public Iterator<Map.Entry<String, Resource>> listResources(AuthToken token) {
        return resourceMap.entrySet().iterator();
    }

    /**
     * Returns list of Roles in the Authentication Service.
     */
    public Iterator<Map.Entry<String, Role>> listRoles() {
        return roleMap.entrySet().iterator();
    }

    /**
     * Returns list of Users in the Authentication Service.
     */
    public Iterator<Map.Entry<String, User>> listUsers() {
        return userMap.entrySet().iterator();
    }

    private Entitlement getEntitlement(String entitlementId)
            throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException{

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

    private Permission getPermission(String id) {
        return permissionMap.get(id);
    }

    private Resource getResource(String id) {
        return resourceMap.get(id);
    }

    private Role getRole(String id) {
        return roleMap.get(id);
    }

    private User getUser(String userId) throws AuthenticationException, AccessDeniedException, InvalidAuthTokenException {
        User user = this.userMap.get(userId);

        if (user == null) {
            throw new AuthenticationException(
                    "get user",
                    String.format("A user with id '%s' is not registered with the AuthenticationService.", userId)
            );
        }

        return user;
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

}