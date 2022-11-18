package com.finlock.ldap.controller;

import com.finlock.ldap.model.User;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;
import com.finlock.ldap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
//@ApiOperation(nickname = "User Endpoint", value = "manage users")
@RequestMapping(path = "/Users")
public class UserController {

    @Autowired
    UserRepository userRepo;


    @PostMapping()
    public void addUser(@Valid @RequestBody User user) {
        System.out.println("user: " + user.getSn() + " " + user.getCn() + " " + user.getUid());
        userRepo.addUser(user);
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAll() {
        List<User> users = userRepo.getUsers();
        System.out.println("Users: :" + users);
        return ResponseEntity.ok(users);
    }

}
