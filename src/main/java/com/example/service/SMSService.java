package com.example.service;


import com.example.client.ThirdPartyClient;
import com.example.model.OTPRequest;
import com.example.util.OTPGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SMSService {
    private final Logger logger = LogManager.getLogger(OTPGenerator.class);

    @Autowired
    ThirdPartyClient thirdPartyClient;

    public void sendSms(OTPRequest otpRequest, String otpCode, String otpReference) {

        logger.info(String.format("Prepare message for OTP ref [%s]", otpReference));
        String message = String.format(otpRequest.getSmsTemplate(), otpCode, otpReference);
        thirdPartyClient.callSmsSender(otpRequest.getMobileNumber(), otpRequest.getSenderName(), message);
    }


}
