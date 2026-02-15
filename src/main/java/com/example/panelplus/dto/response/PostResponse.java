package com.example.panelplus.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PostResponse(
        UUID id,
        UUID userId,
        Integer status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<PostTranslationResponse> translations,
        List<PostMenuLinkResponse> menus
) {}
