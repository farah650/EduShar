package com.edushare_backend.edushare_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.File;

@Configuration
public class FileUploadConfig {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        createUploadDirectories();
    }

    private void createUploadDirectories() {
        try {
            File uploadRoot = new File(uploadDir);
            if (!uploadRoot.exists()) {
                boolean created = uploadRoot.mkdirs();
                if (created) {
                    System.out.println("✅ Répertoire upload créé: " + uploadRoot.getAbsolutePath());
                }
            }

            File videosDir = new File(uploadDir + "/videos");
            if (!videosDir.exists()) {
                boolean created = videosDir.mkdirs();
                if (created) {
                    System.out.println("✅ Répertoire videos créé: " + videosDir.getAbsolutePath());
                }
            }

            File thumbnailsDir = new File(uploadDir + "/thumbnails");
            if (!thumbnailsDir.exists()) {
                boolean created = thumbnailsDir.mkdirs();
                if (created) {
                    System.out.println("✅ Répertoire thumbnails créé: " + thumbnailsDir.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création des répertoires d'upload: " + e.getMessage());
        }
    }
}