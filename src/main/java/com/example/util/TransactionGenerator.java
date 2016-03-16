package com.example.util;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


@Component
public class TransactionGenerator {
    private static final int RANDOM_NUMBER_LENGTH = 6;
    private static final String TIMESTAMP_FORMAT = "yyMMddHHmmss";
    private static final Locale TIMESTAMP_LOCALE = Locale.US;

    public String get() {
        String timestamp = generateTimestamp(TIMESTAMP_FORMAT, TIMESTAMP_LOCALE);
        String randomNumber = randomNumber(RANDOM_NUMBER_LENGTH);

        return timestamp.concat(randomNumber);
    }

    private String generateTimestamp(String format, Locale locale) {
        return new SimpleDateFormat(format, locale).format(new Date());
    }

    private String randomNumber(int length) {
        Random random = new Random();
        String randomNumber = "";

        for (int i = 0; i < length; i++) {
            randomNumber += random.nextInt(9);
        }

        return randomNumber;
    }
}
