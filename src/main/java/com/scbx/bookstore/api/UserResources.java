package com.scbx.bookstore.api;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.scbx.bookstore.domain.Role;
import com.scbx.bookstore.domain.Userr;
import com.scbx.bookstore.repo.UserProjection;
import com.scbx.bookstore.repo.UserRepo;
import com.scbx.bookstore.service.UserService;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class UserResources {
    private final UserService userService;
    private final UserRepo userRepo;
    // private final Books books;
    
    private static final String GET_ALL_BOOKS_API = "https://scb-test-book-publisher.herokuapp.com/books";
    private static final String GET_ALL_RECOMMENDED_API = "https://scb-test-book-publisher.herokuapp.com/books/recommendation";
    

    static RestTemplate restTemplate = new RestTemplate();


    // @GetMapping("/users")
    // public ResponseEntity<List<Userr>>getUsers() {
    //     return ResponseEntity.ok().body(userService.getUsers());
    // }

    @GetMapping("/users")
    public Collection<UserProjection>getUserD() {
        return userRepo.getNSD();
    }

    @DeleteMapping("/users")
    public void deleteUser() {
        userRepo.deleteAll();
    }

    @PostMapping("/users")
    public ResponseEntity<Userr>saveUser(@RequestBody Userr user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
        // return new ResponseEntity<Userr>(userService.saveUser(user), HttpStatus.CREATED);
    }

    // @GetMapping("/books")
    // public String getBooks() {
    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

    //     HttpEntity<String> entity = new HttpEntity<>("parameter", headers);

    //     ResponseEntity<String> allBooks = restTemplate.exchange(GET_ALL_BOOKS_API, HttpMethod.GET, entity, String.class);
    //     ResponseEntity<String> recommendedBooks = restTemplate.exchange(GET_ALL_RECOMMENDED_API, HttpMethod.GET, entity, String.class);

    //     JSONObject jsonObject = new JSONObject();
    //     try {
    //         Field changeMap = jsonObject.getClass().getDeclaredField("map");
    //         changeMap.setAccessible(true);
    //         changeMap.set(jsonObject, new LinkedHashMap<>());
    //         changeMap.setAccessible(false);
    //     }
    //     catch(IllegalAccessException | NoSuchFieldException e) {
    //         log.error(e.getMessage());
    //     }
    //     for(int i = 0 ; i < allBooks.getBody().length() ; i++) {

    //     }
    //     JSONObject aB = new JSONObject(allBooks.getBody());
    //     JSONObject rB = new JSONObject(recommendedBooks.getBody());      

    //     Collections.sort(books, new Comparator<Books>() {

    //         @Override
    //         public int compare(Books arg0, Books arg1) {
    //             // TODO Auto-generated method stub
    //             return 0;
    //         }
            
    //     });

        // return allBooks.getBody();
        // Set hashSet = new HashSet<>();
        // ObjectMapper mapper = new ObjectMapper();
        // try {
        //     List booksList = Arrays.asList(mapper.readValue(allBooks.toString(), Books[].class));
        //     List recommendedList = Arrays.asList(mapper.readValue(recommendedBooks.toString(), Books[].class));
        //     for (int i = 0; i < booksList.size(); i++) {
        //         hashSet.add(arg0)
        //     }
        // } catch (JsonMappingException e) {
        //     e.printStackTrace();
        // } catch (JsonProcessingException e) {
        //     e.printStackTrace();
        // }

    // }

    @PostMapping("/role/save")
    public ResponseEntity<Role>saveRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }
    
    @PostMapping("/role/addtouser")
    public ResponseEntity<?>addRoleToUser(@RequestBody RoleToUserForm form) {
        userService.addRoleToUser(form.getUsername(), form.getRolename());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) 
            throws StreamWriteException, DatabindException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                Userr user = userService.getUser(username);
                String access_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURI().toString())
                        .withClaim("role", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, String> token = new HashMap<>();
                token.put("access_token", access_token);
                token.put("refresh_token", refresh_token);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), token);;

            }
            catch(Exception exception) {
                response.setHeader("error", exception.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                // response.sendError(HttpStatus.FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
         
            }
        }
        else {
            throw new RuntimeException("Refresh token is missing.");
        }
    }

    @Data
    class RoleToUserForm {
        private String username;
        private String rolename;

    }  


}
