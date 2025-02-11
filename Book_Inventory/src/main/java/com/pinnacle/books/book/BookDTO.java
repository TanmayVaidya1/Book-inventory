package com.pinnacle.books.book;

public class BookDTO {

    private Long bookId;
    private String title;
    private String isbn;
    private Long authorId;
    private Long categoryId;
    private Long userId;
    private double price;
    private int quantity;
    private String description;
    private String authorName;
    private String categoryName;

    public BookDTO(Long bookId, String title, String isbn, Long authorId, String authorName, Long categoryId, String categoryName,
                   Long userId, double price, int quantity, String description) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.authorId = authorId;
        this.authorName = authorName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.userId = userId;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
    }

    // Getters and Setters
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
