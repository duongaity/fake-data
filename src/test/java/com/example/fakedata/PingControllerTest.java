package com.example.fakedata;

import com.example.fakedata.controller.PingController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PingController.class)
public class PingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/ping should return 'pong'")
    void testPingGet() throws Exception {
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

    @Test
    @DisplayName("POST /api/ping should return 'pong'")
    void testPingPost() throws Exception {
        mockMvc.perform(post("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

}
