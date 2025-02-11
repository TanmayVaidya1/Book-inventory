package com.pinnacle.books.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.pinnacle.books.users.Users;
import com.pinnacle.books.users.UsersRepository;

import java.util.Random;

@Service
public class OTPService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JavaMailSender mailSender;  

    private static final int OTP_EXPIRY_TIME = 5 * 60 * 1000;  // 5 minutes expiry

    public String generateOTP(String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String otp = String.format("%06d", new Random().nextInt(999999));  // 6-digit OTP
        long expiryTime = System.currentTimeMillis() + OTP_EXPIRY_TIME;

        user.setOtp(otp);
        user.setOtpExpiry(expiryTime);
        usersRepository.save(user);

        sendOTPEmail(email, otp);
        return otp;
    }

    private void sendOTPEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("tanmay.vaidya@pinnacle.in");  // Ensure this email is configured in your SMTP settings
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + "\nThis OTP will expire in 5 minutes.");

        mailSender.send(message);
    }

    public boolean isOtpValid(String email, String otp) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return user.getOtp() != null 
            && user.getOtp().equals(otp) 
            && user.getOtpExpiry() > System.currentTimeMillis();
    }
}
