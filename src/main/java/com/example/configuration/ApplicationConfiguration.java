package com.example.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfiguration {


    @Value("${redis.database.url}")
    private String redisDatabaseUrl;

    @Value("${3rd.service.host}")
    private String thirdServiceHost;

    @Value("${3rd.service.api.sms.path}")
    private String thirdServiceApiSmsPath;

    @Value("${sms.service.provider}")
    private String smsServiceProvider;

    @Value("${sms.service.connection.timeout}")
    private int smsServiceConnectionTimeout;

    @Value("${sms.service.read.timeout}")
    private int smsServiceReadTimeout;

    public String getSmsServiceProvider() {
        return smsServiceProvider;
    }

    public String getRedisDatabaseUrl() {
        return redisDatabaseUrl;
    }

    public String getThirdServiceHost() {
        return thirdServiceHost;
    }

    public String getThirdServiceApiSmsPath() {
        return thirdServiceApiSmsPath;
    }

    public int getSmsServiceConnectionTimeout() {
        return smsServiceConnectionTimeout;
    }

    public int getSmsServiceReadTimeout() {
        return smsServiceReadTimeout;
    }
}
