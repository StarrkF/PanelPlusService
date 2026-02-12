package com.example.panelplus.controller;

import com.example.panelplus.dto.LanguageDto;
import com.example.panelplus.service.LanguageService;
import com.example.panelplus.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/languages")
@RequiredArgsConstructor
public class LanguageController extends BaseController {

    private final LanguageService languageService;

    @PostMapping
    public ResponseEntity<ApiResponse<LanguageDto>> create(@RequestBody LanguageDto request) {
        return created(languageService.create(request));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<LanguageDto>> get(@PathVariable String code) {
        return ok(languageService.get(code));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LanguageDto>>> list() {
        return ok(languageService.list());
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String code) {
        languageService.delete(code);
        return ok();
    }
}
