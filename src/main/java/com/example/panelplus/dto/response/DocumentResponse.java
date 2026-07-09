package com.example.panelplus.dto.response;

import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String originalFileName,
        String storedFileName,
        String path,
        String url,
        String contentType,
        String extension,
        Long size,
        String altText
) {}
