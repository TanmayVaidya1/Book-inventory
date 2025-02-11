package com.pinnacle.books.author;

import com.pinnacle.books.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Change 'userId' to 'user.userId' to reference the correct property in the Users entity
    List<Author> findByUser_UserId(Long userId);
    List<Author> findByUserUserId(Long userId);
    // Check if an author with the same name exists
    boolean existsByAuthorName(String authorName);
}
