package com.pinnacle.books.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
//    Optional<Users> findByEmail(String email);
    Optional<Users> findByEmail(String email);

 // Finds a user by reset token (useful for password reset functionality).
    Optional<Users> findByResetToken(String resetToken);
    
 // Checks if a user with the given email already exists.
    boolean existsByEmail(String email);
 // Repository method
    boolean existsByName(String name);

}
