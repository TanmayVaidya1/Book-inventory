package com.pinnacle.books.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pinnacle.books.users.Users;
import com.pinnacle.books.users.UsersRepository;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UsersRepository userRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }
    
    public List<Category> getCategoriesByUserEmail(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return categoryRepository.findByUser(user);
    }
    
    public List<Category> getCategoriesByUser(Long userId) {
        return categoryRepository.findByUser_UserId(userId);
    }

    public Category createCategory(Category category) {
        // Get a default user for the category (You can change the logic to fetch a user in a different way)
    	Long userId = category.getUser().getUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Set the user to the category
        category.setUser(user);

        // Save the category with the user set
        return categoryRepository.save(category);
    }
    
    public Category updateCategoryForUser(Long categoryId, Category updatedCategory, String email) {
        // Fetch the user by email
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Fetch the existing category
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        // Check if the logged-in user is the owner of the category
        if (!existingCategory.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not authorized to update this category.");
        }

        // Update the category fields
        existingCategory.setName(updatedCategory.getName());

        // Save the updated category
        return categoryRepository.save(existingCategory);
    }

    public Category updateCategory(Long id, Category updatedCategory) {
        Category existingCategory = getCategoryById(id);
        existingCategory.setName(updatedCategory.getName());
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
