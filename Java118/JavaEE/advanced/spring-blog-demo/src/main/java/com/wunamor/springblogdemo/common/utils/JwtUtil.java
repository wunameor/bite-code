package com.wunamor.springblogdemo.common.utils;

import com.wunamor.springblogdemo.common.exception.BlogException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtUtil {

    private static final String secretString = "aphifgaafwowurlg8ayzlsfgyafwawz39y5t2pa0u1eafwawf2g1233";

    private static final SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));

    private static final long Expiration = 7 * 24 * 1000 * 60 * 60; // 7 天

    public static String getJwt(Map<String, Object> claim) {
        String result = Jwts.builder()
                .setClaims(claim) //⾃定义内容(负载)
                .setIssuedAt(new Date())// 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() +
                        Expiration)) //设置过期时间
                .signWith(secretKey) //签名算法
                .compact();
        return result;
    }

    public static Claims parseJwt(String token) {
        Claims result = null;
        try {
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
            result = jwtParser.parseClaimsJws(token).getBody();
        } catch (Exception e) {
            log.error("令牌解析失败：token: {}", token);
            throw new BlogException("令牌获取失败");
        }
        // 使用 parseClaimsJws 而不是 parseClaimsJwt
        return result;
    }

    public static void main(String[] args) {
        System.out.println(parseJwt("eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlck5hbWUiOiJ6aGFuZ3NhbiIsImlhdCI6MTc5MDcyODYyOCwiZXhwIjoxNzkxMzMzNDI4fQ.vpPGm4sUDBxLViufLHAKlzwXHmMffnb4jGokr_xk1js"));
    }
}
