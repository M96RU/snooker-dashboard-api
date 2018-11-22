package p.russo.manuel.snookerdashboardapi.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity    // Enable security config. This annotation denotes config for spring security.
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final TokenProperties properties;
    private final TokenUserDetailsServiceImpl tokenUserDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                // make sure we use stateless session; session won't be used to store user's state.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                // handle an authorized attempts
                .exceptionHandling().authenticationEntryPoint((req, rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))

                .and()
                // Add a filter to validate the tokens with every request
                .addFilterAfter(new TokenHeaderAuthenticationFilter(properties), UsernamePasswordAuthenticationFilter.class)

                // What's the authenticationManager()?
                // An object provided by WebSecurityConfigurerAdapter, used to authenticate the user passing user's credentials
                // The filter needs this auth manager to authenticate the user.
                .addFilter(new TokenCredentialsAuthenticationFilter(authenticationManager(), properties, objectMapper));
    }

    // Spring has UserDetailsService interface, which can be overriden to provide our implementation for fetching user from database (or any other source).
    // The UserDetailsService object is used by the auth manager to load the user from database.
    // In addition, we need to define the password encoder also. So, auth manager can compare and verify passwords.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(tokenUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
