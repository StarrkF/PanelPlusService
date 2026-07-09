package com.example.panelplus.service;

import com.example.panelplus.entity.Document;
import com.example.panelplus.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentStorageService {

    private final DocumentRepository documentRepository;

    private final Path uploadRoot = Path.of("uploads");

    @Transactional
    public Document store(MultipartFile file, String folder, String altText) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Dosya boş olamaz.");
        }

        try {
            String originalFileName = StringUtils.cleanPath(
                    file.getOriginalFilename() == null ? "file" : file.getOriginalFilename()
            );

            String extension = extractExtension(originalFileName);
            String storedFileName = UUID.randomUUID() + (extension == null ? "" : "." + extension);

            Path targetDirectory = uploadRoot.resolve(folder).normalize();
            Files.createDirectories(targetDirectory);

            Path targetPath = targetDirectory.resolve(storedFileName).normalize();
            file.transferTo(targetPath);

            String relativePath = uploadRoot.resolve(folder).resolve(storedFileName).toString().replace("\\", "/");

            Document document = Document.builder()
                    .originalFileName(originalFileName)
                    .storedFileName(storedFileName)
                    .path(relativePath)
                    .url("/" + relativePath)
                    .contentType(file.getContentType())
                    .extension(extension)
                    .size(file.getSize())
                    .altText(altText)
                    .build();

            return documentRepository.save(document);
        } catch (IOException e) {
            throw new IllegalStateException("Dosya kaydedilemedi.", e);
        }
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return null;
        }

        return fileName.substring(dotIndex + 1).toLowerCase();
    }
}
