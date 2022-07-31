package com.scbx.bookstore;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.scbx.bookstore.domain.Role;
import com.scbx.bookstore.domain.Userr;
import com.scbx.bookstore.service.UserService;

@SpringBootApplication
public class BookstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookstoreApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			// userService.saveRole(new Role(null, "ROLL_USER"));
			// userService.saveUser(new Userr(null, "john.doe", 
			// "thisismysecret", "01-05-2000", new ArrayList<>(), "", ""));
			// userService.addRoleToUser("john.doe", "ROLL_USER");
		};
	}
}
