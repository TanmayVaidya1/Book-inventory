package com.pinnacle.books.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BookResponseDTO {
	@NotNull private Long bookId;
    @NotBlank private String title;
    @NotBlank private String isbn;
    @Positive private double price;
    @Min(0) private int quantity;
    private Long userId;

    // Constructor
    public BookResponseDTO(Books book) {
        this.bookId = book.getBookId();
        this.title = book.getTitle();
        this.isbn = book.getIsbn();
        this.price = book.getPrice();
        this.quantity = book.getQuantity();
        this.userId = book.getUser() != null ? book.getUser().getUserId() : null;
    }

    // Getters and Setters
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}

