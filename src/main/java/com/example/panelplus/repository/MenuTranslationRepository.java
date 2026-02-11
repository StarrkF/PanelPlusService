package com.example.panelplus.repository;

import com.example.panelplus.entity.MenuTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuTranslationRepository extends JpaRepository<MenuTranslation, UUID> {

    Optional<MenuTranslation> findByMenuIdAndLanguage(UUID menuId, String language);

    List<MenuTranslation> findByLanguage(String language);
}
