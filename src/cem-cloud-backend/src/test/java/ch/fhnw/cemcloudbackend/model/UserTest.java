package ch.fhnw.cemcloudbackend.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserTest {
    @Test
    public void getEmailShouldBeEqual() {
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        final String userEmail = "someone@example.com";
        Map<String, Object> userAttributes = Map.of("email", userEmail);
        when(auth.getTokenAttributes()).thenReturn(userAttributes);
        User user = new User(auth);

        String email = user.getEmail();

        assertEquals(userEmail, email);
    }

    @Test
    public void getIdShouldBeEqual() {
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        final UUID userId = UUID.randomUUID();
        when(auth.getName()).thenReturn(userId.toString());
        User user = new User(auth);

        UUID id = user.getId();

        assertEquals(userId, id);
    }

    @Test
    public void isInRoleShouldBeTrue() {
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        List<String> roles = List.of(Role.ADMINISTRATOR, Role.INSTALLATEUR);
        Map<String, Object> realmAccess = Map.of("roles", roles);
        Map<String, Object> userAttributes = Map.of("realm_access", realmAccess);
        when(auth.getTokenAttributes()).thenReturn(userAttributes);
        User user = new User(auth);

        boolean isInRole = user.isInRole(Role.INSTALLATEUR);

        assertTrue(isInRole);
    }

    @Test
    public void isInRoleShouldBeFalse() {
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        List<String> roles = List.of(Role.INSTALLATEUR);
        Map<String, Object> realmAccess = Map.of("roles", roles);
        Map<String, Object> userAttributes = Map.of("realm_access", realmAccess);
        when(auth.getTokenAttributes()).thenReturn(userAttributes);
        User user = new User(auth);

        boolean isInRole = user.isInRole(Role.ADMINISTRATOR);

        assertFalse(isInRole);
    }

    @Test
    public void isInAnyRoleShouldBeTrue() {
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        List<String> roles = List.of(Role.ADMINISTRATOR);
        Map<String, Object> realmAccess = Map.of("roles", roles);
        Map<String, Object> userAttributes = Map.of("realm_access", realmAccess);
        when(auth.getTokenAttributes()).thenReturn(userAttributes);
        User user = new User(auth);

        boolean isInRole = user.isInAnyRole(Role.INSTALLATEUR, Role.ADMINISTRATOR);

        assertTrue(isInRole);
    }

    @Test
    public void isInAnyRoleShouldBeFalse() {
        JwtAuthenticationToken auth = mock(JwtAuthenticationToken.class);
        List<String> roles = List.of(Role.ADMINISTRATOR);
        Map<String, Object> realmAccess = Map.of("roles", roles);
        Map<String, Object> userAttributes = Map.of("realm_access", realmAccess);
        when(auth.getTokenAttributes()).thenReturn(userAttributes);
        User user = new User(auth);

        boolean isInRole = user.isInAnyRole(Role.INSTALLATEUR);

        assertFalse(isInRole);
    }
}
