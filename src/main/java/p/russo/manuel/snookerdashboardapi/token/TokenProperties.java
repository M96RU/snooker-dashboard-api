package p.russo.manuel.snookerdashboardapi.token;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "snooker.token")
public class TokenProperties {
    private String header;
    private String prefix;
    private int expiration;
    private String secret;
}
