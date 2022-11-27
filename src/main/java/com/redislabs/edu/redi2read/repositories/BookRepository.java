package com.redislabs.edu.redi2read.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.redislabs.edu.redi2read.models.Book;

public interface BookRepository extends PagingAndSortingRepository<Book, String> {

    
    
}
