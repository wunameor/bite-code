package com.wunamor.springblogdemo.utils;

import io.jsonwebtoken.Jwts;
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

    private final long Expiration = 1000 * 60 * 60;

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
}
