package p.russo.manuel.snookerdashboardapi.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class TokenCredentialsAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authManager;
    private final TokenProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {

            // 1. Get credentials from request
            UserCredentials credentials = objectMapper.readValue(request.getInputStream(), UserCredentials.class);

            // 2. Create auth object (contains credentials) which will be used by auth manager
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword(), Collections.emptyList());

            // 3. Setting details if need elsewhere
            this.setDetails(request, authToken);

            // 4. Authentication manager authenticate the user, and use UserDetialsServiceImpl::loadUserByUsername() method to load the user.
            return authManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("Unable to authenticate the user", e);
        }
    }

    // Upon successful authentication, generate a token.
    // The 'auth' passed to successfulAuthentication() is the current authenticated user.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        Instant now = Instant.now();
        Instant expired = now.plusSeconds(properties.getExpiration());
        String username = auth.getName();

        String token = Jwts.builder()
                .setSubject(username)
                // Convert to list of strings.
                // This is important because it affects the way we get them back in the Gateway.
                .claim("authorities", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(now.toEpochMilli()))
                .setExpiration(new Date(expired.toEpochMilli()))  // in milliseconds
                .signWith(SignatureAlgorithm.HS512, properties.getSecret().getBytes())
                .compact();

        objectMapper.writeValue(response.getWriter(), new TokenResponse(username, token));
    }

    @AllArgsConstructor
    @Data
    private static class TokenResponse {
        private String username, token;
    }

    @AllArgsConstructor
    @Data
    private static class UserCredentials {
        private String username, password;
    }
}
