package com.example.controller;


import com.example.model.OTP;
import com.example.model.OTPRequest;
import com.example.model.OTPResponse;
import com.example.model.OTPVerificationRequest;
import com.example.model.OTPVerificationResponse;
import com.example.util.OTPGenerator;
import com.example.service.RedisService;
import com.example.service.SMSService;
import com.example.util.TransactionGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RestController
public class OTPController {
    private final Logger logger = LogManager.getLogger(OTPController.class);

    @Autowired
    private OTPGenerator otpGenerator;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TransactionGenerator transactionGenerator;

    @Autowired
    private SMSService smsService;

    @RequestMapping(value = "/v2/otp", method = RequestMethod.POST)
    @Produces(MediaType.APPLICATION_JSON)
    public OTPResponse requestOtp(@RequestBody OTPRequest otpRequest) {
        String transactionId = transactionGenerator.get();
        ThreadContext.put("transactionId", transactionId);
        logger.info(String.format("========== Received OTP request for mobile number [%s] ==========", otpRequest.getMobileNumber()));

        OTPResponse otpResponse = new OTPResponse();
        String mobileNumber = otpRequest.getMobileNumber();

        try {
            OTP otpObject = otpGenerator.generateNewOTP();

            otpResponse.setMobileNumber(mobileNumber);
            otpResponse.setOtpReference(otpObject.getReference());

            redisService.saveOtp(mobileNumber, otpObject.getReference(), otpObject.getOtp());

            logger.info(String.format("OTPRef: [%s], OTPCode: [%s]", otpObject.getReference(), otpObject.getOtp()));

            smsService.sendSms(otpRequest, otpObject.getOtp(), otpObject.getReference());

        } catch (JedisConnectionException e) {
            logger.error(String.format("Error while saving OTP into Radis for mobile number [%s] : %s", otpRequest.getMobileNumber(), e));
            e.printStackTrace();
        } catch (Exception e) {
            logger.error(String.format("Error request OTP for mobile number [%s] : %s", otpRequest.getMobileNumber(), e));
            e.printStackTrace();
        }

        logger.info(String.format("========== Return OTP response for mobile number [%s] ==========", otpRequest.getMobileNumber()));

        return otpResponse;
    }

    @RequestMapping(value = "/v2/otp/verification", method = RequestMethod.POST)
    @Produces(MediaType.APPLICATION_JSON)
    public OTPVerificationResponse otpVerify(@RequestBody OTPVerificationRequest otpVerificationRequest) {
        String transactionId = transactionGenerator.get();
        ThreadContext.put("transactionId", transactionId);

        OTPVerificationResponse otpVerificationResponse = new OTPVerificationResponse();
        String mobileNumber = otpVerificationRequest.getMobileNumber();
        String otpReference = otpVerificationRequest.getOtpReference();

        logger.info(String.format("========== Received OTP verification request for mobile number [%s] with [%s] ==========", mobileNumber, otpReference));

        try {

            String otp = redisService.getOtp(mobileNumber, otpReference);

            if (otp == null) {
                otpVerificationResponse.setResult(false);
                logger.error(String.format("Not found OTP for [%s] with reference [%s]", mobileNumber, otpReference));
            } else if (otp.equals(otpVerificationRequest.getOtpCode())) {
                redisService.delOtp(mobileNumber, otpReference);
                otpVerificationResponse.setResult(true);
                logger.info(String.format("Found OTP for [%s] with reference [%s]", mobileNumber, otpReference));
            } else {
                otpVerificationResponse.setResult(false);
                logger.error(String.format("Incorrect OTP code for [%s] with reference [%s]", mobileNumber, otpReference));
            }

        } catch (Exception e) {
            otpVerificationResponse.setResult(false);
            logger.error(String.format("Error while verify OTP in Radis for mobile number [%s] : %s", mobileNumber, e));
            e.printStackTrace();
        }

        logger.info(String.format("========== Return OTP verification request for mobile number [%s] with [%s] and result success is [%s]  ==========", mobileNumber, otpReference, otpVerificationResponse.getResult()));

        return otpVerificationResponse;
    }

}
