package tw.com.pluto.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tw.com.pluto.model.User;
import tw.com.pluto.repository.UserRepository;
import tw.com.pluto.security.utils.JwtUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token;
            String bearer = request.getHeader("Authorization");  // 由請求的 Header 中取出 key 為 Authorization 的值
            if (bearer != null && !isBlankString(bearer) && bearer.startsWith("Bearer ")) {  // 依據 Bearer (RFC 6750) 的定義，前面七個字元是 "Bearer "
                token = bearer.substring(7);  // 只取 token
                String userName = jwtUtil.parseUserNameFromToken(token); // 取得使用者名稱
                List<SimpleGrantedAuthority> userAuthorities = jwtUtil.parseUserAuthoritiesFromToken(token); // 取得使用者授權
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName); // 由使用者名稱取得詳細資訊

                UsernamePasswordAuthenticationToken authAfterSuccessLogin = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authAfterSuccessLogin.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authAfterSuccessLogin);
            }
        }
        catch (Exception e) {  // 若解析 token 過程中有錯誤則中斷請求
            logger.error(e.getMessage(), e);

            Map<String, String> errorMsg = new LinkedHashMap<>();
            errorMsg.put("error", e.getMessage());

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), errorMsg);
            return;
        }
        // 若在這個過濾器運作正常(1. 沒 token；2. 有 token 且驗證成功)，則將請求交由下一個過濾器進行處理
        filterChain.doFilter(request, response);
    }

    private boolean isBlankString(String bearer) {
        return bearer == null || bearer.trim().isEmpty();
    }
}
