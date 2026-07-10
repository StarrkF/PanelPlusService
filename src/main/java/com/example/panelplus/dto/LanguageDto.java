package com.example.panelplus.dto;

public record LanguageDto(
        String code,
        String name,
        Boolean isActive,
        Boolean defaultLanguage
) {}
