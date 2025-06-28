package com.example.fakedata.model;

import lombok.Data;

import java.util.Map;

@Data
public class ApiResponse<T> {
    private T data;
    private Map<String, Object> metadata;

    public void ok(T data) {
        this.data = data;
    }

    public void ok(T data, Map<String, Object> metadata) {
        this.data = data;
        this.metadata = metadata;
    }
}
