package tw.com.pluto.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MyPasswordEncoder {
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PasswordEncoder getPasswordEncoder(){
        return this.passwordEncoder;
    }
}