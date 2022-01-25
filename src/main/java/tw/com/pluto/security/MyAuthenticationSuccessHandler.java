package tw.com.pluto.security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create(); // Gson 預設會將字串中的一些特殊字元轉碼，如 = 會轉成 \U003D

        ResponseEntity<String> responseEntity = ResponseEntity
                .status(HttpStatus.OK.value())
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .body("登入成功");

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(gson.toJson(responseEntity));
    }
}
