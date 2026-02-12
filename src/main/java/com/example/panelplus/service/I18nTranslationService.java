package com.example.panelplus.service;

import com.example.panelplus.dto.request.I18nTranslationRequest;
import com.example.panelplus.dto.response.I18nTranslationResponse;
import com.example.panelplus.entity.I18nTranslation;
import com.example.panelplus.entity.Language;
import com.example.panelplus.exception.BaseException;
import com.example.panelplus.mapper.I18nTranslationMapper;
import com.example.panelplus.repository.I18nTranslationRepository;
import com.example.panelplus.repository.LanguageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class I18nTranslationService extends BaseService<I18nTranslation, UUID, I18nTranslationRepository> {

    private final I18nTranslationMapper mapper;
    private final LanguageRepository languageRepository;

    public I18nTranslationService(I18nTranslationRepository repo,
                                  I18nTranslationMapper mapper,
                                  LanguageRepository languageRepository) {
        super(repo);
        this.mapper = mapper;
        this.languageRepository = languageRepository;
    }

    public I18nTranslationResponse create(I18nTranslationRequest request) {
        if (getRepository().existsByLanguageCodeAndKey(request.language(), request.key())) {
            throw new BaseException("Translation already exists for this key and language", HttpStatus.CONFLICT);
        }

        Language language = languageRepository.findById(request.language())
                .orElseThrow(() -> new BaseException("Language not found", HttpStatus.NOT_FOUND));

        I18nTranslation entity = mapper.toEntity(request);
        entity.setLanguage(language);

        return mapper.toResponse(getRepository().save(entity));
    }

    public I18nTranslationResponse update(UUID id, I18nTranslationRequest request) {
        I18nTranslation entity = getRepository().findById(id)
                .orElseThrow(() -> new BaseException("Translation not found", HttpStatus.NOT_FOUND));

        mapper.updateEntity(entity, request);

        if (!entity.getLanguage().getCode().equals(request.language())) {
            Language language = languageRepository.findById(request.language())
                    .orElseThrow(() -> new BaseException("Language not found", HttpStatus.NOT_FOUND));
            entity.setLanguage(language);
        }

        return mapper.toResponse(getRepository().save(entity));
    }

    @Transactional(readOnly = true)
    public I18nTranslationResponse get(UUID id) {
        return getRepository().findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BaseException("Translation not found", HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<I18nTranslationResponse> list(Pageable pageable) {
        return getRepository().findAll(pageable)
                .map(mapper::toResponse);
    }

    public void delete(UUID id) {
        if (!getRepository().existsById(id)) {
            throw new BaseException("Translation not found", HttpStatus.NOT_FOUND);
        }
        getRepository().deleteById(id);
    }

    // for frontend json nested value
    @Transactional(readOnly = true)
    public Map<String, String> getAllByLanguage(String languageCode) {
        return getRepository().findAllByLanguageCode(languageCode)
                .stream()
                .collect(Collectors.toMap(I18nTranslation::getKey, I18nTranslation::getValue));
    }
}