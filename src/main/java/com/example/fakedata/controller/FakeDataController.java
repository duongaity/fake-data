package com.example.fakedata.controller;

import com.example.fakedata.model.ApiResponse;
import com.example.fakedata.model.User;
import com.example.fakedata.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class FakeDataController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getFakeUsers(@RequestParam(required = false, defaultValue = "5") Integer limit) {
        List<User> userList = userService.getAllUsers(limit);
        ApiResponse<List<User>> response = new ApiResponse<>();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("totalItem", userList.size());
        metadata.put("currentTime", new Date().getTime());
        response.ok(userList, metadata);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getUserById(@PathVariable String id) {
        ApiResponse<User> response = new ApiResponse<>();
        User user = userService.getUserById(id);
        response.ok(user);
        return ResponseEntity.ok(response);
    }

}
