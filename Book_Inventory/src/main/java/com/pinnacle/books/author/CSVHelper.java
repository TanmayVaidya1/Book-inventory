package com.pinnacle.books.author;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import com.pinnacle.books.users.Users;

public class CSVHelper {

    // MIME type for CSV files
    public static String TYPE = "text/csv";

    // Check if the file is a CSV
    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType()) || file.getOriginalFilename().endsWith(".csv");
    }

    // Convert CSV data to Author list
    public static List<Author> csvToAuthors(InputStream is, Users user, AuthorRepository authorRepository) {
        List<Author> authors = new ArrayList<>();
        
        // Using try-with-resources to handle automatic closing of resources
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            // Iterate through the CSV records
            for (CSVRecord record : csvParser) {
                // Ensure the CSV contains the necessary columns
                if (record.isMapped("authorName") && record.isMapped("biography")) {
                    String authorName = record.get("authorName");

                    // Skip if author already exists
                    if (authorRepository.existsByAuthorName(authorName)) {
                        continue;
                    }

                    Author author = new Author();
                    author.setAuthorName(authorName);
                    author.setBiography(record.get("biography"));
                    author.setUser(user);  // Associate with logged-in user
                    
                    authors.add(author);
                } else {
                    throw new RuntimeException("CSV format is invalid. Missing 'authorName' or 'biography'.");
                }
            }

        } catch (Exception e) {
            // Catch any errors and throw with a custom message
            throw new RuntimeException("Error while parsing CSV: " + e.getMessage(), e);
        }

        return authors;
    }
}
