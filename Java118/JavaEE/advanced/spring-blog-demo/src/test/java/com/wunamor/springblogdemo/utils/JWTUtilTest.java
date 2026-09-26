package com.wunamor.springblogdemo.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtilTest {

    private final String secretString = "aphifgaafwowurlg8ayzlsfgyafwawz39y5t2pa0u1eafwawf2g1233";

    private final SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));

    private final long Expiration = 7 * 24 * 1000 * 60 * 60; // 7 天

    @Test
    public void test1() {
        Map<String, Object> claim = new HashMap<>();
        claim.put("id", 1);
        claim.put("name", "lisi");

        String result = Jwts.builder()
                .setClaims(claim) //⾃定义内容(负载)
                .setIssuedAt(new Date())// 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() +
                        Expiration)) //设置过期时间
                .signWith(secretKey) //签名算法
                .compact();
        System.out.println(result);
    }

    @Test
    public void parse() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoibGlzaSIsImlkIjoxLCJpYXQiOjE3OTA0MTE4ODcsImV4cCI6MTc5MTAxNjY4N30.lNGjeQu2vTYVGsgcRWKFwtmXzpbAGLJXt5r3a_87kOs";

        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
        // 使用 parseClaimsJws 而不是 parseClaimsJwt
        Claims body = jwtParser.parseClaimsJws(token).getBody();
        System.out.println(body);

    }
}
