package com.example.panelplus.service;

import com.example.panelplus.entity.PostTranslation;
import com.example.panelplus.repository.PostTranslationRepository;

import java.util.UUID;

public class PostTranslationService extends BaseService<PostTranslation, UUID, PostTranslationRepository> {

    public PostTranslationService(PostTranslationRepository repo) {
        super(repo);
    }
}
