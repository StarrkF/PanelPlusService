package com.example.panelplus.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostResponse(
        UUID id,
        Integer userId,
        Integer status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
