package com.pinnacle.books.book;

import com.pinnacle.books.category.Category;
import com.pinnacle.books.category.CategoryRepository;
import com.pinnacle.books.users.Users;
import com.pinnacle.books.users.UsersRepository;
import com.pinnacle.books.author.Author;
import com.pinnacle.books.author.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.io.IOException;




@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UsersRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, UsersRepository usersRepository,
                           CategoryRepository categoryRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = usersRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public Books createBook(Books book, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        book.setUser(user);

        if (book.getCategory() != null) {
            Category category = categoryRepository.findById(book.getCategory().getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            book.setCategory(category);
        }

        if (book.getAuthor() != null) {
            Author author = authorRepository.findById(book.getAuthor().getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Author not found"));
            book.setAuthor(author);
        }

        return bookRepository.save(book);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        List<Books> books = bookRepository.findAll();

        return books.stream()
                .map(book -> new BookDTO(
                        book.getBookId(),
                        book.getTitle(),
                        book.getIsbn(),
                        book.getAuthor().getAuthorId(),
                        book.getAuthor().getAuthorName(),
                        book.getCategory().getCategoryId(),
                        book.getCategory().getName(),
                        book.getUser().getUserId(),
                        book.getPrice(),
                        book.getQuantity(),
                        book.getDescription()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> getBooksByUser(Long userId) {
        List<Books> books = bookRepository.findByUser_UserId(userId);

        return books.stream()
                .map(book -> new BookDTO(
                        book.getBookId(),
                        book.getTitle(),
                        book.getIsbn(),
                        book.getAuthor().getAuthorId(),
                        book.getAuthor().getAuthorName(),
                        book.getCategory().getCategoryId(),
                        book.getCategory().getName(),
                        book.getUser().getUserId(),
                        book.getPrice(),
                        book.getQuantity(),
                        book.getDescription()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> getBooksByCategory(Long categoryId) {
        List<Books> books = bookRepository.findByCategory_CategoryId(categoryId);

        return books.stream()
                .map(book -> new BookDTO(
                        book.getBookId(),
                        book.getTitle(),
                        book.getIsbn(),
                        book.getAuthor().getAuthorId(),
                        book.getAuthor().getAuthorName(),
                        book.getCategory().getCategoryId(),
                        book.getCategory().getName(),
                        book.getUser().getUserId(),
                        book.getPrice(),
                        book.getQuantity(),
                        book.getDescription()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> getBooksByAuthor(Long authorId) {
        List<Books> books = bookRepository.findByAuthor_AuthorId(authorId);

        return books.stream()
                .map(book -> new BookDTO(
                        book.getBookId(),
                        book.getTitle(),
                        book.getIsbn(),
                        book.getAuthor().getAuthorId(),
                        book.getAuthor().getAuthorName(),
                        book.getCategory().getCategoryId(),
                        book.getCategory().getName(),
                        book.getUser().getUserId(),
                        book.getPrice(),
                        book.getQuantity(),
                        book.getDescription()
                ))
                .collect(Collectors.toList());
    }

//    @Override
//    public Books updateBookByUser(Long bookId, Books updatedBook, Long userId) {
//        Books book = bookRepository.findById(bookId)
//                .orElseThrow(() -> new RuntimeException("Book not found"));
////        if (!book.getUser().getUserId().equals(userId)) {
////            throw new RuntimeException("Unauthorized access to update this book");
////        }
//
//        book.setTitle(updatedBook.getTitle());
//        book.setIsbn(updatedBook.getIsbn());
//        book.setPrice(updatedBook.getPrice());
//        book.setQuantity(updatedBook.getQuantity());
//
//        if (updatedBook.getCategory() != null) {
//            Category category = categoryRepository.findById(updatedBook.getCategory().getCategoryId())
//                    .orElseThrow(() -> new RuntimeException("Category not found"));
//            book.setCategory(category);
//        }
//
//        if (updatedBook.getAuthor() != null) {
//            Author author = authorRepository.findById(updatedBook.getAuthor().getAuthorId())
//                    .orElseThrow(() -> new RuntimeException("Author not found"));
//            book.setAuthor(author);
//        }
//
//        return bookRepository.save(book);
//    }
    
//    @Override
//    public Books updateBookByUser(Long bookId, Books updatedBook) {
//        Books book = bookRepository.findById(bookId)
//                .orElseThrow(() -> new RuntimeException("Book not found"));
//
//        // Update book details
//        book.setTitle(updatedBook.getTitle());
//        book.setIsbn(updatedBook.getIsbn());
//        book.setPrice(updatedBook.getPrice());
//        book.setQuantity(updatedBook.getQuantity());
//
//        if (updatedBook.getCategory() != null) {
//            Category category = categoryRepository.findById(updatedBook.getCategory().getCategoryId())
//                    .orElseThrow(() -> new RuntimeException("Category not found"));
//            book.setCategory(category);
//        }
//
//        if (updatedBook.getAuthor() != null) {
//            Author author = authorRepository.findById(updatedBook.getAuthor().getAuthorId())
//                    .orElseThrow(() -> new RuntimeException("Author not found"));
//            book.setAuthor(author);
//        }
//
//        return bookRepository.save(book);
//    }
    
    @Override
    public Books updateBook(Long bookId, BookDTO bookDTO) {
        Books existingBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        // Update fields from DTO to entity
        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setPrice(bookDTO.getPrice());
        existingBook.setQuantity(bookDTO.getQuantity());
        existingBook.setDescription(bookDTO.getDescription());

        if (bookDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(bookDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existingBook.setCategory(category);
        }

        if (bookDTO.getAuthorId() != null) {
            Author author = authorRepository.findById(bookDTO.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Author not found"));
            existingBook.setAuthor(author);
        }

        return bookRepository.save(existingBook);
    }



    @Override
    public void deleteBookByUser(Long bookId, Long userId) {
        Books book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to delete this book");
        }

        bookRepository.delete(book);
    }
    

    public long getBookCount() {
        return bookRepository.countBooks();
    }
    

    
//    public void bulkUploadBooks(MultipartFile file, Long loggedInUserId) {
//        Users user = userRepository.findById(loggedInUserId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
//            String line;
//            boolean isFirstLine = true;
//            List<Books> bookBatch = new ArrayList<>();
//            int batchSize = 50;
//
//            while ((line = reader.readLine()) != null) {
//                if (isFirstLine) {
//                    isFirstLine = false;
//                    continue;
//                }
//
//                String[] data = line.split(",");
//                if (data.length < 6) continue; // Removed userId from CSV
//
//                String title = data[0].trim();
//                String isbn = data[1].trim();
//                double price = Double.parseDouble(data[2].trim());
//                int quantity = Integer.parseInt(data[3].trim());
//                String description = data.length > 4 ? data[4].trim() : "";
//                Long authorId = Long.parseLong(data[5].trim());
//                Long categoryId = Long.parseLong(data[6].trim());
//
//                // Validate price and quantity
//                if (price < 0) {
//                    throw new RuntimeException("Price cannot be negative for book: " + title);
//                }
//                if (quantity < 0) {
//                    throw new RuntimeException("Quantity cannot be negative for book: " + title);
//                }
//
//                // Check if book already exists by ISBN
//                if (bookRepository.existsByIsbn(isbn)) {
//                    throw new RuntimeException("Duplicate entry found for ISBN: " + isbn);
//                }
//
//                // Check if book already exists by title
//                if (bookRepository.existsByTitle(title)) {
//                    throw new RuntimeException("Duplicate entry found for book with title: " + title);
//                }
//
//                Category category = categoryRepository.findById(categoryId)
//                        .orElseThrow(() -> new RuntimeException("Category not found for ID: " + categoryId));
//                Author author = authorRepository.findById(authorId)
//                        .orElseThrow(() -> new RuntimeException("Author not found for ID: " + authorId));
//
//                Books book = new Books();
//                book.setTitle(title);
//                book.setIsbn(isbn);
//                book.setPrice(price);
//                book.setQuantity(quantity);
//                book.setDescription(description);
//                book.setUser(user); // Assign the logged-in user
//                book.setCategory(category);
//                book.setAuthor(author);
//
//                bookBatch.add(book);
//
//                if (bookBatch.size() >= batchSize) {
//                    bookRepository.saveAll(bookBatch);
//                    bookBatch.clear();
//                }
//            }
//
//            if (!bookBatch.isEmpty()) {
//                bookRepository.saveAll(bookBatch);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Error processing CSV file: " + e.getMessage(), e);
//        }
//    }
    
//    public void bulkUploadBooks(MultipartFile file, Long loggedInUserId) {
//        Users user = userRepository.findById(loggedInUserId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
//            String line;
//            boolean isFirstLine = true;
//            List<Books> bookBatch = new ArrayList<>();
//            int batchSize = 50;
//            List<String> errors = new ArrayList<>();
//
//            while ((line = reader.readLine()) != null) {
//                if (isFirstLine) {
//                    isFirstLine = false;
//                    continue;
//                }
//
//                String[] data = line.split(",");
//                if (data.length < 7) { // Ensure all required fields exist
//                    errors.add("Invalid data format: " + line);
//                    continue;
//                }
//
//                String title = data[0].trim();
//                String isbn = data[1].trim();
//                double price;
//                int quantity;
//                Long authorId, categoryId;
//
//                try {
//                    price = Double.parseDouble(data[2].trim());
//                    quantity = Integer.parseInt(data[3].trim());
//                    authorId = Long.parseLong(data[5].trim());
//                    categoryId = Long.parseLong(data[6].trim());
//                } catch (NumberFormatException e) {
//                    errors.add("Invalid number format in line: " + line);
//                    continue;
//                }
//
//                // Validate price and quantity
//                if (price < 0) {
//                    errors.add("Price cannot be negative for book: " + title);
//                    continue;
//                }
//                if (quantity < 0) {
//                    errors.add("Quantity cannot be negative for book: " + title);
//                    continue;
//                }
//
//                // Check if book already exists by ISBN or title
//                if (bookRepository.existsByIsbn(isbn)) {
//                    errors.add("Duplicate entry found for ISBN: " + isbn);
//                    continue;
//                }
//                if (bookRepository.existsByTitle(title)) {
//                    errors.add("Duplicate entry found for book with title: " + title);
//                    continue;
//                }
//
//                // Validate category and author existence
//                Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
//                Optional<Author> authorOpt = authorRepository.findById(authorId);
//
//                if (categoryOpt.isEmpty()) {
//                    errors.add("Category not found for ID: " + categoryId);
//                    continue;
//                }
//                if (authorOpt.isEmpty()) {
//                    errors.add("Author not found for ID: " + authorId);
//                    continue;
//                }
//
//                // Create and save book
//                Books book = new Books();
//                book.setTitle(title);
//                book.setIsbn(isbn);
//                book.setPrice(price);
//                book.setQuantity(quantity);
//                book.setDescription(data.length > 4 ? data[4].trim() : "");
//                book.setUser(user);
//                book.setCategory(categoryOpt.get());
//                book.setAuthor(authorOpt.get());
//
//                bookBatch.add(book);
//
//                if (bookBatch.size() >= batchSize) {
//                    bookRepository.saveAll(bookBatch);
//                    bookBatch.clear();
//                }
//            }
//
//            if (!bookBatch.isEmpty()) {
//                bookRepository.saveAll(bookBatch);
//            }
//
//            if (!errors.isEmpty()) {
//                throw new RuntimeException("Some errors occurred during upload: \n" + String.join("\n", errors));
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Error processing CSV file: " + e.getMessage(), e);
//        }
//    }
    
    public void bulkUploadBooks(MultipartFile file, Long loggedInUserId) {
        Users user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            List<Books> bookBatch = new ArrayList<>();
            int batchSize = 50;
            List<String> errors = new ArrayList<>();
            
            // Set to track ISBNs already seen within the current upload to detect duplicates
            Set<String> seenIsbns = new HashSet<>();

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length < 7) { // Ensure all required fields exist
                    errors.add("Invalid data format: " + line);
                    continue;
                }

                String title = data[0].trim();
                String isbn = new BigDecimal(data[1].trim()).toPlainString(); // Ensure proper ISBN format
                double price;
                int quantity;
                Long authorId, categoryId;

                try {
                    price = Double.parseDouble(data[2].trim());
                    quantity = Integer.parseInt(data[3].trim());
                    authorId = Long.parseLong(data[5].trim());
                    categoryId = Long.parseLong(data[6].trim());
                } catch (NumberFormatException e) {
                    errors.add("Invalid number format in line: " + line);
                    continue;
                }

                // Validate price and quantity
                if (price < 0) {
                    errors.add("Price cannot be negative for book: " + title);
                    continue;
                }
                if (quantity < 0) {
                    errors.add("Quantity cannot be negative for book: " + title);
                    continue;
                }

                // Check if the ISBN is already seen in this upload
                if (!seenIsbns.add(isbn)) {
                    errors.add("Duplicate entry found for ISBN within file: " + isbn);
                    continue;
                }

                // Check if book already exists by ISBN or title in the database
                if (bookRepository.existsByIsbn(isbn)) {
                    errors.add("Duplicate entry found for ISBN in database: " + isbn);
                    continue;
                }
                if (bookRepository.existsByTitle(title)) {
                    errors.add("Duplicate entry found for book with title: " + title);
                    continue;
                }

                // Validate category and author existence
                Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
                Optional<Author> authorOpt = authorRepository.findById(authorId);

                if (categoryOpt.isEmpty()) {
                    errors.add("Category not found for ID: " + categoryId);
                    continue;
                }
                if (authorOpt.isEmpty()) {
                    errors.add("Author not found for ID: " + authorId);
                    continue;
                }

                // Create and save book
                Books book = new Books();
                book.setTitle(title);
                book.setIsbn(isbn);
                book.setPrice(price);
                book.setQuantity(quantity);
                book.setDescription(data.length > 4 ? data[4].trim() : "");
                book.setUser(user);
                book.setCategory(categoryOpt.get());
                book.setAuthor(authorOpt.get());

                bookBatch.add(book);

                if (bookBatch.size() >= batchSize) {
                    bookRepository.saveAll(bookBatch);
                    bookBatch.clear();
                }
            }

            if (!bookBatch.isEmpty()) {
                bookRepository.saveAll(bookBatch);
            }

            // If there were any errors, throw an exception with the collected error messages
            if (!errors.isEmpty()) {
                throw new RuntimeException("Some errors occurred during upload:   " + String.join("  ", errors));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing CSV file: " + e.getMessage(), e);
        }
    }




   


}
