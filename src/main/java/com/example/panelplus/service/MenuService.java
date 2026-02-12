package com.example.panelplus.service;

import com.example.panelplus.dto.request.MenuRequest;
import com.example.panelplus.dto.request.MenuTranslationRequest;
import com.example.panelplus.dto.response.MenuResponse;
import com.example.panelplus.entity.Language;
import com.example.panelplus.entity.Menu;
import com.example.panelplus.entity.MenuTranslation;
import com.example.panelplus.exception.BaseException;
import com.example.panelplus.mapper.MenuMapper;
import com.example.panelplus.repository.LanguageRepository;
import com.example.panelplus.repository.MenuRepository;
import com.example.panelplus.repository.MenuTranslationRepository;
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
public class MenuService extends BaseService<Menu, UUID, MenuRepository>  {

    private final MenuMapper menuMapper;
    private final LanguageRepository languageRepository;
    private final MenuTranslationRepository menuTranslationRepository;

    public MenuService(MenuRepository repo,
                       MenuMapper menuMapper,
                       LanguageRepository languageRepository, MenuTranslationRepository menuTranslationRepository) {
        super(repo);
        this.menuMapper = menuMapper;
        this.languageRepository = languageRepository;
        this.menuTranslationRepository = menuTranslationRepository;
    }

    public MenuResponse create(MenuRequest request) {
        Menu menu = menuMapper.toEntity(request);

        if (request.parentId() != null) {
            Menu parent = getRepository().findById(request.parentId())
                    .orElseThrow(() -> new BaseException("Parent Menu Not Found", HttpStatus.NOT_FOUND));
            menu.setParent(parent);
        }

        if (request.translations() != null && !request.translations().isEmpty()) {
            if (menu.getTranslations() == null) {
                menu.setTranslations(new HashSet<>());
            }

            for (MenuTranslationRequest trDto : request.translations()) {
                Language language = languageRepository.findById(trDto.language()).orElseThrow(() ->  new BaseException("Language not found" + trDto.language(), HttpStatus.NOT_FOUND));

                MenuTranslation tr = menuMapper.toTranslationEntity(trDto);
                tr.setSlug(UtilService.toSlug(trDto.name()));
                tr.setMenu(menu);
                tr.setLanguage(language);
                menu.getTranslations().add(tr);
            }
        }

        Menu savedMenu = getRepository().save(menu);
        return menuMapper.toResponse(savedMenu);
    }

    public MenuResponse update(UUID id, MenuRequest request) {
        Menu menu = getRepository().findById(id)
                .orElseThrow(() ->  new BaseException("Language not found", HttpStatus.NOT_FOUND));

        menuMapper.updateEntity(menu, request);

        if (request.parentId() != null) {
            // Kendisini parent yapmasını engelle
            if (request.parentId().equals(menu.getId())) {
                throw new BaseException("A menu cannot be its own parent", HttpStatus.CONFLICT);
            }
            Menu parent = getRepository().findById(request.parentId())
                    .orElseThrow(() -> new  BaseException("Parent Menu Not Found", HttpStatus.NOT_FOUND));
            menu.setParent(parent);
        } else {
            menu.setParent(null); // Parent'ı kaldır (Root menü yap)
        }

        if (request.translations() != null) {
            mergeTranslations(menu, request.translations());
        }

        Menu savedMenu = getRepository().save(menu);
        return menuMapper.toResponse(savedMenu);
    }

    @Transactional(readOnly = true)
    public MenuResponse get(UUID id) {
        return getRepository().findById(id)
                .map(menuMapper::toResponse)
                .orElseThrow(() -> new  BaseException("Menu not found", HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<MenuResponse> list(Pageable pageable) {
        return getRepository().findAll(pageable)
                .map(menuMapper::toResponse);
    }

    public void softDelete(UUID id) {
        Menu menu = getRepository().findById(id)
                .orElseThrow(() -> new  BaseException("Menu not found", HttpStatus.NOT_FOUND));

        menu.setDeletedAt(now());
        getRepository().save(menu);
    }

    private void mergeTranslations(Menu entity, List<MenuTranslationRequest> dtos) {
        if (dtos == null) return;

        Set<MenuTranslation> currentTranslations = entity.getTranslations();
        if (currentTranslations == null) {
            currentTranslations = new HashSet<>();
            entity.setTranslations(currentTranslations);
        }

        for (MenuTranslationRequest dto : dtos) {
            Optional<MenuTranslation> existingTr = currentTranslations.stream()
                    .filter(t -> t.getLanguage().getCode().equals(dto.language()))
                    .findFirst();

            if (existingTr.isPresent()) {
                MenuTranslation tr = existingTr.get();
                menuMapper.updateTranslation(tr, dto);
            } else {
                Language language = languageRepository.findById(dto.language())
                        .orElseThrow(() -> new BaseException("Language not found: " + dto.language(), HttpStatus.NOT_FOUND));

                MenuTranslation newTr = menuMapper.toTranslationEntity(dto);
                newTr.setSlug(UtilService.toSlug(dto.name()));
                newTr.setMenu(entity);
                newTr.setLanguage(language);
                currentTranslations.add(newTr);
            }
        }

        currentTranslations.removeIf(tr ->
                dtos.stream().noneMatch(dto -> dto.language().equals(tr.getLanguage().getCode()))
        );
    }

    @Transactional
    public void addTranslation(UUID menuId, MenuTranslationRequest dto) {
        Menu menu = getRepository().findById(menuId)
                .orElseThrow(() -> new  BaseException("Menu not found", HttpStatus.NOT_FOUND));

        boolean exists = menuTranslationRepository
                .existsByLanguageAndSlug(dto.language(), dto.name()); // veya sadece dil kontrolü

        if (exists) {
            // Basit dil kontrolü (Slug kontrolü değil, dilin var olup olmadığı)
            // Eğer detaylı kontrol isterseniz: existsByMenuIdAndLanguage_Id gibi bir repo metodu gerekebilir
            // Şimdilik sadece logic'i ekliyoruz:
        }

        Language language = languageRepository.findById(dto.language())
                .orElseThrow(() -> new BaseException("Language not found", HttpStatus.NOT_FOUND));

        MenuTranslation tr = menuMapper.toTranslationEntity(dto);
        tr.setSlug(UtilService.toSlug(dto.name()));
        tr.setMenu(menu);
        tr.setLanguage(language);

        menuTranslationRepository.save(tr);
    }
}
