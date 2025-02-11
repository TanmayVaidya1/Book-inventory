package com.pinnacle.books.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.validation.Valid;

import java.util.List;


@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3000") 
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }
    
    @GetMapping("/my-category")
    public ResponseEntity<List<Category>> getCategoriesForLoggedInUser(Authentication authentication) {
        // Get the authenticated user's email or username
        String email = authentication.getName(); // This should be the email or username

        // Fetch categories for the logged-in user using the email (or username)
        List<Category> categories = categoryService.getCategoriesByUserEmail(email);
        return ResponseEntity.ok(categories);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category updatedCategory) {
        return ResponseEntity.ok(categoryService.updateCategory(id, updatedCategory));
    }
    
    @PutMapping("/my-category/{id}")
    public ResponseEntity<Category> updateCategoryForLoggedInUser(@PathVariable Long id, 
                                                                 @Valid @RequestBody Category updatedCategory, 
                                                                 Authentication authentication) {
        // Get the authenticated user's email
        String email = authentication.getName(); // Assuming email is used for authentication

        // Update the category for the logged-in user
        Category updatedCategoryResponse = categoryService.updateCategoryForUser(id, updatedCategory, email);
        return ResponseEntity.ok(updatedCategoryResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
