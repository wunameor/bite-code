package com.wunamor.lotterysystem.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: yibo
 */
public class JWTUtil {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);
    /**
     * 密钥：Base64编码的密钥。
     * <p>云服务器通过 JWT_SECRET 环境变量注入独立密钥，避免使用仓库里的公开默认值；
     * 本地未配置时沿用原开发密钥，保证启动和测试行为不变。</p>
     */
    private static final String SECRET = resolveSecret();
    /**
     * 生成安全密钥：将一个Base64编码的密钥解码并创建一个HMAC SHA密钥。
     */
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode(SECRET));
    /**
     * 过期时间(单位: 毫秒)
     */
    private static final long EXPIRATION = 7*24*60*60*1000; // 7 天

    /**
     * 优先读取 JWT_SECRET，未配置时回退到仓库默认密钥。
     * <p>环境变量必须是 Base64 编码、解码后至少 32 字节，否则 HS256 初始化直接失败，
     * 让配置错误在启动阶段暴露，而不是运行期才出现令牌异常。</p>
     */
    private static String resolveSecret() {
        String fromEnv = System.getenv("JWT_SECRET");
        return StringUtils.hasText(fromEnv) ? fromEnv
                : "SDKltwTl3SiWX62dQiSHblEB6O03FG9/vEaivFu6c6g=";
    }

    /**
     * 生成密钥
     *
     * @param claim  {"id": 12, "name":"张山"}
     * @return
     */
    public static String genJwt(Map<String, Object> claim){
        //签名算法
        String jwt = Jwts.builder()
                .setClaims(claim)             // 自定义内容(载荷)
                .setIssuedAt(new Date())      // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) // 设置过期时间
                .signWith(SECRET_KEY)         // 签名算法
                .compact();
        return jwt;
    }

    /**
     * 验证密钥
     */
    public static Claims parseJWT(String jwt){
        if (!StringUtils.hasLength(jwt)){
            return null;
        }
        // 创建解析器, 设置签名密钥
        JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder().setSigningKey(SECRET_KEY);
        Claims claims = null;
        try {
            //解析token
            claims = jwtParserBuilder.build().parseClaimsJws(jwt).getBody();
        }catch (Exception e){
            // 签名验证失败 / 令牌过期都属于正常业务分支，用 warn 记录，且不要打印完整 token
            logger.warn("解析令牌失败: {}", e.getMessage());
        }
        return claims;

    }

    /**
     * 从token中获取用户ID
     */
    public static Integer getUserIdFromToken(String jwtToken) {
        Claims claims = JWTUtil.parseJWT(jwtToken);
        if (claims != null) {
            Map<String, Object> userInfo = new HashMap<>(claims);
            Object userId = userInfo.get("userId");
            // 不能直接 (Integer) 强转：claim 里是数字，Jackson 可能给 Integer 也可能给 Long
            return userId == null ? null : ((Number) userId).intValue();
        }
        return null;
    }
}
