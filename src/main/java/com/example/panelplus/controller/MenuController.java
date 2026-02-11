package com.example.panelplus.controller;

import com.example.panelplus.dto.request.MenuRequest;
import com.example.panelplus.dto.request.MenuTranslationRequest;
import com.example.panelplus.dto.response.MenuResponse;
import com.example.panelplus.entity.Menu;
import com.example.panelplus.mapper.MenuMapper;
import com.example.panelplus.service.MenuService;
import com.example.panelplus.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController extends BaseController {

    private final MenuService menuService;
    private final MenuMapper menuMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<MenuResponse>> create(@RequestBody MenuRequest request) {
        // Service katmanı burada create işleminden sonra direkt Response dönüyor
        MenuResponse menu = menuService.create(request);
        return created(menu);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> get(@PathVariable UUID id) {
        // BaseService'den gelen findById Entity döner, burada Mapper ile Response'a çeviriyoruz
        Menu menu = menuService.findById(id);
        return ok(menuMapper.toResponse(menu));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MenuResponse>>> list() {
        // BaseService'den gelen findAll Entity listesi döner, Stream ile çeviriyoruz
        return ok(
                menuService.findAll()
                        .stream()
                        .map(menuMapper::toResponse)
                        .toList()
        );
    }

    // Bu metodun çalışabilmesi için MenuService'e addTranslation metodunu eklemeniz gerekir
    @PostMapping("/{menuId}/translations")
    public ResponseEntity<ApiResponse<Void>> addTranslation(@PathVariable UUID menuId, @RequestBody MenuTranslationRequest request) {
        menuService.addTranslation(menuId, request);
        return ok();
    }
}