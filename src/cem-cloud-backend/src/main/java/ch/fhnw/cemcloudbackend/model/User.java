package ch.fhnw.cemcloudbackend.model;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;

public class User {

    private static final String EMAIL_CLAIM_NAME = "email";
    private static final String REALM_ACCESS_CLAIM_NAME = "realm_access";
    private static final String ROLE_ATTRIBUTE_NAME = "roles";

    private final JwtAuthenticationToken auth;

    public User(JwtAuthenticationToken auth) {
        this.auth = auth;
    }

    public String getEmail() {
        var attributes = auth.getTokenAttributes();
        return (String) attributes.get(EMAIL_CLAIM_NAME);
    }

    public UUID getId() {
        return UUID.fromString(auth.getName());
    }

    public boolean isInRole(String role) {
        Collection<String> roles = getRoles();

        return roles.contains(role);
    }

    public boolean isInAnyRole(String... roles) {
        Collection<String> userRoles = getRoles();

        return Arrays.stream(roles)
                     .anyMatch(userRoles::contains);
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getRoles() {
        var attributes = auth.getTokenAttributes();
        Map<String, Object> access = (Map<String, Object>) attributes.get(REALM_ACCESS_CLAIM_NAME);
        List<String> roles = (List<String>) access.get(ROLE_ATTRIBUTE_NAME);

        return roles;
    }
}
