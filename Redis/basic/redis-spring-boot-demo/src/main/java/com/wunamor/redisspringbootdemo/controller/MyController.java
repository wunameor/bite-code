package com.wunamor.redisspringbootdemo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/my")
@RestController
public class MyController {

    @Autowired
    public StringRedisTemplate redisTemplate;

    @RequestMapping("/testString/{id}")
    public String testString(@PathVariable("id") String id) {
        flushAll();
        redisTemplate.opsForValue().set("k1", "111");
        redisTemplate.opsForValue().set("k2", "222");
        redisTemplate.opsForValue().set("k3", "333");

        return redisTemplate.opsForValue().get(id);
    }

    @RequestMapping("/testList")
    public String testList() {
        flushAll();
        redisTemplate.opsForList().leftPushAll("k1", List.of("1", "2", "3"));
        System.out.println(redisTemplate.opsForList().rightPop("k1"));
        System.out.println(redisTemplate.opsForList().rightPop("k1"));
        System.out.println(redisTemplate.opsForList().rightPop("k1"));
        return "OK";
    }

    @RequestMapping("/testSet")
    public String testSet() {
        flushAll();
        String key = "k1";
        redisTemplate.opsForSet().add(key, "1", "2", "3");

        System.out.println("size: " + redisTemplate.opsForSet().size(key));

        System.out.println("members: " + redisTemplate.opsForSet().members(key));
        System.out.println("isMembers: " + redisTemplate.opsForSet().isMember(key, "1"));
        redisTemplate.opsForSet().remove(key, "1", "3");
        System.out.println("members: " + redisTemplate.opsForSet().members(key));

        return "OK";
    }

    @RequestMapping("/testHash")
    public String testHash() {
        flushAll();
        String key = "k1";
        redisTemplate.opsForHash().put(key, "f1", "1");
        redisTemplate.opsForHash().put(key, "f2", "2");
        redisTemplate.opsForHash().put(key, "f3", "3");

        System.out.println("get: f1 " + redisTemplate.opsForHash().get(key, "f1"));
        System.out.println("get: f5 " + redisTemplate.opsForHash().get(key, "f5"));
        System.out.println("size: " + redisTemplate.opsForHash().size(key));

        redisTemplate.opsForHash().delete(key, "f2", "f4");

        System.out.println("size: " + redisTemplate.opsForHash().size(key));

        return "OK";
    }

    @RequestMapping("/testZSet")
    public String testZSet() {
        flushAll();
        String key = "k1";
        redisTemplate.opsForZSet().add(key, "z", 10);
        redisTemplate.opsForZSet().add(key, "l", 20);
        redisTemplate.opsForZSet().add(key, "w", 30);

        System.out.println("size: " + redisTemplate.opsForZSet().size(key));
        System.out.println("range: " + redisTemplate.opsForZSet().range(key, 0, -1));
        System.out.println("rangeWithScores: " + redisTemplate.opsForZSet().rangeWithScores(key, 0, -1));
        redisTemplate.opsForZSet().remove(key, "l");

        System.out.println("score:'z' " + redisTemplate.opsForZSet().score(key, "z"));
        System.out.println("rank:'w' " + redisTemplate.opsForZSet().rank(key, "w"));
        System.out.println("size: " + redisTemplate.opsForZSet().size(key));


        return "OK";
    }

    private void flushAll() {
        redisTemplate.execute((RedisConnection redisConnection) -> {
            redisConnection.flushAll();
            return null;
        });
    }
}
