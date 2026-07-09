package com.example.panelplus.controller;

import com.example.panelplus.dto.request.PostRequest;
import com.example.panelplus.dto.request.PostTranslationRequest;
import com.example.panelplus.dto.response.PostResponse;
import com.example.panelplus.entity.Post;
import com.example.panelplus.mapper.PostMapper;
import com.example.panelplus.service.PostService;
import com.example.panelplus.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController extends BaseController {

    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> create(@RequestBody PostRequest request) {
        PostResponse post = postService.create(request);
        return created(post);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> get(@PathVariable UUID id) {

        Post post = postService.findById(id);

        return ok(postMapper.toResponse(post));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> list() {

        return ok(
                postService.findAll()
                        .stream()
                        .map(postMapper::toResponse)
                        .toList()
        );
    }

    @PostMapping("/{postId}/translations")
    public ResponseEntity<ApiResponse<Void>> addTranslation(@PathVariable UUID postId,  @RequestBody PostTranslationRequest request ) {
        postService.addTranslation(postId, request);
        return ok();
    }

    @PostMapping(value = "/{postId}/banner-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponse>> updateBannerImage(
            @PathVariable UUID postId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(name = "altText", required = false) String altText
    ) {
        PostResponse response = postService.updateBannerImage(postId, file, altText);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(value = "/{postId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PostResponse>> updateImage(
            @PathVariable UUID postId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(name = "altText", required = false) String altText
    ) {
        PostResponse response = postService.updateImage(postId, file, altText);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{postId}/banner-image")
    public ResponseEntity<ApiResponse<PostResponse>> removeBannerImage(@PathVariable UUID postId) {
        PostResponse response = postService.removeBannerImage(postId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{postId}/image")
    public ResponseEntity<ApiResponse<PostResponse>> removeImage(@PathVariable UUID postId) {
        PostResponse response = postService.removeImage(postId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Menu linking endpoints
    @PostMapping("/{postId}/menus/{menuId}")
    public ResponseEntity<ApiResponse<Void>> addMenu(
            @PathVariable UUID postId,
            @PathVariable UUID menuId,
            @RequestParam(name = "weight", required = false) Integer weight
    ) {
        postService.addMenu(postId, menuId, weight);
        return ok();
    }

    @DeleteMapping("/{postId}/menus/{menuId}")
    public ResponseEntity<ApiResponse<Void>> removeMenu(
            @PathVariable UUID postId,
            @PathVariable UUID menuId
    ) {
        postService.removeMenu(postId, menuId);
        return noContent();
    }
}
