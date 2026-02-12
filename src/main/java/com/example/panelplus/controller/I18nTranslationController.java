package com.example.panelplus.controller;

import com.example.panelplus.dto.request.I18nTranslationRequest;
import com.example.panelplus.dto.response.I18nTranslationResponse;
import com.example.panelplus.service.I18nTranslationService;
import com.example.panelplus.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/translations")
@RequiredArgsConstructor
public class I18nTranslationController extends BaseController {

    private final I18nTranslationService service;

    @PostMapping
    public ResponseEntity<ApiResponse<I18nTranslationResponse>> create(@RequestBody I18nTranslationRequest request) {
        return created(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<I18nTranslationResponse>> update(@PathVariable UUID id, @RequestBody I18nTranslationRequest request) {
        return ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<I18nTranslationResponse>> get(@PathVariable UUID id) {
        return ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<I18nTranslationResponse>>> list(Pageable pageable) {
        return ok(service.list(pageable).getContent());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.delete(id);
        return ok();
    }

    @GetMapping("/lang/{languageCode}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getByLanguage(@PathVariable String languageCode) {
        return ok(service.getAllByLanguage(languageCode));
    }
}