package com.redislabs.edu.redi2read.boot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.edu.redi2read.models.Category;
import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.edu.redi2read.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(3)
@Slf4j
@RequiredArgsConstructor
public class CreateBooks implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    
    @Override
    public void run(String... args) throws Exception {
      
        if(bookRepository.count() == 0) {
            List<File> files = Files.list(Paths.get(getClass().getResource("/data/books").toURI()))
                .filter(Files::isRegularFile)
                .filter(path-> path.toString().endsWith(".json"))
                .map(java.nio.file.Path::toFile)
                .collect(Collectors.toList());

            Map<String, Category> categories = new HashMap<>();
            
            files.stream().forEach(file -> {
               
                try {
                    Category category = null;
                    String categoryName = file.getName().substring(0, file.getName().lastIndexOf("_"));
                    if(!categories.containsKey(categoryName)) {
                        category = Category.builder().name(categoryName).build();
                        categoryRepository.save(category);
                        categories.put(categoryName, category);
                    }
                    else {
                        category = categories.get(categoryName);
                    }

                    
                        TypeReference<List<Book>> typeReference = new TypeReference<List<Book>>() {
                            
                        };

                        FileInputStream fileStream = new FileInputStream(file);
                        ObjectMapper objectMapper = new ObjectMapper();
                        List<Book> books = objectMapper.readValue(fileStream, typeReference);
                        Category cat = category;
                        books.stream().forEach(book -> {
                            book.addCategory(cat);                            
                        });
                        bookRepository.saveAll(books);
                        log.info("All books for category: {} saved successfully", categoryName);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
               
            });
        }
        
    }
    
}
