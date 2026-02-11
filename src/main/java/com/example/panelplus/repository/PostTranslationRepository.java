package com.example.panelplus.repository;

import com.example.panelplus.entity.PostTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostTranslationRepository extends JpaRepository<PostTranslation, UUID> {

    Optional<PostTranslation> findByPostIdAndLanguage(UUID postId, String language);

    Optional<PostTranslation> findByLanguageAndSlug(String language, String slug);

    List<PostTranslation> findByLanguage(String language);

    boolean existsByPostIdAndLanguage_Code(UUID postId, String language);
}
