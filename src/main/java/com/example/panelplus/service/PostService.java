package com.example.panelplus.service;

import com.example.panelplus.dto.request.PostRequest;
import com.example.panelplus.dto.request.PostTranslationRequest;
import com.example.panelplus.dto.response.PostMenuLinkResponse;
import com.example.panelplus.dto.response.PostResponse;
import com.example.panelplus.entity.Language;
import com.example.panelplus.entity.Menu;
import com.example.panelplus.entity.MenuPost;
import com.example.panelplus.entity.Post;
import com.example.panelplus.entity.PostTranslation;
import com.example.panelplus.entity.User;
import com.example.panelplus.exception.BaseException;
import com.example.panelplus.mapper.PostMapper;
import com.example.panelplus.repository.LanguageRepository;
import com.example.panelplus.repository.MenuPostRepository;
import com.example.panelplus.repository.MenuRepository;
import com.example.panelplus.repository.PostRepository;
import com.example.panelplus.repository.PostTranslationRepository;
import com.example.panelplus.repository.UserRepository;
import com.example.panelplus.security.AuthUtil;
import com.example.panelplus.util.UtilService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.time.LocalDateTime.now;

@Service
@Transactional
public class PostService extends BaseService<Post, UUID, PostRepository> {
    
    private final PostMapper postMapper;
    private final LanguageRepository languageRepository;
    private final PostTranslationRepository postTranslationRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final MenuPostRepository menuPostRepository;

    public PostService(PostRepository repo, PostMapper postMapper, LanguageRepository languageRepository, PostTranslationRepository postTranslationRepository, UserRepository userRepository, MenuRepository menuRepository, MenuPostRepository menuPostRepository) {
        super(repo);
        this.postMapper = postMapper;
        this.languageRepository = languageRepository;
        this.postTranslationRepository = postTranslationRepository;
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
        this.menuPostRepository = menuPostRepository;
    }

    public PostResponse create(PostRequest request) {
        Post post = postMapper.toEntity(request);

        User user = AuthUtil.getCurrentUserOrThrow();
        post.setUser(user);

        // Translations işlemleri
        if (request.translations() != null && !request.translations().isEmpty()) {
            if (post.getTranslations() == null) {
                post.setTranslations(new HashSet<>());
            }

            for (PostTranslationRequest trDto : request.translations()) {
                Language language = languageRepository.getReferenceById(trDto.language());

                PostTranslation tr = postMapper.toTranslationEntity(trDto);
                String slug = UtilService.toSlug(trDto.title());
                tr.setPost(post);
                tr.setSlug(slug);
                tr.setLanguage(language);

                post.getTranslations().add(tr);
            }
        }

        Post savedPost = getRepository().save(post);
        return postMapper.toResponse(savedPost);
    }

