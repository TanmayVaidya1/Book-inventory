package com.pinnacle.books.book;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface BookService {

    Books createBook(Books book, Long userId);

    List<BookDTO> getBooksByUser(Long userId);

    List<BookDTO> getAllBooks();

    List<BookDTO> getBooksByCategory(Long categoryId);

    List<BookDTO> getBooksByAuthor(Long authorId);

//    Books updateBookByUser(Long bookId, Books updatedBook, Long userId);
//    Books updateBookByUser(Long bookId, Books updatedBook);
    Books updateBook(Long bookId, BookDTO bookDTO);



    void deleteBookByUser(Long bookId, Long userId);
    

    public long getBookCount();
    
    public void bulkUploadBooks(MultipartFile file, Long loggedInUserId);


}