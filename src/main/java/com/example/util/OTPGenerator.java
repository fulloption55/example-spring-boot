package com.example.util;


import com.example.model.OTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OTPGenerator {
    private final Logger logger = LogManager.getLogger(OTPGenerator.class);

    public OTP generateNewOTP() {
        String refNumber = UUID.randomUUID().toString();
        String refCode = RandomUtil.genRandomString(4);
        String otpCode = RandomUtil.genRandomNumber(6);
        logger.info("Generated OTP data");
        return new OTP(refNumber, refCode, otpCode);
    }

}