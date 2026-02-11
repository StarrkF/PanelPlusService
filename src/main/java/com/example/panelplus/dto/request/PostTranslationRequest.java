package com.example.panelplus.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PostTranslationRequest(
        UUID id,
        String language,   // tr, en
        String title,
        String subtitle,
        String body,
        String slug
) {}
