package com.example.panelplus.dto.response;

import java.util.UUID;

public record I18nTranslationResponse(
        UUID id,
        String language,
        String key,
        String value
) {}
