package tw.com.pluto.security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create(); // Gson 預設會將字串中的一些特殊字元轉碼，如 = 會轉成 \U003D
        int statue = HttpStatus.BAD_REQUEST.value();
        MediaType contentType = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
        String body = exception.getMessage();

        // 若有需要可以依據異常，產生不同的回應
        //  - UsernameNotFoundException : 會在 UserDetailsService 的 loadUserByUsername() 中找不到使用者時拋出
        //  - BadCredentialsException : 會在 AuthenticationProvider 的 authenticate() 中驗證失敗時拋出
        if (exception instanceof UsernameNotFoundException) {
//            statue = HttpStatus.BAD_REQUEST.value();
            statue = 123;
        }
        else if (exception instanceof BadCredentialsException) {
 //           statue = HttpStatus.BAD_REQUEST.value();
            statue = 456;
        }

        ResponseEntity<String> responseEntity = ResponseEntity
                .status(statue)
                .contentType(contentType)
                .body(body);

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(gson.toJson(responseEntity));
    }
}