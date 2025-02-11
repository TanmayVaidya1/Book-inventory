package com.pinnacle.books.inventory;

import com.pinnacle.books.book.Books;
import com.pinnacle.books.supplier.Supplier;
import com.pinnacle.books.users.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stock_id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Books book;  // Change this to InventoryBook instead of java.awt.print.Book

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user; // Foreign key to Users table

    @Column(nullable = false)
    private int quantity;
    
    @Column(nullable = false)
    private double purchasePrice;

    public Inventory() {}

    public Inventory(Long stock_id, Books book, Supplier supplier, Users user, int quantity, double purchasePrice) {
        this.stock_id = stock_id;
        this.book = book;
        this.supplier = supplier;
        this.user = user;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

	public Long getStock_id() {
		return stock_id;
	}

	public void setStock_id(Long stock_id) {
		this.stock_id = stock_id;
	}

	public Books getBook() {
		return book;
	}

	public void setBook(Books book) {
		this.book = book;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

    // Getters and Setters
}
