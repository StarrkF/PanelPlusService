package com.example.panelplus.mapper;

import com.example.panelplus.dto.request.PostRequest;
import com.example.panelplus.dto.request.PostTranslationRequest;
import com.example.panelplus.dto.response.PostResponse;
import com.example.panelplus.entity.Post;
import com.example.panelplus.entity.PostTranslation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {

    Post toEntity(PostRequest dto);

    PostResponse toResponse(Post entity);

    void updateEntity(@MappingTarget Post entity, PostRequest dto);

    @Mapping(target = "language", ignore = true)
    PostTranslation toTranslationEntity(PostTranslationRequest dto);

    @Mapping(target = "language", ignore = true)
    void updateTranslation(@MappingTarget PostTranslation entity,
                           PostTranslationRequest dto);
}
