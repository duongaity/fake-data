package com.example.fakedata.service;

import com.example.fakedata.model.User;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final Faker faker = new Faker(new Locale("vi"));

    @Override
    public List<User> getAllUsers(Integer limit) {
        List<User> userList = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            userList.add(generateFakeUser(UUID.randomUUID().toString()));
        }

        return userList;
    }

    @Override
    public User getUserById(String id) {
        return generateFakeUser(id);
    }

    private User generateFakeUser(String id) {
        User user = new User();
        user.setUserId(id);
        user.setFullName(faker.name().fullName());
        user.setEmail(faker.internet().emailAddress());
        user.setAddress(faker.address().fullAddress());
        user.setPhone(faker.phoneNumber().phoneNumber());
        user.setJobTitle(faker.job().title());
        user.setGender(faker.demographic().sex());
        Date dob = faker.date().birthday(18, 60);
        user.setBirthday(dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        user.setAvatarUrl("https://i.pravatar.cc/150?u=" + user.getUserId());
        return user;
    }

}
