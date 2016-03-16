package com.example.service;


import com.example.model.OTP;
import com.example.util.OTPGenerator;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class OTPGeneratorTest {
	private OTPGenerator otpGenerator = new OTPGenerator();

	@Test
	public void testGenerateNewOTP_success_keyAndOtpAndReferenceIsNotNull() {
		OTP otp = otpGenerator.generateNewOTP();

		assertNotNull(otp.getKey());
		assertNotNull(otp.getOtp());
		assertNotNull(otp.getReference());
	}
}
