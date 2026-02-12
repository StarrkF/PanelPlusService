package com.example.panelplus.dto.request;

public record I18nTranslationRequest(
        String language,
        String key,
        String value
) {}