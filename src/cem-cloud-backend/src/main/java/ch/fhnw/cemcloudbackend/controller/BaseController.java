package ch.fhnw.cemcloudbackend.controller;

import ch.fhnw.cemcloudbackend.model.User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public abstract class BaseController {

    protected User getUser(JwtAuthenticationToken auth) {
        return new User(auth);
    }
}
