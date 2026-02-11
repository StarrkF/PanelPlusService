package com.example.panelplus.dto.request;

public record RegisterRequest(
        String username,
        String email,
        String password
) {
}
