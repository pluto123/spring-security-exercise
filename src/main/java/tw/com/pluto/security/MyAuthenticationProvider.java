package tw.com.pluto.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class MyAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    MyUserDetailsService myUserDetailsService;

    @Autowired
    MyPasswordEncoder myPasswordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
        String dd = myPasswordEncoder.getPasswordEncoder().encode(password);
        boolean matchPassword = myPasswordEncoder.getPasswordEncoder().matches(password, userDetails.getPassword());
        if (!matchPassword) {
            throw new BadCredentialsException("密碼錯誤");
        }
        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}