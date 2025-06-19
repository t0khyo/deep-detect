package com.validata.deepdetect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "video_detections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoDetectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double confidence;

    private boolean isReal;

    private String prediction;

    private String videoFileName;

    private String customerId;

    private LocalDateTime detectedAt;
}

