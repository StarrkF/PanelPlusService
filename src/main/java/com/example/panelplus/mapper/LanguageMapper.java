package com.example.panelplus.mapper;

import com.example.panelplus.dto.LanguageDto;
import com.example.panelplus.entity.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    LanguageDto toDto(Language entity);

    @Mapping(target = "code", source = "code")
    Language toEntity(LanguageDto dto);

    @Mapping(target = "code", ignore = true)
    void updateEntity(@MappingTarget Language entity, LanguageDto dto);
}
