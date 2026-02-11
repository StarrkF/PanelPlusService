package com.example.panelplus.dto.request;

import java.util.List;
import java.util.UUID;

public record MenuRequest(
        UUID parentId,
        Integer weight,
        List<MenuTranslationRequest> translations
) {}
