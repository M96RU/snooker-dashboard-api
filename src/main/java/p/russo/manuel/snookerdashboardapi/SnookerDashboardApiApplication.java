package p.russo.manuel.snookerdashboardapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@SpringBootApplication
public class SnookerDashboardApiApplication {

	@Configuration
	@Order(SecurityProperties.IGNORED_ORDER)
	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
					.httpBasic()
					.and()
					.authorizeRequests()
					.antMatchers("/index.html", "/", "/home", "/login").permitAll()
					.anyRequest().authenticated()
					.and().logout().logoutUrl("/api/logout").permitAll()
					.and().csrf()
					.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(SnookerDashboardApiApplication.class, args);
	}
}
