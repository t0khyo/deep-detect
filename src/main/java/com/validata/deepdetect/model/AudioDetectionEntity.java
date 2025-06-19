package com.validata.deepdetect.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audio_detections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AudioDetectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prediction;

    private String audioFileName;

    private String customerId;

    private LocalDateTime detectedAt;
}
