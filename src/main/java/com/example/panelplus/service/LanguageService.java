package com.example.panelplus.service;

import com.example.panelplus.dto.LanguageDto;
import com.example.panelplus.entity.Language;
import com.example.panelplus.exception.BaseException;
import com.example.panelplus.mapper.LanguageMapper;
import com.example.panelplus.repository.LanguageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LanguageService extends BaseService<Language, String, LanguageRepository> {

    private final LanguageMapper languageMapper;

    public LanguageService(LanguageRepository repo, LanguageMapper languageMapper) {
        super(repo);
        this.languageMapper = languageMapper;
    }

    public LanguageDto create(LanguageDto request) {
        if (getRepository().existsById(request.code())) {
            throw new BaseException("Language with code " + request.code() + " already exists.", HttpStatus.ALREADY_REPORTED);
        }

        if (request.defaultLanguage()) {
            resetDefaultLanguage();
        }

        Language language = languageMapper.toEntity(request);
        return languageMapper.toDto(getRepository().save(language));
    }

    @Transactional(readOnly = true)
    public LanguageDto get(String code) {
        return getRepository().findById(code).map(languageMapper::toDto).orElseThrow(() -> new BaseException("Language not found", HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<LanguageDto> list() {
        return getRepository().findAll().stream().map(languageMapper::toDto).toList();
    }


    public void delete(String code) {
        if (!getRepository().existsById(code)) {
            throw new BaseException("Language not found", HttpStatus.NOT_FOUND);
        }
        getRepository().deleteById(code);
    }

    private void resetDefaultLanguage() {
        getRepository().findByDefaultLanguageTrue().ifPresent(lang -> {
            lang.setDefaultLanguage(false);
            getRepository().save(lang);
        });
    }
}
