package p.russo.manuel.snookerdashboardapi;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UiApplication {

    @RequestMapping("/api/user")
    public Principal user(Principal user) {
        return user;
    }

    @RequestMapping("/api/resource")
    public Map<String, Object> home() {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("id", UUID.randomUUID().toString());
        model.put("content", "Hello World");
        return model;
    }
}
