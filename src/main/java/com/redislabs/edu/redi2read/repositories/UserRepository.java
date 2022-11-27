package com.redislabs.edu.redi2read.repositories;

import org.springframework.data.repository.CrudRepository;

import com.redislabs.edu.redi2read.models.User;

public interface UserRepository extends CrudRepository<User, String> {
    
    public User findFirstByEmail(String email);
}
