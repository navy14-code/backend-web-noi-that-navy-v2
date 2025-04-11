package com.example.doan.controller;


//import com.example.doan.doman.AccountStatus;
import com.example.doan.doman.USER_ROLE;
import com.example.doan.modal.User;
import com.example.doan.repository.UserRepository;
import com.example.doan.request.UserRequest;
import com.example.doan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.getUserProfile(jwt);

        return  ResponseEntity.ok(user);
    }

//    @PostMapping("/create")
//    public ResponseEntity<User> creatUser( @RequestBody User user) throws Exception {
//
//            User createdUser = userService.createUser(user);
//
//            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
//    }

    @GetMapping("{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) throws Exception {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<User>> getCustomers() {
        List<User> customers = userRepository.findByRole(USER_ROLE.ROLE_CUSTOMER);
        return ResponseEntity.ok(customers);
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
//    @GetMapping()
//    public ResponseEntity<List<User>> getAllUsers(
//            @RequestParam(required = false)AccountStatus status){
//        List<User> users = userService.getAllUsers(status);
//        return ResponseEntity.ok(users);
//    }

    @PatchMapping("/update")
    public ResponseEntity<User> updateUser(@RequestHeader("Authorization") String jwt, @RequestBody UserRequest req) throws Exception {

        User profile = userService.getUserProfile(jwt);
        User updatedUser = userService.updateUser(profile.getId(), req);

        return ResponseEntity.ok(updatedUser);

    }

    @DeleteMapping("{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) throws Exception {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}


