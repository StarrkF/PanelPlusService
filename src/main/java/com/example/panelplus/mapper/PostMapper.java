package com.example.panelplus.mapper;

import com.example.panelplus.dto.request.PostRequest;
import com.example.panelplus.dto.request.PostTranslationRequest;
import com.example.panelplus.dto.response.PostMenuLinkResponse;
import com.example.panelplus.dto.response.PostResponse;
import com.example.panelplus.dto.response.PostTranslationResponse;
import com.example.panelplus.entity.Menu;
import com.example.panelplus.entity.MenuPost;
import com.example.panelplus.entity.MenuTranslation;
import com.example.panelplus.entity.Post;
import com.example.panelplus.entity.PostTranslation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "translations", ignore = true)
    Post toEntity(PostRequest dto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "menuPosts", target = "menus")
    PostResponse toResponse(Post entity);

    @Mapping(source = "language.code", target = "language")
    PostTranslationResponse toTranslationResponse(PostTranslation entity);

    void updateEntity(@MappingTarget Post entity, PostRequest dto);

    @Mapping(target = "language", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    PostTranslation toTranslationEntity(PostTranslationRequest dto);

    @Mapping(target = "language", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateTranslation(@MappingTarget PostTranslation entity, PostTranslationRequest dto);

    // MenuPost -> PostMenuLinkResponse mapping
    @Mapping(source = "menu.id", target = "menuId")
    @Mapping(source = "menu", target = "name", qualifiedByName = "menuName")
    PostMenuLinkResponse toMenuLinkResponse(MenuPost entity);

    List<PostMenuLinkResponse> toMenuLinkResponseList(List<MenuPost> entities);

    @Named("menuName")
    default String mapMenuName(Menu menu) {
        if (menu == null) return null;
        Set<MenuTranslation> translations = menu.getTranslations();
        if (translations == null || translations.isEmpty()) return null;
        // Return the first available name (no language filtering specified)
        return translations.iterator().next().getName();
    }
}
