package com.pinnacle.books.author;

import com.pinnacle.books.users.DTO.ReqRes;
import com.pinnacle.books.users.UserService;
import com.pinnacle.books.users.Users;
import com.pinnacle.books.users.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;
    
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserService userService;

    private AuthorDTO convertToDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setAuthorId(author.getAuthorId());
        dto.setAuthorName(author.getAuthorName());
        dto.setBiography(author.getBiography());

        if (author.getUser() != null) {
            ReqRes reqRes = new ReqRes();
            reqRes.setName(author.getUser().getName());
            reqRes.setEmail(author.getUser().getEmail());
            reqRes.setRole(author.getUser().getRole());
            dto.setUser(reqRes);
        }
        return dto;
    }

    private Author convertToEntity(AuthorDTO dto) {
        Author author = new Author();
        author.setAuthorId(dto.getAuthorId());
        author.setAuthorName(dto.getAuthorName());
        author.setBiography(dto.getBiography());

        if (dto.getUserId() != null) {
            Users user = userService.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));
            author.setUser(user);
        }
        return author;
    }

    public List<AuthorDTO> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AuthorDTO> getAuthorsByUserId(Long userId) {
        List<Author> authors = authorRepository.findByUserUserId(userId);
        return authors.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


//    public List<AuthorDTO> getAuthorsByUserId(Long userId) {
//        // Change this method to use the correct query method name
//        return authorRepository.findByUser_UserId(userId).stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }

    public AuthorDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with ID: " + id));
        return convertToDTO(author);
    }

    public AuthorDTO createAuthor(AuthorDTO dto) {
        Author author = convertToEntity(dto);
        Author savedAuthor = authorRepository.save(author);
        return convertToDTO(savedAuthor);
    }

    public AuthorDTO updateAuthor(Long id, AuthorDTO dto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with ID: " + id));

        author.setAuthorName(dto.getAuthorName());
        author.setBiography(dto.getBiography());

        if (dto.getUserId() != null) {
            Users user = userService.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));
            author.setUser(user);
        }

        Author updatedAuthor = authorRepository.save(author);
        return convertToDTO(updatedAuthor);
    }

    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with ID: " + id));
        authorRepository.delete(author);
    }
    

    public void saveAuthorsFromCSV(MultipartFile file, Long userId) {
        try {
            Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            
            // Pass the repository as well
            List<Author> authors = CSVHelper.csvToAuthors(file.getInputStream(), user, authorRepository);
            authorRepository.saveAll(authors);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store CSV data: " + e.getMessage());
        }
    }


}
