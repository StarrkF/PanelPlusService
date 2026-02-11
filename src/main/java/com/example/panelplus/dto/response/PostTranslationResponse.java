package com.example.panelplus.dto.response;

public record PostTranslationResponse(
        String language, // "en", "tr" gibi kod döner
        String title,
        String slug,
        String subtitle,
        String body
) {}
