package com.redislabs.edu.redi2read.repositories;


import org.springframework.data.repository.CrudRepository;

import com.redislabs.edu.redi2read.models.Role;

public interface RoleRepository extends CrudRepository<Role, String> {
    
    Role findFirstByName(String name);
}
