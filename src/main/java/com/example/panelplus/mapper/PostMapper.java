package com.example.panelplus.mapper;

import com.example.panelplus.dto.request.PostRequest;
import com.example.panelplus.dto.request.PostTranslationRequest;
import com.example.panelplus.dto.response.PostResponse;
import com.example.panelplus.dto.response.PostTranslationResponse;
import com.example.panelplus.entity.Post;
import com.example.panelplus.entity.PostTranslation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "translations", ignore = true)
    Post toEntity(PostRequest dto);

    @Mapping(source = "user.id", target = "userId")
    PostResponse toResponse(Post entity);

    @Mapping(source = "language.code", target = "language")
    PostTranslationResponse toTranslationResponse(PostTranslation entity);

    void updateEntity(@MappingTarget Post entity, PostRequest dto);

    @Mapping(target = "language", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "id", ignore = true)
    PostTranslation toTranslationEntity(PostTranslationRequest dto);

    @Mapping(target = "language", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateTranslation(@MappingTarget PostTranslation entity, PostTranslationRequest dto);
}
