package com.pinnacle.books.author;

import com.pinnacle.books.users.DTO.ReqRes;
import java.time.LocalDateTime;

public class AuthorDTO {

    private Long authorId;
    private String authorName;
    private String biography;

    private Long userId;
    private ReqRes user;

    // Getters and Setters
    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

   

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ReqRes getUser() {
        return user;
    }

    public void setUser(ReqRes user) {
        this.user = user;
    }
}
