package com.example.fakedata.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class User implements Serializable {
   private String userId;
   private String fullName;
   private String email;
   private String address;
   private String phone;
   private String jobTitle;
   private String gender;
   private LocalDate birthday;
   private String avatarUrl;
}
