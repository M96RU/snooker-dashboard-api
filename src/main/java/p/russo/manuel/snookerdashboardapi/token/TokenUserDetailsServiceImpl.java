package p.russo.manuel.snookerdashboardapi.token;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TokenUserDetailsServiceImpl implements UserDetailsService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // hard coding the users. All passwords must be encoded.
        final List<AppUser> users = Arrays.asList(
                new AppUser(1, "manuel", encoder.encode("russo"), Arrays.asList("USER")),
                new AppUser(2, "admin", encoder.encode("russo"), Arrays.asList("USER", "ADMIN"))
        );

        final AppUser currentUser = users.stream().filter((user) -> {
            return user.getUsername().equals(username);
        }).findFirst().get();

        if (currentUser == null) {
            // If user not found. Throw this exception.
            throw new UsernameNotFoundException("Username: " + username + " not found");
        }
        
        List<GrantedAuthority> grantedAuthorities = currentUser.getRoles().stream().map((role) -> {
            return "ROLE_" + role; // Spring needs roles to be begin by "ROLE_"
        }).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        // The "User" class is provided by Spring and represents a model class for user to be returned by UserDetailsService
        // And used by auth manager to verify and check user authentication.
        return new User(currentUser.getUsername(), currentUser.getPassword(), grantedAuthorities);
    }

    @Data
    @AllArgsConstructor
    private static class AppUser {
        private Integer id;
        private String username, password;
        private List<String> roles;
    }
}
