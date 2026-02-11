package com.example.panelplus.service;

import com.example.panelplus.dto.request.PostRequest;
import com.example.panelplus.dto.request.PostTranslationRequest;
import com.example.panelplus.dto.response.PostResponse;
import com.example.panelplus.entity.Language;
import com.example.panelplus.entity.Post;
import com.example.panelplus.entity.PostTranslation;
import com.example.panelplus.mapper.PostMapper;
import com.example.panelplus.repository.LanguageRepository;
import com.example.panelplus.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.now;

@Service
@Transactional
public class PostService extends BaseService<Post, UUID, PostRepository> {
    
    private final PostMapper postMapper;
    private final LanguageRepository languageRepository;

    public PostService(PostRepository repo, PostMapper postMapper, LanguageRepository languageRepository) {
        super(repo);
        this.postMapper = postMapper;
        this.languageRepository = languageRepository;
    }

    public Post create(PostRequest request) {

        Post post = postMapper.toEntity(request);

        // parent save
        post = getRepository().save(post);

        // translations
        if (request.translations() != null) {
            for (PostTranslationRequest trDto : request.translations()) {

                PostTranslation tr = postMapper.toTranslationEntity(trDto);

                // FK set
                tr.setPost(post);

                Language language = languageRepository
                        .findById(trDto.language())
                        .orElseThrow(() -> new RuntimeException("Language not found"));

                tr.setLanguage(language);

                post.getTranslations().add(tr);
            }
        }

        return getRepository().save(post);
    }

    public Post update(UUID id, PostRequest request) {

        Post post = getRepository().findById(id).orElseThrow();

        postMapper.updateEntity(post, request);

        if (request.translations() != null) {

            for (PostTranslationRequest trDto : request.translations()) {

                PostTranslation tr = post.getTranslations().stream()
                        .filter(t -> t.getLanguage().getCode().equals(trDto.language()))
                        .findFirst()
                        .orElseGet(() -> {
                            PostTranslation newTr = new PostTranslation();
                            newTr.setPost(post);
                            post.getTranslations().add(newTr);
                            return newTr;
                        });

                postMapper.updateTranslation(tr, trDto);

                Language lang = languageRepository
                        .findById(trDto.language())
                        .orElseThrow();

                tr.setLanguage(lang);
            }
        }

        return getRepository().save(post);
    }

    @Transactional(readOnly = true)
    public PostResponse get(UUID id) {
        return getRepository().findById(id)
                .map(postMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> list(Pageable pageable) {
        return getRepository().findAll(pageable)
                .map(postMapper::toResponse);
    }

    public void softDelete(UUID id) {
        Post post = getRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setDeletedAt(now());
        getRepository().save(post);
    }

    private void mergeTranslations(Post entity, List<PostTranslationRequest> dtos) {
        if (dtos == null) return;

        var current = entity.getTranslations();

        // update + add
        dtos.forEach(dto -> {
            PostTranslation found = current.stream()
                    .filter(t -> t.getLanguage().equals(dto.language()))
                    .findFirst()
                    .orElse(null);

            if (found != null) {
                postMapper.updateTranslation(found, dto);
            } else {
                PostTranslation newTr = postMapper.toTranslationEntity(dto);
                newTr.setPost(entity);
                current.add(newTr);
            }
        });

        current.removeIf(tr ->
                dtos.stream().noneMatch(d -> d.language().equals(tr.getLanguage()))
        );
    }
}
