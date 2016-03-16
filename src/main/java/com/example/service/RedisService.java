package com.example.service;

import com.example.configuration.ApplicationConfiguration;
import com.example.model.OTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class RedisService {
    private final Logger logger = LogManager.getLogger(RedisService.class);

    @Autowired
    ApplicationConfiguration applicationConfiguration;

    public void saveOtp(OTP otpObject) {
        Jedis jedis = new Jedis(applicationConfiguration.getRedisDatabaseUrl());
        jedis.set(createKey(otpObject), otpObject.getOtp());
    }

    public void saveOtp(String mobileNumber, String otpReferance, String otpCode) {
        Jedis jedis = new Jedis(applicationConfiguration.getRedisDatabaseUrl());
        jedis.set(createKey(mobileNumber, otpReferance), otpCode);
        logger.info("Saved OTP to Redis");
    }

    public String getOtp(String refNumber, String refCode) {
        Jedis jedis = new Jedis(applicationConfiguration.getRedisDatabaseUrl());
        String otp = jedis.get(createKey(refNumber, refCode));

        return otp;
    }

    public void delOtp(String refNumber, String refCode) {
        Jedis jedis = new Jedis(applicationConfiguration.getRedisDatabaseUrl());
        jedis.del(createKey(refNumber, refCode));
    }

    private String createKey(OTP otpObject) {
        return createKey(otpObject.getKey(), otpObject.getReference());
    }

    private String createKey(String refNumber, String refCode) {
        return "otp:" + refNumber + ":" + refCode;
    }
}