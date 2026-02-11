package com.example.panelplus.mapper;

import com.example.panelplus.dto.request.MenuRequest;
import com.example.panelplus.dto.request.MenuTranslationRequest;
import com.example.panelplus.dto.request.PostRequest;
import com.example.panelplus.dto.request.PostTranslationRequest;
import com.example.panelplus.dto.response.MenuResponse;
import com.example.panelplus.dto.response.MenuTranslationResponse;
import com.example.panelplus.dto.response.PostResponse;
import com.example.panelplus.dto.response.PostTranslationResponse;
import com.example.panelplus.entity.Menu;
import com.example.panelplus.entity.MenuTranslation;
import com.example.panelplus.entity.Post;
import com.example.panelplus.entity.PostTranslation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    // --- ENTITY -> RESPONSE ---

    @Mapping(source = "parent.id", target = "parentId")
    MenuResponse toResponse(Menu entity);

    @Mapping(source = "language.code", target = "language")
    MenuTranslationResponse toTranslationResponse(MenuTranslation entity);

    // --- REQUEST -> ENTITY ---

    @Mapping(target = "translations", ignore = true)
    @Mapping(target = "parent", ignore = true)
    Menu toEntity(MenuRequest dto);

    @Mapping(target = "language", ignore = true)
    @Mapping(target = "menu", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    MenuTranslation toTranslationEntity(MenuTranslationRequest dto);

    // --- UPDATE ---

    @Mapping(target = "translations", ignore = true)
    @Mapping(target = "parent", ignore = true)
    void updateEntity(@MappingTarget Menu entity, MenuRequest dto);

    @Mapping(target = "language", ignore = true)
    @Mapping(target = "menu", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateTranslation(@MappingTarget MenuTranslation entity, MenuTranslationRequest dto);
}
