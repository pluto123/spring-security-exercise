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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()  // 允許跨域
                .csrf().disable()  // 關閉 CSRF
                .authorizeRequests()  // 設定權限
                .mvcMatchers("/user/**").hasRole("USER")
                .mvcMatchers("/admin/**").hasRole("ADMIN").and()
                .formLogin().permitAll()  // 允許每個使用者都可訪問登入 URL
                .successHandler(myAuthenticationSuccessHandler) // 登入成功後的處理程序
                .failureHandler(myAuthenticationFailureHandler).and() // 登入失敗後的處理程序
                .logout().permitAll()  // 允許每個使用者都可訪問登出 URL
                .logoutSuccessHandler(myLogoutSuccessHandler)  // 登出成功後的處理程序
                .deleteCookies("JSESSIONID").and()  // 刪除 SESSION Cookie
                .exceptionHandling()
                .accessDeniedHandler(myAccessDeniedHandler)  // 無權限訪問的處理程序
                .authenticationEntryPoint(unauthorizedEntryPoint); // 未驗證時的處理程序
    }

    @Bean
    RoleHierarchy hierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");

        return hierarchy;
    }
}