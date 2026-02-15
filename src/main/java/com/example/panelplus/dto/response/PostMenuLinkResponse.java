package com.example.panelplus.dto.response;

import java.util.UUID;

public record PostMenuLinkResponse(
        UUID menuId,
        String name,
        Integer weight
) {}
