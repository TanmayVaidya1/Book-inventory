package com.pinnacle.books.author;

import com.pinnacle.books.users.UserService;
import com.pinnacle.books.users.Users;
import com.pinnacle.books.users.service.JWTUtils;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private JWTUtils jwtUtils;

    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }
    
    @GetMapping("/my-authors")
    public ResponseEntity<?> getAuthorsByLoggedInUser(HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Unauthorized"));
            }

            String token = authorizationHeader.substring(7);
            Long userId = jwtUtils.extractUserId(token);

            List<AuthorDTO> authors = authorService.getAuthorsByUserId(userId);
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }



    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuthorDTO>> getAuthorsByUserId(@PathVariable Long userId) {
        List<AuthorDTO> authors = authorService.getAuthorsByUserId(userId);
        if (authors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @PostMapping
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody AuthorDTO authorDTO, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        Long userId;
        try {
            userId = jwtUtils.extractUserId(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        authorDTO.setUserId(userId); // Associate author with the user
        return ResponseEntity.ok(authorService.createAuthor(authorDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable Long id, @RequestBody AuthorDTO authorDTO) {
        return ResponseEntity.ok(authorService.updateAuthor(id, authorDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @PostMapping("/upload-csv")
    public ResponseEntity<?> uploadAuthors(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            if (!CSVHelper.hasCSVFormat(file)) {
                return ResponseEntity.badRequest().body("Please upload a CSV file!");
            }

            String token = request.getHeader("Authorization").substring(7);
            Long userId = jwtUtils.extractUserId(token);

            authorService.saveAuthorsFromCSV(file, userId);

            return ResponseEntity.ok("Authors uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading CSV: " + e.getMessage());
        }
    }
}
