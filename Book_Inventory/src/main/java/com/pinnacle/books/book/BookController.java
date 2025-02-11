package com.pinnacle.books.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.pinnacle.books.users.service.JWTUtils;

import java.util.List;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {

    @Autowired
    private BookService bookService;
    
//    private final BookService bookService;
    private final JWTUtils jwtUtils;

    // Inject JWTUtils to extract user ID from the token
    public BookController(BookService bookService, JWTUtils jwtUtils) {
        this.bookService = bookService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> bookDTOs = bookService.getAllBooks();
        return ResponseEntity.ok(bookDTOs);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<BookDTO>> getBooksByCategory(@PathVariable Long categoryId) {
        List<BookDTO> books = bookService.getBooksByCategory(categoryId);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<BookDTO>> getBooksByAuthor(@PathVariable Long authorId) {
        List<BookDTO> books = bookService.getBooksByAuthor(authorId);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookDTO>> getBooksByUser(@PathVariable Long userId) {
        List<BookDTO> books = bookService.getBooksByUser(userId);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Books> createBook(@PathVariable Long userId, @RequestBody Books book) {
        Books createdBook = bookService.createBook(book, userId);
        return ResponseEntity.status(201).body(createdBook);
    }

//    @PutMapping("/{bookId}/user/{userId}")
//    public ResponseEntity<Books> updateBook(@PathVariable Long bookId, @PathVariable Long userId, @RequestBody Books updatedBook) {
//        Books updated = bookService.updateBookByUser(bookId, updatedBook, userId);
//        return ResponseEntity.ok(updated);
//    }
    
//    @PutMapping("/{bookId}")
//    public ResponseEntity<Books> updateBook(@PathVariable Long bookId, @RequestBody Books updatedBook) {
//        Books updated = bookService.updateBookByUser(bookId, updatedBook);
//        return ResponseEntity.ok(updated);
//    }
    
    @PutMapping("/{bookId}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long bookId, @RequestBody BookDTO bookDTO) {
        Books updatedBook = bookService.updateBook(bookId, bookDTO);
        BookDTO responseDTO = new BookDTO(
                updatedBook.getBookId(),
                updatedBook.getTitle(),
                updatedBook.getIsbn(),
                updatedBook.getAuthor().getAuthorId(),
                updatedBook.getAuthor().getAuthorName(),
                updatedBook.getCategory().getCategoryId(),
                updatedBook.getCategory().getName(),
                updatedBook.getUser().getUserId(),
                updatedBook.getPrice(),
                updatedBook.getQuantity(),
                updatedBook.getDescription()
        );
        return ResponseEntity.ok(responseDTO);
    }



    @DeleteMapping("/{bookId}/user/{userId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId, @PathVariable Long userId) {
        bookService.deleteBookByUser(bookId, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getBookCount() {
        long count = bookService.getBookCount();
        return ResponseEntity.ok(count);
    }
    
    
    @GetMapping("/download/{userId}")
    public ResponseEntity<Resource> downloadBooks(@PathVariable Long userId, @RequestParam(required = false) String fileType) throws IOException {
        List<BookDTO> books = bookService.getBooksByUser(userId);

        if (fileType == null || fileType.isEmpty()) {
            fileType = "csv";
        }

        // Create CSV content
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        writer.write("Book ID,Title,ISBN,Author ID,Author Name,Category ID,Category Name,User ID,Price,Quantity,Description\n");
        for (BookDTO book : books) {
            writer.write(String.format("%d,%s,%s,%d,%s,%d,%s,%d,%.2f,%d,%s\n",
                    book.getBookId(),
                    book.getTitle(),
                    book.getIsbn(),
                    book.getAuthorId(),
                    book.getAuthorName(),
                    book.getCategoryId(),
                    book.getCategoryName(),
                    book.getUserId(),
                    book.getPrice(),
                    book.getQuantity(),
                    book.getDescription()));
        }

        writer.flush();
        writer.close();

        byte[] fileContent = outputStream.toByteArray();
        ByteArrayResource resource = new ByteArrayResource(fileContent);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books_" + userId + "." + fileType);
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileContent.length)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
    
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadBooks(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body("File is empty. Please upload a valid CSV file.");
//        }
//        
//        try {
//            bookService.bulkUploadBooks(file);
//            return ResponseEntity.ok("Books uploaded successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
//        }
//    }
    
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadBooks(
//            @RequestParam("file") MultipartFile file,
//            @RequestHeader("Authorization") String authHeader) {
//
//        // Extract user ID from the JWT token
//        Long loggedInUserId = extractUserIdFromToken(authHeader);
//
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body("File is empty. Please upload a valid CSV file.");
//        }
//
//        try {
//            // Call the service method to handle the file upload
//            bookService.bulkUploadBooks(file, loggedInUserId);
//            return ResponseEntity.ok("Books uploaded successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
//        }
//    }
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadBooks(
//            @RequestParam("file") MultipartFile file,
//            @RequestHeader("Authorization") String authHeader) {
//
//        // Extract user ID from the JWT token
//        Long loggedInUserId = extractUserIdFromToken(authHeader);
//
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body("File is empty. Please upload a valid CSV file.");
//        }
//
//        try {
//            // Call the service method to handle the file upload
//            bookService.bulkUploadBooks(file, loggedInUserId);
//            return ResponseEntity.ok("Books uploaded successfully!");
//        } catch (RuntimeException e) {
//            // Handle custom exceptions like duplicate entries or invalid data
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
//        }
//    }
    
//    @PostMapping("/upload")
//    public ResponseEntity<ApiResponse> uploadBooks(
//            @RequestParam("file") MultipartFile file,
//            @RequestHeader("Authorization") String authHeader) {
//
//        // Extract user ID from the JWT token
//        Long loggedInUserId = extractUserIdFromToken(authHeader);
//
//        if (file.isEmpty()) {
//            ApiResponse response = new ApiResponse(false, "File is empty. Please upload a valid CSV file.");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        try {
//            // Call the service method to handle the file upload
//            bookService.bulkUploadBooks(file, loggedInUserId);
//            ApiResponse response = new ApiResponse(true, "Books uploaded successfully!");
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException e) {
//            ApiResponse response = new ApiResponse(false, e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        } catch (Exception e) {
//            ApiResponse response = new ApiResponse(false, "Error processing file: " + e.getMessage());
//            return ResponseEntity.internalServerError().body(response);
//        }
//    }
    
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadBooks(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {

        // Extract user ID from the JWT token
        Long loggedInUserId = extractUserIdFromToken(authHeader);

        if (file.isEmpty()) {
            ApiResponse response = new ApiResponse(false, "File is empty. Please upload a valid CSV file.", 400);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Call the service method to handle the file upload
            bookService.bulkUploadBooks(file, loggedInUserId);
            ApiResponse response = new ApiResponse(true, "Books uploaded successfully!", 200);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ApiResponse response = new ApiResponse(false, e.getMessage(), 400);  // 400 for bad request
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "Error processing file: " + e.getMessage(), 500);  // 500 for internal server error
            return ResponseEntity.internalServerError().body(response);
        }
    }




    // Extract userId from the Authorization header (JWT token)
    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            return jwtUtils.extractUserId(token);  // Use JWTUtils to extract the userId
        } else {
            throw new RuntimeException("Invalid Authorization header.");
        }
    }


    
}

