package com.example.fakedata;

import com.example.fakedata.model.User;
import com.example.fakedata.service.UserService;
import com.example.fakedata.service.UserServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceTest {

    private final UserService userService = new UserServiceImpl();

    @Test
    void testGetAllUsersReturnCorrectNumber() {
        int limit = 5;
        List<User> users = userService.getAllUsers(limit);

        assertThat(users).hasSize(limit);
        assertThat(users).allSatisfy(user -> {
            assertThat(user.getUserId()).isNotBlank();
            assertThat(user.getFullName()).isNotBlank();
            assertThat(user.getEmail()).isNotBlank();
            assertThat(user.getAddress()).isNotBlank();
            assertThat(user.getPhone()).isNotBlank();
            assertThat(user.getJobTitle()).isNotBlank();
            assertThat(user.getGender()).isNotBlank();
            assertThat(user.getBirthday()).isNotNull();
            assertThat(user.getAvatarUrl()).contains("https://i.pravatar.cc/");
        });
    }

    @Test
    void testGetUserByIdReturnUserWithGivenId() {
        String testId = "test-id-123";
        User user = userService.getUserById(testId);

        assertThat(user).isNotNull();
        assertThat(user.getUserId()).isEqualTo(testId);
        assertThat(user.getAvatarUrl()).contains(testId);
    }

}
