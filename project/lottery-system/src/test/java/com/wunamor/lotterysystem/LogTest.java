package com.wunamor.lotterysystem;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class LogTest {

    private final static Logger logger = LoggerFactory.getLogger(LogTest.class);


    @Test
    void test() {
//        System.out.println("this is a log test");
        log.info("this is a log test");
    }
}
