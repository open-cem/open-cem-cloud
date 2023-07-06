package ch.fhnw.cemcloudbackend.model;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class User {

    private static final String EMAIL_CLAIM_NAME = "email";

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

    public Collection<String> getRoles() {
        var attributes = auth.getTokenAttributes();
        Map<String, Object> access = (Map<String, Object>) attributes.get("realm_access");
        List<String> roles = (List<String>) access.get("roles");

        return roles;
    }
}
