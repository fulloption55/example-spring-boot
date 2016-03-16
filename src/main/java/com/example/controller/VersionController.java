package com.example.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {
    private final Logger logger = LogManager.getLogger(VersionController.class);

    @Value("${application.version}")
    String applicationVersion;

    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public String getVersion() {
        ThreadContext.put("transactionId", "1");

        logger.info("========== Received get version request ==========");

        logger.info(String.format("Version: %s", applicationVersion));

        logger.info("========== Response get version request ==========");

        return applicationVersion;
    }


}
