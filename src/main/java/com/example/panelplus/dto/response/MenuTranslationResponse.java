package com.example.panelplus.dto.response;

public record MenuTranslationResponse(
        String language,
        String name,
        String slug
) {}