package com.redislabs.edu.redi2read.repositories;

import org.springframework.data.repository.CrudRepository;

import com.redislabs.edu.redi2read.models.BookRating;

public interface BookRatingRepository extends CrudRepository<BookRating, String> {
    
}
