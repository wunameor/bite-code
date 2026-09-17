package com.wunamor.captchademo.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LogController {
    private Logger logger = LoggerFactory.getLogger(LogController.class);

    @RequestMapping("/print")
    public void print() {
        logger.error("this is a error log");
        logger.warn("this is a warn log");
        logger.info("this is a info log");
        logger.debug("this is a debug log");
        logger.trace("this is a trace log");
    }
}
