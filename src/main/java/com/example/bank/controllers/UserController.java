package com.example.bank.controllers;

import com.example.bank.dto.UserDto;
import com.example.bank.entities.Users;
import com.example.bank.interceptor.RequestInterceptor;
import com.example.bank.repositories.UserRepository;
import com.example.bank.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/manager")
public class UserController{

    private final Logger LOG = LoggerFactory.getLogger(RequestInterceptor.class);
    private UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/lockUser")
    public String lockUser(String userName){
        userService.updateStatusOfUser("L", userName);
        return userName + " successfully locked";
    }

    @PostMapping("/getUsersList")
    public ResponseEntity<List<UserDto>> getAllUsersList(){
        LOG.info("UserController.getUsersList");
        List<UserDto> usersList = new ArrayList<>();
        for (Users u : userService.getUsersList()) {
            usersList.add(new UserDto(u.getId(),u.getUsername(),u.getEmail(),u.getPassword(),u.getStatus()));
        }
        // (UserDto) userService.getUsersList().get(1);
        LOG.info("UserController.getUsersList usersList.size={}",usersList.size());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(usersList);
    }
//    public ResponseEntity<List<Users>> getUsersList(){
//        LOG.info("UserController.getUsersList: started");
//        List<Users> users = userService.getUsersList();
//        return ResponseEntity
//               .status(HttpStatus.ACCEPTED)
//               .body(users);
//    }

    @GetMapping("/deleteUser")
    public String deleteUser(String userName){
        int result = userService.deleteUser(userName);
        return userName + ((result==1) ? " successfully deleted" : " not deleted");
    }
    @GetMapping("/unlockUser")
    public String unlockUser(String userName){
        int result = userService.updateStatusOfUser("A", userName);
        return userName + ((result == 1) ? " successfully unlocked" : " not unlocked");
    }

//    @RequestMapping(path="/createUser", method = RequestMethod.POST)
//    public ResponseEntity<UserDto> createUser(@RequestBody UserDto user) throws JsonProcessingException {
//                LOG.info(   "incoming json: " + new ObjectMapper().writeValueAsString(user
//                          + ";user.getId()=" + user.getId()
//                          + ";user.getUsername()=" + user.getUsername()
//                          + ";user.getPassword()=" + user.getPassword()
//                          + ";user.getEmail()=" + user.getEmail()
//                          + ";user.getStatus()=" + user.getStatus()));
////        Users newUser = new Users();
////        newUser.setUsername(user.getUsername());
////        newUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
////        newUser.setEmail(user.getEmail());
////        newUser.setStatus("A");
////        userService.save(newUser);
//        userService.createNewUser(user);
//        return ResponseEntity
//                .status(HttpStatus.ACCEPTED)
//                .body(user);
//    }
    @PostMapping("/createUser")
    public UserDto createUser(@RequestBody UserDto user) throws SQLException {
        return userService.createNewUser(user);
    }
}
