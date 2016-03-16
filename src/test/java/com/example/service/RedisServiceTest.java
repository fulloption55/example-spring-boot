package com.example.service;

import com.example.configuration.ApplicationConfiguration;
import com.example.model.OTP;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(value = PowerMockRunner.class)
@PrepareForTest(RedisService.class)
@PowerMockIgnore({"javax.management.*"})
public class RedisServiceTest {
    @InjectMocks
    private RedisService redisService;

    @Mock
    private ApplicationConfiguration configMock;
    @Mock
    private Jedis jedisMock;
    @Mock
    private OTP otpMock;

    @Before
    public void setup() throws Exception {
        whenNew(Jedis.class).withAnyArguments().thenReturn(jedisMock);
    }

    @Test
    public void testSaveOtp_success_otpWasSaved() throws Exception {
        redisService.saveOtp(otpMock);

        verify(jedisMock).set(anyString(), anyString());
    }

    @Test
    public void testSaveOtp_with_string_success_otpWasSaved() throws Exception {
        redisService.saveOtp("0800000011", "REFC", "123456");

        verify(jedisMock).set("otp:0800000011:REFC", "123456");
    }

    @Test
    public void testGetOtp_success_otp() {
        when(jedisMock.get(anyString())).thenReturn("otpMock");

        String otp = redisService.getOtp("referenceNumber", "referenceCode");

        assertEquals("otpMock", otp);
    }

    @Test
    public void testDelOtp_success_otpWasDeleted() {
        redisService.delOtp("referenceNumber", "referenceCode");

        verify(jedisMock).del(anyString());
    }
}
