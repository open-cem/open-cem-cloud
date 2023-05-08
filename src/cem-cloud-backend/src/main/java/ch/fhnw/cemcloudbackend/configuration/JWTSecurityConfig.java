package ch.fhnw.cemcloudbackend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class JWTSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                auth.anyRequest()
                        .authenticated())
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        http.cors();
        return  http.build();
    }
}
