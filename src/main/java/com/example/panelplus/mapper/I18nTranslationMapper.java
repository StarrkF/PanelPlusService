package com.example.panelplus.mapper;

import com.example.panelplus.dto.request.I18nTranslationRequest;
import com.example.panelplus.dto.response.I18nTranslationResponse;
import com.example.panelplus.entity.I18nTranslation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface I18nTranslationMapper {

    @Mapping(source = "language.code", target = "language")
    I18nTranslationResponse toResponse(I18nTranslation entity);

    @Mapping(target = "language", ignore = true)
    @Mapping(target = "id", ignore = true)
    I18nTranslation toEntity(I18nTranslationRequest dto);

    @Mapping(target = "language", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget I18nTranslation entity, I18nTranslationRequest dto);
}
