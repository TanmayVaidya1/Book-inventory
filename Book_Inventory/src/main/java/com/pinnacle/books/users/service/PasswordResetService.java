package com.pinnacle.books.users.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.pinnacle.books.users.Users;
import com.pinnacle.books.users.UsersRepository;
import com.pinnacle.books.users.DTO.ReqRes;

@Service
public class PasswordResetService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private OTPService otpService;  

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ReqRes resetPassword(String email, String otp, String newPassword) {
        ReqRes response = new ReqRes();

        try {
            if (!otpService.isOtpValid(email, otp)) {
                response.setStatusCode(400);
                response.setMessage("Invalid or expired OTP.");
                return response;
            }

            Users user = usersRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setPassword(passwordEncoder.encode(newPassword));

            // Invalidate OTP after successful reset
            user.setOtp(null);
            user.setOtpExpiry(null);
            usersRepository.save(user);

            response.setStatusCode(200);
            response.setMessage("Password reset successful.");
            response.setEmailExists(true);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error resetting password: " + e.getMessage());
        }
        return response;
    }
}
