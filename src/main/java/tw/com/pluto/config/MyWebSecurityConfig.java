package tw.com.pluto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import tw.com.pluto.security.*;

@Configuration
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    MyAuthenticationProvider myAuthenticationProvider;
    @Autowired
    MyAccessDeny myAccessDeny;
    @Autowired
    MyAuthenticationFailureHandler myAuthenticationFailureHandler;
    @Autowired
    MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    MyLogoutSuccessHandler myLogoutSuccessHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(myAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .failureHandler(myAuthenticationFailureHandler)
                .successHandler(myAuthenticationSuccessHandler).and()
                .logout()
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .deleteCookies("JSESSIONID").and()
                .authorizeRequests()
                .mvcMatchers("/").permitAll()
                .mvcMatchers("/user/**").hasRole("USER")
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated().and()
                .exceptionHandling().accessDeniedHandler(myAccessDeny);
    }

    @Bean
    RoleHierarchy hierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        return hierarchy;
    }
}
