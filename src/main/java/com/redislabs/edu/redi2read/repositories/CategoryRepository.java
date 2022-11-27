package com.redislabs.edu.redi2read.repositories;

import org.springframework.data.repository.CrudRepository;

import com.redislabs.edu.redi2read.models.Category;

public interface CategoryRepository extends CrudRepository<Category, String> {
    
}
