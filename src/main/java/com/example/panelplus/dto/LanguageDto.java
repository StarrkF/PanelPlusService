package com.example.panelplus.dto;

public record LanguageDto(
        String code,
        String name,
        boolean isActive,
        boolean defaultLanguage
) {}
