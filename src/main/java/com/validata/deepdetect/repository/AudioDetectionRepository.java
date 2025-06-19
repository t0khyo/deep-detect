package com.validata.deepdetect.repository;

import com.validata.deepdetect.model.AudioDetectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudioDetectionRepository extends JpaRepository<AudioDetectionEntity, Long> {
}
