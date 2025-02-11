package com.pinnacle.books.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pinnacle.books.users.Users;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findByUser_UserId(Long userId);
	 List<Category> findByUser(Users user); 
}
