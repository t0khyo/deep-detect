package com.validata.deepdetect.repository;

import com.validata.deepdetect.model.VideoDetectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoDetectionRepository extends JpaRepository<VideoDetectionEntity, Long> {
}

