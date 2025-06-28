package com.example.fakedata.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/ping")
public class PingController {

    @GetMapping
    public String pingGet() {
        return "pong";
    }

    @PostMapping
    public String pingPost() {
        return "pong";
    }

}
