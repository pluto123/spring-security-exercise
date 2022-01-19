package tw.com.pluto.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = User.builder()
                .username("user123")
                .password("{noop}user123") // 密碼前面加上"{noop}"，不對密碼進行任何格式的編碼。
                .roles("USER")
                .build();

        return userDetails;
    }
}
