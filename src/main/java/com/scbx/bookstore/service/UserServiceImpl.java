package com.scbx.bookstore.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scbx.bookstore.domain.Role;
import com.scbx.bookstore.domain.Userr;
import com.scbx.bookstore.repo.RoleRepo;
import com.scbx.bookstore.repo.UserProjection;
import com.scbx.bookstore.repo.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService{
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Userr user = userRepo.findByUsername(username);
        if(user == null) {
            log.error("User not found.");
            throw new UsernameNotFoundException("User not found.");
        }
        else {
            log.info("User found: {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public Userr saveUser(Userr user) {
        log.info("Saving new user: {} to the DB.", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setName(user.getUsername().split("[.]")[0]);
        user.setSurname(user.getUsername().split("[.]")[1]);
        return userRepo.save(user);
    }

    @Override
    public Userr getUser(String username) {
        log.info("Fetching user: {}.", username);
        return userRepo.findByUsername(username);
    }

    @Override
    public Collection<UserProjection> getUserD(String username) {
        log.info("Fetching user: {}.", username);
        return userRepo.getNSD();
    }
    
    @Override
    public List<Userr> getUsers() {
        log.info("Fetching all user.");
        return userRepo.findAll();
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role: {} to user: {}.", roleName, username);
        Userr user = userRepo.findByUsername(username);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving new role: {} to the DB.", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public void deleteUser(Userr user) {
        userRepo.deleteAll();
    }

}
