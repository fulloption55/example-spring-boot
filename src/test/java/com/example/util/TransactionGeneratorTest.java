package com.example.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class TransactionGeneratorTest {

    @InjectMocks
    private TransactionGenerator transactionGenerator;

    private static final int TRANSACTION_ID_LENGTH = 18;

    @Test
    public void test_get_successCase_returnString18Digits() {
        String transactionId = transactionGenerator.get();
        assertNotNull(transactionId);
        assertEquals(TRANSACTION_ID_LENGTH, transactionId.length());
    }

}
