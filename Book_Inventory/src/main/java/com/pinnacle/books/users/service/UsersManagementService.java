package com.pinnacle.books.users.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pinnacle.books.users.Users;
import com.pinnacle.books.users.UsersRepository;
import com.pinnacle.books.users.DTO.ReqRes;

@Service
public class UsersManagementService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ReqRes register(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();
        try {
            
            Users user = new Users();
            user.setEmail(registrationRequest.getEmail());
            user.setCity(registrationRequest.getCity());
            user.setRole(registrationRequest.getRole());
            user.setName(registrationRequest.getName());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

            Users savedUser = usersRepository.save(user);

            if (savedUser.getUserId() > 0) {
                resp.setUser(savedUser);
                resp.setMessage("User saved successfully");
                resp.setStatusCode(200);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError("Error occurred while saving user: " + e.getMessage());
        }
        return resp;
    }

    public ReqRes login(ReqRes loginRequest) {
        ReqRes response = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), loginRequest.getPassword()));

            var user = usersRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid email or password"));
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully logged in");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error during login: " + e.getMessage());
        }
        return response;
    }

    public ReqRes refreshToken(ReqRes refreshTokenRequest) {
        ReqRes response = new ReqRes();
        try {
            String email = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            Users user = usersRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Invalid token"));

            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), user)) {
                var jwt = jwtUtils.generateToken(user);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hrs");
                response.setMessage("Token refreshed successfully");
            }

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error during token refresh: " + e.getMessage());
        }
        return response;
    }

    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();

        try {
            List<Users> result = usersRepository.findAll();
            if (!result.isEmpty()) {
                reqRes.setUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }

    public ReqRes getUsersById(Long id) {
        ReqRes reqRes = new ReqRes();
        try {
            Users user = usersRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
            reqRes.setUser(user);
            reqRes.setStatusCode(200);
            reqRes.setMessage("User fetched successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error fetching user: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes deleteUser(Long userId) {
        ReqRes reqRes = new ReqRes();
        try {
            if (usersRepository.existsById(userId)) {
                usersRepository.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error during user deletion: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes updateUser(Long userId, Users updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Users existingUser = usersRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found for update"));

            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setName(updatedUser.getName());
            existingUser.setCity(updatedUser.getCity());
            existingUser.setRole(updatedUser.getRole());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            Users savedUser = usersRepository.save(existingUser);
            reqRes.setUser(savedUser);
            reqRes.setStatusCode(200);
            reqRes.setMessage("User updated successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error during user update: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes getMyInfo(String email) {
        ReqRes reqRes = new ReqRes();
        try {
            Users user = usersRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            reqRes.setUser(user);
            reqRes.setStatusCode(200);
            reqRes.setMessage("User info fetched successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error fetching user info: " + e.getMessage());
        }
        return reqRes;
    }
    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
    
    // ✅ Upload Profile Image
    public ReqRes uploadProfileImage(Long userId, byte[] image) {
        ReqRes response = new ReqRes();
        try {
            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setProfileImage(image);  // Set the profile image as a byte array
            usersRepository.save(user);  // Save the user with the updated profile image

            response.setStatusCode(200);
            response.setMessage("Profile image uploaded successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error uploading profile image: " + e.getMessage());
        }
        return response;
    }

    // ✅ Get Profile Image
    public byte[] getProfileImage(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getProfileImage();  // Return the profile image byte array
    }
    
    public ReqRes updateUserWithProfileImage(Long userId, Users updatedUser, byte[] profileImage) {
        ReqRes reqRes = new ReqRes();
        try {
            // Fetch the existing user from the database
            Users existingUser = usersRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found for update"));

            // Update user details
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setName(updatedUser.getName());
            existingUser.setCity(updatedUser.getCity());
            existingUser.setRole(updatedUser.getRole());

            // Check if the password is provided, and update it
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            // Update the profile image if provided
            if (profileImage != null && profileImage.length > 0) {
                existingUser.setProfileImage(profileImage);  // Set the profile image as byte array
            }

            // Save the updated user information to the database
            Users savedUser = usersRepository.save(existingUser);

            // Prepare the response
            reqRes.setUser(savedUser);
            reqRes.setStatusCode(200);
            reqRes.setMessage("User and profile image updated successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error during user and profile image update: " + e.getMessage());
        }
        return reqRes;
    }


    
}
