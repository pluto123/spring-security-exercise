package tw.com.pluto.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final String CLAIMS_KEY_USER_ROLES = "userRoles";

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.expire_time}")
    private long expireTime;

    public String createToken(String userName, List<String> userRoles){
        Map <String, Object> claimMap = new HashMap();
        claimMap.put(CLAIMS_KEY_USER_ROLES, userRoles);

        String token = Jwts.builder()
                .setSubject(userName)  // 把使用者名稱放入 TOKEN 的 subject 中，之後的請求返回 TOKEN 時，就可以依據該使用者名稱取得對應的權限
                .addClaims(claimMap)  // 也可把一些不敏感的資料記錄到 TOKEN 的 claim 區塊中，我這邊以使用權限作示範，實際生產環境中，通常還是會由資料庫中查詢權限
                .setIssuedAt(new Date()) // 簽發時間
                .setExpiration(Date.from(Instant.now().plusSeconds(expireTime))) // 過期時間
                .signWith(
                        Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS512)  // 簽證的方式
                .compact();
        logger.debug("token : {}", token);
        return token;
    }

    private Claims parseToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();  // 示範將 TOKEN 中的不敏感資料取出，以產生 TOKEN 時，填入的使用者權限為例
        logger.debug("claims : {}", claims);
        return claims;
    }

    public String parseUserNameFromToken(String token) {
        return parseToken(token).getSubject();  // 產生 TOKEN 時，是將使用者名稱填入的 subject 中，所以也由 subject 中取出使用者名稱
    }

    public List<SimpleGrantedAuthority> parseUserAuthoritiesFromToken(String token) {
        List<String> userRoles = parseToken(token).get(CLAIMS_KEY_USER_ROLES, List.class);
        logger.debug("userRoles : {}", userRoles);
        return userRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
