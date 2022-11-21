package com.finlock.ldap.controller;

import com.finlock.ldap.model.User;
import io.swagger.annotations.ApiOperation;
import com.finlock.ldap.repository.UserRepository;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController()
@ApiOperation(nickname = "User Endpoint", value = "manage users")
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    UserRepository userRepo;


    @ApiOperation(value = "Add a new User entry")
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "User successfully inserted."),
            @io.swagger.annotations.ApiResponse(code = 409, message = "User already exists."),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Unknown error.")
    })
    @PostMapping()
    public void addUser(@Valid @RequestBody User user) {
        System.out.println("user: " + user.getSn() + " " + user.getCn() + " " + user.getUid());
        userRepo.addUser(user);
    }

    @ApiOperation(value = "Get an User entry by its uid")
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "User successfully found."),
            @io.swagger.annotations.ApiResponse(code = 404, message = "User not found."),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Unknown error.")
    })
    @GetMapping(path = "/{uid}")
    public User getUser(@PathVariable("uid") String uid) {
        return userRepo.getUser(uid);
    }

    @ApiOperation(value= "Get All User entries")
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "Successfully returned all entries."),
            @io.swagger.annotations.ApiResponse(code = 500, message = "Unknown error.")
    })
    @GetMapping()
    public List<User> getUser() {
        return userRepo.getUsers();
    }

    @ApiOperation(value = "Delete using given its uid")
    @ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 200, message = "User successfully deleted."),
            @io.swagger.annotations.ApiResponse(code = 404, message = "User not found."),
            @ApiResponse(code = 500, message = "Unknown error.")
    })
    @DeleteMapping(path = "/{uid}")
    public void deleteUser(@PathVariable("uid") String uid) {
        userRepo.delete(uid);
    }

    @ApiOperation("Updating user.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated user."),
            @ApiResponse(code = 400, message = "Bad request.")
    })
    @PutMapping(path = "/{uid}")
    public void updateUser(@PathVariable("uid") String uid, @RequestBody User user){
        userRepo.updateUsers(uid,user);
    }
}
