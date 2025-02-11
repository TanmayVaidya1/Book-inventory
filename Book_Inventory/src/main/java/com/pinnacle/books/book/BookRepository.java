package com.pinnacle.books.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Books, Long> {

    List<Books> findByUser_UserId(Long userId);

    List<Books> findByCategory_CategoryId(Long categoryId);

    List<Books> findByAuthor_AuthorId(Long authorId);
    
    @Query("SELECT COUNT(b) FROM Books b")
    long countBooks();
    
    // Method to find a book by its ISBN
    Books findByIsbn(String isbn);
    
 // Custom method to check if a book exists by ISBN
    boolean existsByIsbn(String isbn);
    
 // Method to check if a book with the same title exists
    boolean existsByTitle(String title);
}
