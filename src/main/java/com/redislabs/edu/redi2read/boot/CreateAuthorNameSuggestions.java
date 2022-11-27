package com.redislabs.edu.redi2read.boot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.redislabs.edu.redi2read.models.Book;
import com.redislabs.edu.redi2read.repositories.BookRepository;
import com.redislabs.lettusearch.RediSearchCommands;
import com.redislabs.lettusearch.StatefulRediSearchConnection;
import com.redislabs.lettusearch.Suggestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(7)
@RequiredArgsConstructor
public class CreateAuthorNameSuggestions implements CommandLineRunner {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final StatefulRediSearchConnection<String, String> searchConnection;
    private final BookRepository bookRepository;

    @Value("${app.authors-autocompletekey}")
    private String autoCompleteKey;

    @Override    
    public void run(String... args) throws Exception {
        
        if(!redisTemplate.hasKey(autoCompleteKey)) {

            RediSearchCommands<String, String> commands = searchConnection.sync();
            

            Iterable<Book> books = bookRepository.findAll();
            for (Book book : books) {
                for (String author : book.getAuthors()) {
                    Suggestion<String> suggestion = Suggestion.builder(author).score(1d).build();
                    commands.sugadd(autoCompleteKey, suggestion);
                } 
            }

            log.info(">>> Created Author name suggestions...");
        }
    }
    
}
