package com.example.client;

import com.example.configuration.ApplicationConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class ThirdPartyClient {
    private final Logger logger = LogManager.getLogger(ThirdPartyClient.class);

    @Autowired
    ApplicationConfiguration applicationConfiguration;


    public String callSmsSender(String mobileNumber, String senderName, String message) {
        String host = applicationConfiguration.getThirdServiceHost();
        String pathURL = applicationConfiguration.getThirdServiceApiSmsPath();
        ResponseEntity<String> response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("serviceProvider", applicationConfiguration.getSmsServiceProvider());
            body.add("fromPhoneNo", senderName);
            body.add("toPhoneNo", mobileNumber);
            body.add("message", message);

            HttpEntity<MultiValueMap> entity = new HttpEntity<>(body, headers);

            HttpComponentsClientHttpRequestFactory httpComponentFactory = new HttpComponentsClientHttpRequestFactory();
            httpComponentFactory.setConnectTimeout(applicationConfiguration.getSmsServiceConnectionTimeout());
            httpComponentFactory.setReadTimeout(applicationConfiguration.getSmsServiceConnectionTimeout());

            RestTemplate restTemplate = new RestTemplate(httpComponentFactory);
            response = restTemplate.exchange(host + pathURL, HttpMethod.POST, entity, String.class, body);

            logger.info(String.format("SMS has been sent to [%s]", mobileNumber));

        } catch (HttpClientErrorException e) {
            logger.error(String.format("HTTP Error while send sms to [%s] cause : %s", mobileNumber, e));
            response = new ResponseEntity<>("HTTP Error while send sms", HttpStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            logger.error(String.format("Error while send sms to [%s] cause : %s", mobileNumber, e));
            response = new ResponseEntity<>("Error while send sms", HttpStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }

        return response.getBody();
    }

}