package com.scbx.bookstore.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scbx.bookstore.domain.Role;

public interface RoleRepo extends JpaRepository<Role, Long>{
    Role findByName(String name); 
    
}
