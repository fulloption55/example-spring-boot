package com.example.client;

import com.example.configuration.ApplicationConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(value = PowerMockRunner.class)
@PrepareForTest(ThirdPartyClient.class)
@PowerMockIgnore({"javax.management.*"})
public class thirdPartyClientTest {

    @InjectMocks
    ThirdPartyClient thirdPartyClient;

    @Mock
    ApplicationConfiguration applicationConfigurationMock;

    private int smsServiceConnectionTimeout;
    private int smsServiceReadTimeout;
    private String smsServiceProvider;
    private String smsServiceHost;
    private String smsServiceApiPath;

    @Before
    public void setup() {
        smsServiceConnectionTimeout = 1000;
        smsServiceReadTimeout = 5000;
        smsServiceProvider = "FH";
        smsServiceHost = "https://alpha-internal-notif.tmn-dev.com";
        smsServiceApiPath = "/v1/sms";
        when(applicationConfigurationMock.getThirdServiceHost()).thenReturn(smsServiceHost);
        when(applicationConfigurationMock.getThirdServiceApiSmsPath()).thenReturn(smsServiceApiPath);
        when(applicationConfigurationMock.getSmsServiceConnectionTimeout()).thenReturn(smsServiceConnectionTimeout);
        when(applicationConfigurationMock.getSmsServiceReadTimeout()).thenReturn(smsServiceReadTimeout);
        when(applicationConfigurationMock.getSmsServiceProvider()).thenReturn(smsServiceProvider);
    }

    @Test
    public void test_sendSmsSender_successCase_restTemplateExchangeWasCalledOnce() throws Exception {
        RestTemplate restTemplateMock = mock(RestTemplate.class);
        ResponseEntity<String> responseMock = mock(ResponseEntity.class);
        String mobileNumber = "0896977585";
        String senderName = "TrueMoney";
        String completedMessage = "สวัสดี Exalter รหัสOTP ของเอ็งคือ 123456 (ABCD)";
        whenNew(RestTemplate.class).withAnyArguments().thenReturn(restTemplateMock);
        when(responseMock.getBody()).thenReturn("any String");
        when(restTemplateMock.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class), any(MultiValueMap.class))).thenReturn(responseMock);

        thirdPartyClient.callSmsSender(mobileNumber, senderName, completedMessage);
        verify(restTemplateMock, times(1)).exchange(eq(smsServiceHost + smsServiceApiPath), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class), any(MultiValueMap.class));
    }

    @Test
    public void test_sendSmsSender_successCase_returnStringClass() throws Exception {
        RestTemplate restTemplateMock = mock(RestTemplate.class);
        ResponseEntity<String> responseMock = mock(ResponseEntity.class);
        String mobileNumber = "0896977585";
        String senderName = "TrueMoney";
        String completedMessage = "สวัสดี Exalter รหัสOTP ของเอ็งคือ 123456 (ABCD)";
        whenNew(RestTemplate.class).withAnyArguments().thenReturn(restTemplateMock);
        when(responseMock.getBody()).thenReturn("any String");
        when(restTemplateMock.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class), any(MultiValueMap.class))).thenReturn(responseMock);

        Object responseBody = thirdPartyClient.callSmsSender(mobileNumber, senderName, completedMessage);

        assertEquals(String.class, responseBody.getClass());
    }

    @Test
    public void test_sendSmsSender_throwHttpClientErrorException_returnResponseBodyWithErrorMessage() throws Exception {
        RestTemplate restTemplateMock = mock(RestTemplate.class);
        ResponseEntity<String> responseMock = mock(ResponseEntity.class);
        String mobileNumber = "0896977585";
        String senderName = "TrueMoney";
        String completedMessage = "สวัสดี Exalter รหัสOTP ของเอ็งคือ 123456 (ABCD)";
        whenNew(RestTemplate.class).withAnyArguments().thenReturn(restTemplateMock);
        when(responseMock.getBody()).thenReturn("any String");
        when(restTemplateMock.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class), any(MultiValueMap.class))).thenThrow(HttpClientErrorException.class);

        Object responseBody = thirdPartyClient.callSmsSender(mobileNumber, senderName, completedMessage);
        assertEquals("HTTP Error while send sms", responseBody);
    }

    @Test
    public void test_sendSmsSender_throwException_returnResponseBodyWithErrorMessage() throws Exception {
        RestTemplate restTemplateMock = mock(RestTemplate.class);
        ResponseEntity<String> responseMock = mock(ResponseEntity.class);
        String mobileNumber = "0896977585";
        String senderName = "TrueMoney";
        String completedMessage = "สวัสดี Exalter รหัสOTP ของเอ็งคือ 123456 (ABCD)";
        whenNew(RestTemplate.class).withAnyArguments().thenReturn(restTemplateMock);
        when(responseMock.getBody()).thenReturn("any String");
        when(restTemplateMock.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class), any(MultiValueMap.class))).thenThrow(Exception.class);

        Object responseBody = thirdPartyClient.callSmsSender(mobileNumber, senderName, completedMessage);
        assertEquals("Error while send sms", responseBody);
    }

}
