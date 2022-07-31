package com.scbx.bookstore.service;

import java.util.Collection;
import java.util.List;

import com.scbx.bookstore.domain.Role;
import com.scbx.bookstore.domain.Userr;
import com.scbx.bookstore.repo.UserProjection;

public interface UserService {
    Userr saveUser(Userr user);
    void deleteUser(Userr user);
    Userr getUser(String username);
    Collection<UserProjection> getUserD(String username); 
    List<Userr> getUsers(); 
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);

}
