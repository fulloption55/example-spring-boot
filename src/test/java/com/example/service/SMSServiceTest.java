package com.example.service;

import com.example.model.OTPRequest;
import com.example.client.ThirdPartyClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SMSServiceTest {

    @InjectMocks
    SMSService smsService;

    @Mock
    ThirdPartyClient thirdPartyClient;


    @Test
    public void test_sendSms_successCase_notificationClientWasCalledOnceWithCompletedMessage() {
        OTPRequest otpRequestMock = mock(OTPRequest.class);
        String otpRef = "ABCD";
        String otpCode = "123456";
        String mobileNumber = "+66896977777";
        String senderName = "Fulloption";
        String smsTemplate = "Greeting Loser It's your OTP %s (%s)";
        String expectedCompleteMessage = "Greeting Loser It's your OTP 123456 (ABCD)";
        when(otpRequestMock.getMobileNumber()).thenReturn(mobileNumber);
        when(otpRequestMock.getSenderName()).thenReturn(senderName);
        when(otpRequestMock.getSmsTemplate()).thenReturn(smsTemplate);

        smsService.sendSms(otpRequestMock, otpCode, otpRef);

        verify(thirdPartyClient, times(1)).callSmsSender(mobileNumber, senderName, expectedCompleteMessage);
    }
}
