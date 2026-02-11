package com.example.panelplus.repository;

import com.example.panelplus.entity.I18nTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface I18nTranslationRepository extends JpaRepository<I18nTranslation, UUID> {
}
