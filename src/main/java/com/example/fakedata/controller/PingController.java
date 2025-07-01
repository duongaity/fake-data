package com.example.fakedata.controller;

import org.springframework.web.bind.annotation.*;

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

    @PutMapping
    public String pingPut() {
        return "ok";
    }

    @DeleteMapping
    public String pingDelete() {
        return "ok";
    }

}
