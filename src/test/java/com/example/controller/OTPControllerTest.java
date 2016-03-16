package com.example.controller;


import com.example.model.OTP;
import com.example.model.OTPRequest;
import com.example.model.OTPResponse;
import com.example.model.OTPVerificationRequest;
import com.example.model.OTPVerificationResponse;
import com.example.util.TransactionGenerator;
import com.example.service.RedisService;
import com.example.service.SMSService;
import com.example.util.OTPGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import redis.clients.jedis.exceptions.JedisConnectionException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OTPControllerTest {
    @InjectMocks
    private OTPController otpController;

    @Mock
    private OTPGenerator otpGeneratorMock;
    @Mock
    private RedisService redisServiceMock;
    @Mock
    private OTP otpMock;
    @Mock
    private TransactionGenerator transactionGeneratorMock;
    @Mock
    private SMSService smsServiceMock;

    @Test
    public void testRequestOtp_success_responseContainsOtpData() {
        String mobileNumber = "0896977585";
        String otpKey = "f4e211b7-93cc-4f8e-a15f-a65f3a6ff544";
        String otpCode = "950485";
        String otpReference = "MMKV";
        String transactionIdMock = "1234567890";

        OTPRequest otpRequestMock = mock(OTPRequest.class);
        OTP otpMock = mock(OTP.class);

        when(transactionGeneratorMock.get()).thenReturn(transactionIdMock);

        when(otpMock.getKey()).thenReturn(otpKey);
        when(otpMock.getReference()).thenReturn(otpReference);
        when(otpMock.getOtp()).thenReturn(otpCode);
        when(otpRequestMock.getMobileNumber()).thenReturn(mobileNumber);
        when(otpGeneratorMock.generateNewOTP()).thenReturn(otpMock);

        OTPResponse response = otpController.requestOtp(otpRequestMock);

        verify(smsServiceMock, times(1)).sendSms(any(OTPRequest.class), any(), any());

        assertEquals(mobileNumber, response.getMobileNumber());
        assertEquals(otpReference, response.getOtpReference());
    }

    @Test
    public void testRequestOtp_saveOtpError_responseReferenceCodeNull_notCallSendSMS() {
        String mobileNumber = "0896977585";
        OTPRequest otpRequestMock = mock(OTPRequest.class);
        String transactionIdMock = "1234567890";
        OTP otpMock = mock(OTP.class);

        when(otpGeneratorMock.generateNewOTP()).thenReturn(otpMock);
        when(transactionGeneratorMock.get()).thenReturn(transactionIdMock);
        when(otpRequestMock.getMobileNumber()).thenReturn(mobileNumber);
        doThrow(JedisConnectionException.class).when(redisServiceMock).saveOtp(any(), any(), any());

        OTPResponse response = otpController.requestOtp(otpRequestMock);

        verify(smsServiceMock, never()).sendSms(any(OTPRequest.class), any(), any());
        assertNull(response.getOtpReference());
    }

    @Test
    public void testRequestOtp_sendSmsError_responseReferenceCodeNull() {
        String mobileNumber = "0896977585";
        OTPRequest otpRequestMock = mock(OTPRequest.class);
        String transactionIdMock = "1234567890";
        OTP otpMock = mock(OTP.class);

        when(otpGeneratorMock.generateNewOTP()).thenReturn(otpMock);
        when(transactionGeneratorMock.get()).thenReturn(transactionIdMock);
        when(otpRequestMock.getMobileNumber()).thenReturn(mobileNumber);
        doThrow(Exception.class).when(smsServiceMock).sendSms(any(OTPRequest.class), any(), any());

        OTPResponse response = otpController.requestOtp(otpRequestMock);

        assertNull(response.getOtpReference());
    }

    @Test
    public void testVerifyOtp_success_resultIsTrue() {

        String otpCode = "950485";
        String refCode = "AAAA";
        String transactionIdMock = "1234567890";
        String mobileNumber = "0896977585";

        OTPVerificationRequest requestMock = new OTPVerificationRequest();
        requestMock.setOtpCode(otpCode);
        requestMock.setMobileNumber(mobileNumber);
        requestMock.setOtpReference(refCode);


        when(transactionGeneratorMock.get()).thenReturn(transactionIdMock);
        when(redisServiceMock.getOtp(any(), any())).thenReturn(otpCode);

        OTPVerificationResponse response = otpController.otpVerify(requestMock);

        assertTrue(response.getResult());
    }

    @Test
    public void testVerifyOtp_Notfound_resultIsFalse() {

        String otpCode = "950485";
        String refCode = "AAAA";
        String transactionIdMock = "1234567890";
        String mobileNumber = "0896977585";

        OTPVerificationRequest requestMock = new OTPVerificationRequest();
        requestMock.setOtpCode(otpCode);
        requestMock.setMobileNumber(mobileNumber);
        requestMock.setOtpReference(refCode);


        when(transactionGeneratorMock.get()).thenReturn(transactionIdMock);
        when(redisServiceMock.getOtp(any(), any())).thenReturn(null);

        OTPVerificationResponse response = otpController.otpVerify(requestMock);

        assertFalse(response.getResult());
    }

    @Test
    public void testVerifyOtp_otpNotMatch_resultIsFalse() {

        String otpCode = "950485";
        String otpCodeNotMatch = "950000";
        String refCode = "AAAA";
        String transactionIdMock = "1234567890";
        String mobileNumber = "0896977585";

        OTPVerificationRequest requestMock = new OTPVerificationRequest();
        requestMock.setOtpCode(otpCode);
        requestMock.setMobileNumber(mobileNumber);
        requestMock.setOtpReference(refCode);


        when(transactionGeneratorMock.get()).thenReturn(transactionIdMock);
        when(redisServiceMock.getOtp(any(), any())).thenReturn(otpCodeNotMatch);

        OTPVerificationResponse response = otpController.otpVerify(requestMock);

        assertFalse(response.getResult());
    }

    @Test
    public void testVerifyOtp_gotRedisException_resultIsFalse() {

        String otpCode = "950485";
        String refCode = "AAAA";
        String transactionIdMock = "1234567890";
        String mobileNumber = "0896977585";

        OTPVerificationRequest requestMock = new OTPVerificationRequest();
        requestMock.setOtpCode(otpCode);
        requestMock.setMobileNumber(mobileNumber);
        requestMock.setOtpReference(refCode);


        when(transactionGeneratorMock.get()).thenReturn(transactionIdMock);
        when(redisServiceMock.getOtp(any(), any())).thenThrow(Exception.class);

        OTPVerificationResponse response = otpController.otpVerify(requestMock);

        assertFalse(response.getResult());
    }
}
