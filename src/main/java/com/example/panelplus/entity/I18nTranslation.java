package com.example.panelplus.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "i18n_translations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"language", "key"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class I18nTranslation extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language", nullable = false)
    private Language language;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false, columnDefinition = "text")
    private String value;
}
