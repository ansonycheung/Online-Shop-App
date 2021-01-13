package onlineShop;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.context.annotation.Bean;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private DataSource dataSource;

  @Override
  // Authentication: The process of checking credentials and making sure the current logged user
  //                 is who they claim to be.
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .formLogin()
        .loginPage("/login");
    http
        .authorizeRequests()
        .antMatchers("/cart/**").hasAuthority("ROLE_USER")
        .antMatchers("/get*/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
        .antMatchers("/admin*/**").hasAuthority("ROLE_ADMIN")
        .anyRequest().permitAll();
  }

  @Override
  // Authorization: The process of deciding whether a current logged user is allowed to
  //                perform an action within your application
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    // admin account for test, do not register
    auth
        .inMemoryAuthentication().withUser("admin@shopping.com").password("admin").authorities("ROLE_ADMIN");

    auth
        .jdbcAuthentication()
        .dataSource(dataSource)
        .usersByUsernameQuery("SELECT emailId, password, enabled FROM users WHERE emailId=?")
        .authoritiesByUsernameQuery("SELECT emailId, authorities FROM authorities WHERE emailId=?");
  }

  @SuppressWarnings("deprecation")
  @Bean
  // the method is only for framework, not for my coding
  public static NoOpPasswordEncoder passwordEncoder() {
    return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
  }

}