    public PostResponse update(UUID id, PostRequest request) {
        Post post = getRepository().findById(id)
                .orElseThrow(() -> new BaseException("Post not found", HttpStatus.NOT_FOUND));

        // Ana alanları güncelle
        postMapper.updateEntity(post, request);

        // İlişkili tabloları (Translations) merge et
        if (request.translations() != null) {
            mergeTranslations(post, request.translations());
        }

        Post savedPost = getRepository().save(post);
        return postMapper.toResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public PostResponse get(UUID id) {
        return getRepository().findById(id)
                .map(postMapper::toResponse)
                .orElseThrow(() -> new BaseException("Post not found", HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> list(Pageable pageable) {
        return getRepository().findAll(pageable)
                .map(postMapper::toResponse);
    }

    public void softDelete(UUID id) {
        Post post = getRepository().findById(id)
                .orElseThrow(() -> new BaseException("Post not found", HttpStatus.NOT_FOUND));
        post.setDeletedAt(now());
        getRepository().save(post);
    }

    private void mergeTranslations(Post entity, List<PostTranslationRequest> dtos) {
        if (dtos == null) return;

        Set<PostTranslation> currentTranslations = entity.getTranslations();
        if (currentTranslations == null) {
            currentTranslations = new HashSet<>();
            entity.setTranslations(currentTranslations);
        }

        // 1. Güncelleme ve Ekleme
        for (PostTranslationRequest dto : dtos) {
            Optional<PostTranslation> existingTr = currentTranslations.stream()
                    .filter(t -> t.getLanguage().getCode().equals(dto.language())) // ID karşılaştırması düzeltildi
                    .findFirst();

            if (existingTr.isPresent()) {
                // Mevcut çeviriyi güncelle
                postMapper.updateTranslation(existingTr.get(), dto);
            } else {
                // Yeni çeviri ekle
                Language language = languageRepository.getReferenceById(dto.language());
                PostTranslation newTr = postMapper.toTranslationEntity(dto);
                newTr.setSlug(UtilService.toSlug(dto.title()));
                newTr.setPost(entity);
                newTr.setLanguage(language);
                currentTranslations.add(newTr);
            }
        }

        // 2. Silme (Request listesinde olmayan dilleri kaldır)
        currentTranslations.removeIf(tr ->
                dtos.stream().noneMatch(dto -> dto.language().equals(tr.getLanguage().getCode()))
        );
    }

    @Transactional
    public void addTranslation(UUID postId, PostTranslationRequest dto) {

        Post post = getRepository().findById(postId)
                .orElseThrow(() -> new BaseException("Post not found", HttpStatus.NOT_FOUND));

        boolean exists = postTranslationRepository
                .existsByPostIdAndLanguage_Code(postId, dto.language());

        if (exists) {
            throw new BaseException("Translation already exists for this language", HttpStatus.ALREADY_REPORTED);
        }

        Language language = languageRepository.findById(dto.language())
                .orElseThrow(() -> new BaseException("Language not found", HttpStatus.NOT_FOUND));

        PostTranslation translation = postMapper.toTranslationEntity(dto);
        translation.setPost(post);
        translation.setLanguage(language);
        translation.setSlug(UtilService.toSlug(dto.title()));

        post.getTranslations().add(translation);
        getRepository().save(post);
    }

    // Menu relation methods
    @Transactional
    public void addMenu(UUID postId, UUID menuId, Integer weight) {
        Post post = getRepository().findById(postId)
                .orElseThrow(() -> new BaseException("Post not found", HttpStatus.NOT_FOUND));
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new BaseException("Menu not found", HttpStatus.NOT_FOUND));

        if (weight == null) weight = 0;

        boolean exists = menuPostRepository.existsByMenuIdAndPostId(menuId, postId);
        if (exists) {
            throw new BaseException("Menu already linked to post", HttpStatus.CONFLICT);
        }
        MenuPost link = MenuPost.builder()
                .menu(menu)
                .post(post)
                .weight(weight)
                .build();
        menuPostRepository.save(link);
    }

    @Transactional
    public void removeMenu(UUID postId, UUID menuId) {
        boolean exists = menuPostRepository.existsByMenuIdAndPostId(menuId, postId);
        if (!exists) {
            throw new BaseException("Menu link not found for post", HttpStatus.NOT_FOUND);
        }
        menuPostRepository.deleteByMenuIdAndPostId(menuId, postId);
    }

    @Transactional(readOnly = true)
    public java.util.List<PostMenuLinkResponse> listMenus(UUID postId) {
        // Ensure post exists to return 404 for invalid id
        getRepository().findById(postId)
                .orElseThrow(() -> new BaseException("Post not found", HttpStatus.NOT_FOUND));
        return menuPostRepository.findByPostId(postId).stream()
                .map(mp -> new PostMenuLinkResponse(
                        mp.getMenu().getId(),
                        mp.getMenu().getTranslations() == null || mp.getMenu().getTranslations().isEmpty()
                                ? null
                                : mp.getMenu().getTranslations().iterator().next().getName(),
                        mp.getWeight()
                ))
                .toList();
    }
}
