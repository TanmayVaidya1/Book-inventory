package com.pinnacle.books.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService{

    @Autowired
    private UsersRepository userRepository;
    
  @Autowired
 private JavaMailSender mailSender;

//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow();
    }
    
    public Users getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }
    
    /**
     * Retrieve the currently authenticated user.
     *
     * @return the authenticated `Users` object
     */
    public Users getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (Users) loadUserByUsername(userDetails.getUsername());
    }

    /**
     * Retrieve the currently authenticated user's ID.
     *
     * @return the userId of the authenticated user
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }
    
//    public Users saveUser(Users user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt password
//        return userRepository.save(user);
//    }
    
    public Optional<Users> findById(Long userId) {
        return userRepository.findById(userId);
    }
    
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }
    
    public void deleteUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        userRepository.delete(user);
    }
    
    public UserService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Users findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    

    public void save(Users user) {
        userRepository.save(user);
    }
    
//    // Generate reset token and send email
//    public String generateAndSendResetToken(String email) {
//        // Check if the user exists
//        Users user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
//
//        System.out.println(user +"sakib");
//        // Generate a random reset token
//        String resetToken = UUID.randomUUID().toString();
//        user.setResetToken(resetToken); // Assuming `resetToken` field exists in `Users` class
//        userRepository.save(user);
//
//        // Send the reset token to the user via email
//        sendResetPasswordEmail(email, resetToken);
//        return "Password reset email sent successfully.";
//    }
//
//    // Send password reset email with the token
//    private void sendResetPasswordEmail(String email, String resetToken) {
//        String resetUrl = "http://localhost:8083/auth/reset-password?token=" + resetToken;
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Password Reset Request");
//        message.setText("To reset your password, click the following link: " + resetUrl);
//        mailSender.send(message);
//    }
//
//    // Verify the reset token and allow password reset
//    public String resetPassword(String token, String newPassword) {
//        // Find the user by reset token
//        Optional<Users> optionalUser = userRepository.findByResetToken(token);
//
//        if (optionalUser.isEmpty()) {
//            return "Invalid reset token.";
//        }
//
//        Users user = optionalUser.get();
//
//        // Encrypt the new password and update it
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String encodedPassword = passwordEncoder.encode(newPassword);
//        user.setPassword(encodedPassword);
//        user.setResetToken(null); // Clear the reset token after use
//        userRepository.save(user);
//
//        return "Password has been reset successfully.";
//    }
   

    
    

//    @Autowired
//   private JavaMailSender mailSender;
//
//    public Users registerUser(Users user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return userRepository.save(user);
//    }
//
//    public boolean loginUser(String email, String password) {
//        Optional<Users> user = userRepository.findByEmail(email);
//        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
//            return true; // Login successful
//        }
//        throw new RuntimeException("Invalid email or password");
//    }
//
//    // Check if email exists for forgot password flow
//    public boolean checkEmailExists(String email) {
//        Optional<Users> user = userRepository.findByEmail(email);
//        return user.isPresent();
//    }

    // Create a password reset token for the user
//    public String createPasswordResetToken(String email) {
//        String token = UUID.randomUUID().toString();
//        Optional<Users> user = userRepository.findByEmail(email);
//        user.ifPresent(u -> {
//            u.setResetToken(token);
//            userRepository.save(u); // Save token to DB
//        });
//        return token;
//    }

    // Send password reset email
//    public void sendPasswordResetEmail(String email, String token) {
//        String resetUrl = "http://localhost:5173/reset-password?token=" + token;
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Password Reset Request");
//        message.setText("To reset your password, click the following link: " + resetUrl);
//        mailSender.send(message);
//    }
//
//    // Reset password using the token
//    public boolean resetPassword(String token, String newPassword) {
//        Optional<Users> user = userRepository.findByResetToken(token);
//        if (user.isPresent()) {
//            user.get().setPassword(passwordEncoder.encode(newPassword));
//            user.get().setResetToken(null);  // Clear the reset token
//            userRepository.save(user.get());
//            return true;
//        }
//        return false;
//    }


}
