package com.pinnacle.books.users;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinnacle.books.users.DTO.ReqRes;
import com.pinnacle.books.users.service.JWTUtils;
import com.pinnacle.books.users.service.OTPService;
import com.pinnacle.books.users.service.PasswordResetService;
import com.pinnacle.books.users.service.UsersManagementService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;



@RestController
//@CrossOrigin(origins = "http://localhost:3000") // Enable CORS for frontend
//@RequestMapping("/api/users")
public class UserController {
	
	
    @Autowired
    private UsersManagementService usersManagementService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OTPService otpService;
    
    @Autowired
    private PasswordResetService passwordResetService;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private JWTUtils jwtUtils;



    
    

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reg) {
        return ResponseEntity.ok(usersManagementService.register(reg));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req) {
        return ResponseEntity.ok(usersManagementService.login(req));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req) {
        return ResponseEntity.ok(usersManagementService.refreshToken(req));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers() {
        return ResponseEntity.ok(usersManagementService.getAllUsers());
    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<ReqRes> getUserById(@PathVariable Long userId) {  // Changed to Long for consistency with the entity
        return ResponseEntity.ok(usersManagementService.getUsersById(userId));
    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Long userId, @RequestBody Users reqres) {  // Changed to Long for consistency
        return ResponseEntity.ok(usersManagementService.updateUser(userId, reqres));
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<ReqRes> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes response = usersManagementService.getMyInfo(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUser(@PathVariable Long userId) {  // Changed to Long for consistency
    	System.out.print(userId);
    	try {
    		
    	}catch (Exception e) {
    		e.printStackTrace();
    		
    	}
    	return ResponseEntity.ok(usersManagementService.deleteUser(userId));
    }
    
    @GetMapping("/auth/username")
    public ResponseEntity<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Get the username (email) from the authentication object
        Users user = usersManagementService.getUserByEmail(email); // Retrieve the user entity by email
        return ResponseEntity.ok(user.getName()); // Return the user's name as a response
    }
  
    
//  Request OTP for password reset
//    @PostMapping("auth/forgot-password")
//    public ResponseEntity<ReqRes> forgotPassword(@RequestBody ReqRes request) {
//        ReqRes response = new ReqRes();
//        try {
//            otpService.generateOTP(request.getEmail());
//            response.setStatusCode(200);
//            response.setMessage("OTP sent to email successfully.");
//            response.setEmailExists(true); 
//        } catch (Exception e) {
//            response.setStatusCode(500);
//            response.setMessage("Error generating OTP: " + e.getMessage());
//        }
//        return ResponseEntity.status(response.getStatusCode()).body(response);
//    }
    
    @PostMapping("auth/forgot-password")
    public ResponseEntity<ReqRes> forgotPassword(@RequestBody ReqRes request) {
        ReqRes response = new ReqRes();

        // Check if the email exists in the database
        boolean emailExists = usersRepository.findByEmail(request.getEmail()).isPresent();
        
        if (!emailExists) {
            response.setStatusCode(404);
            response.setMessage("Email does not exist.");
            response.setEmailExists(false);  // Explicitly set this to false
            return ResponseEntity.status(404).body(response);
        }

        try {
            otpService.generateOTP(request.getEmail());
            response.setStatusCode(200);
            response.setMessage("OTP sent to email successfully.");
            response.setEmailExists(true);  // ✅ Set true since email exists
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error generating OTP: " + e.getMessage());
            response.setEmailExists(false);
        }
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    // Reset password using OTP
    @PostMapping("auth/reset-password")
    public ResponseEntity<ReqRes> resetPassword(@RequestBody ReqRes request) {
        ReqRes response = passwordResetService.resetPassword(request.getEmail(), request.getOtp(), request.getPassword());
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    
 // ✅ Upload Profile Image
    @PostMapping("/upload-profile-image")
    public ResponseEntity<ReqRes> uploadProfileImage(
            @RequestParam("userId") Long userId, 
            @RequestParam("image") MultipartFile image) {
        try {
            byte[] imageBytes = image.getBytes();
            ReqRes response = usersManagementService.uploadProfileImage(userId, imageBytes);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            ReqRes response = new ReqRes();
            response.setStatusCode(500);
            response.setMessage("Error uploading profile image: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ✅ Get Profile Image
    @GetMapping("/get-profile-image/{userId}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long userId) {
        try {
            byte[] image = usersManagementService.getProfileImage(userId);
            if (image == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "image/jpeg"); // Adjust based on actual image type (e.g., PNG, JPEG)
            return ResponseEntity.ok().headers(headers).body(image);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
//    @PutMapping("/update/{userId}")
//    public ResponseEntity<ReqRes> updateUserWithProfileImage(
//            @PathVariable Long userId,
//            @RequestBody Users updatedUser,
//            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
//        try {
//            byte[] imageBytes = null;
//            if (profileImage != null) {
//                imageBytes = profileImage.getBytes();
//            }
//
//            ReqRes response = usersManagementService.updateUserWithProfileImage(userId, updatedUser, imageBytes);
//            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
//        } catch (Exception e) {
//            ReqRes errorResponse = new ReqRes();
//            errorResponse.setStatusCode(500);
//            errorResponse.setMessage("Error updating user: " + e.getMessage());
//            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    
//    @PutMapping("/update/{userId}")
//    public ResponseEntity<ReqRes> updateUserWithProfileImage(
//            @PathVariable Long userId,
//            @RequestPart("updatedUser") Users updatedUser,
//            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
//
//        try {
//            byte[] imageBytes = null;
//            if (profileImage != null) {
//                imageBytes = profileImage.getBytes();
//            }
//
//            ReqRes response = usersManagementService.updateUserWithProfileImage(userId, updatedUser, imageBytes);
//            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
//        } catch (Exception e) {
//            ReqRes errorResponse = new ReqRes();
//            errorResponse.setStatusCode(500);
//            errorResponse.setMessage("Error updating user: " + e.getMessage());
//            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    
    @PutMapping("/update/{userId}")
    public ResponseEntity<ReqRes> updateUserWithProfileImage(
            @PathVariable Long userId,
            @RequestParam("updatedUser") String updatedUserJson, // Expecting the user details as a JSON string
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
        
        try {
            // Convert the updatedUserJson to a Users object
            Users updatedUser = new ObjectMapper().readValue(updatedUserJson, Users.class);

            byte[] imageBytes = null;
            if (profileImage != null) {
                imageBytes = profileImage.getBytes(); // Convert file to byte array
            }

            ReqRes response = usersManagementService.updateUserWithProfileImage(userId, updatedUser, imageBytes);
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
        } catch (Exception e) {
            ReqRes errorResponse = new ReqRes();
            errorResponse.setStatusCode(500);
            errorResponse.setMessage("Error updating user: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/getUserInfo")
    public ResponseEntity<ReqRes> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            // Extract the token from "Bearer <token>"
        	 System.out.println(token + " tokenemail");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7).trim(); // Remove "Bearer " prefix and trim any extra spaces
            }

            // Extract the username (email) from the token
            System.out.println(token + " tokenemail");
            String email = jwtUtils.extractUsername(token);
            System.out.println(email + " email");

            // Fetch user details from the service layer using the email
            Users user = usersManagementService.getUserByEmail(email);

            // Prepare the response
            ReqRes response = new ReqRes();
            response.setUser(user);
            response.setStatusCode(200);
            response.setMessage("User info fetched successfully");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ReqRes errorResponse = new ReqRes();
            errorResponse.setStatusCode(500);
            errorResponse.setMessage("Error fetching user info: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    @GetMapping("auth/google-success")
//    public Map<String, String> googleLoginSuccess(Authentication authentication) {
//        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
//        String email = authToken.getPrincipal().getAttribute("email");
//        String name = authToken.getPrincipal().getAttribute("name");
//
//        Users user = userService.findByEmail(email);
//        if (user == null) {
//            user = new Users(email, name, "USER");
//            userService.save(user);
//        }
//
//        String token = jwtUtils.generateToken(user);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Google login successful!");
//        response.put("token", token);
//        return response;
//    }




    
    
}
