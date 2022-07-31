package com.scbx.bookstore.repo;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.scbx.bookstore.domain.Userr;

public interface UserRepo extends JpaRepository<Userr, Long>{
    Userr findByUsername(String username);

    @Query(value = "select u.name , u.surname , u.date_of_birth FROM bookstore.userr u", nativeQuery = true)
    Collection<UserProjection> getNSD();
}
