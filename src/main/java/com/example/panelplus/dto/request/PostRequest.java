package com.example.panelplus.dto.request;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record PostRequest(
        UUID userId,
        Integer status,
        List<PostTranslationRequest> translations
) {}
