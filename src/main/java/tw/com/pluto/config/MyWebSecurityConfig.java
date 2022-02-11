package tw.com.pluto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tw.com.pluto.security.MyUserDetailsService;
import tw.com.pluto.security.entry.UnauthorizedEntryPoint;
import tw.com.pluto.security.handler.MyAccessDeniedHandler;
import tw.com.pluto.security.handler.MyAuthenticationFailureHandler;
import tw.com.pluto.security.handler.MyAuthenticationSuccessHandler;
import tw.com.pluto.security.handler.MyLogoutSuccessHandler;
import tw.com.pluto.security.strategy.MySessionInformationExpiredStrategy;

@Configuration
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    MyUserDetailsService myUserDetailsService;
    @Autowired
    MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    MyAuthenticationFailureHandler myAuthenticationFailureHandler;
    @Autowired
    MyLogoutSuccessHandler myLogoutSuccessHandler;
    @Autowired
    MyAccessDeniedHandler myAccessDeniedHandler;
    @Autowired
    UnauthorizedEntryPoint unauthorizedEntryPoint;
    @Autowired
    MySessionInformationExpiredStrategy mySessionInformationExpiredStrategy;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable();

        http
                .authorizeRequests()
                .mvcMatchers("/user/**").hasRole("USER")
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .and().formLogin().permitAll()
                .successHandler(myAuthenticationSuccessHandler) // 登录成功逻辑处理
                .failureHandler(myAuthenticationFailureHandler) // 登录失败逻辑处理

                .and()
                .logout()   //开启注销
                .permitAll()    //允许所有人访问
                .logoutSuccessHandler(myLogoutSuccessHandler) //注销逻辑处理
                .deleteCookies("JSESSIONID")    //删除cookie

                .and().exceptionHandling()
                .accessDeniedHandler(myAccessDeniedHandler)    //权限不足的时候的逻辑处理
                .authenticationEntryPoint(unauthorizedEntryPoint);  //未登录是的逻辑处理
    }

    @Bean
    RoleHierarchy hierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        return hierarchy;
    }
}