package com.example.fakedata.service;

import com.example.fakedata.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers(Integer limit);
    User getUserById(String id);
}
