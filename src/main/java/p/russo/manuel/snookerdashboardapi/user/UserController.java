package p.russo.manuel.snookerdashboardapi.user;

import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Secured("ROLE_USER")
    @RequestMapping(method = RequestMethod.GET, value = "/users/current", produces = MediaType.APPLICATION_JSON_VALUE)
    UserResponse getCurrent(Authentication authentication) {
        final UserResponse user = new UserResponse();
        user.setUsername(String.valueOf(authentication.getPrincipal()));
        return user;
    }
}
