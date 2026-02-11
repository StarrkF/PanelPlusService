package com.example.panelplus.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record PostRequest(
        Integer userId,
        Integer status,
        List<PostTranslationRequest> translations
) {}
