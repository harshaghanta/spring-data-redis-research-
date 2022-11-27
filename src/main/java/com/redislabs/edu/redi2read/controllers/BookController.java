package com.redislabs.edu.redi2read.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.edu.redi2read.models.Category;
import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.edu.redi2read.repositories.CategoryRepository;
import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.SearchResults;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import com.redislabs.lettusearch.Suggestion;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final StatefulRediSearchConnection<String, String> searchConnection;

    @Value("${app.booksSearchIndexName}")
    private String bookSearchIndexName;

    @Value("${app.authors-autocompletekey}")
    private String autosuggestAuthorKey;

    @GetMapping
    public Map<String, Object> getAll(@RequestParam(defaultValue = "0") Integer page, 
                    @RequestParam(defaultValue = "10") Integer size)  
    {
        PageRequest pageRequest = PageRequest.of(page,size);
        Page<Book> pagedBooks = bookRepository.findAll(pageRequest);
        
        List<Book> books = pagedBooks.hasContent() ? pagedBooks.getContent() : Collections.emptyList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("books", books);
        response.put("page", pagedBooks.getNumber());
        response.put("pages", pagedBooks.getTotalPages());
        response.put("total", pagedBooks.getTotalElements());
        return response;
    }

    @GetMapping("/categories")
    public Iterable<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    @GetMapping("/{isbn}")
    public Book getBookByIsbn(@PathVariable String isbn) {
        return bookRepository.findById(isbn).get();
    }

    @GetMapping("/search")
    public SearchResults<String, String> search(@RequestParam(value = "q") String query) {

        RediSearchCommands<String, String> commands = searchConnection.sync();
        SearchResults<String, String> results = commands.search(bookSearchIndexName, query);
        return results;
    }

    @GetMapping("/authors")
    public List<Suggestion<String>> authorAutoComplete(@RequestParam(value = "q") String query) {
        RediSearchCommands<String, String> commands = searchConnection.sync();
        return commands.sugget(autosuggestAuthorKey, query);
    }
    
}
