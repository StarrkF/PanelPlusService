package com.example.panelplus.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MenuResponse(
        UUID id,
        UUID parentId,
        Integer weight,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<MenuTranslationResponse> translations
) {}
