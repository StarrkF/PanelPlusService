package com.example.panelplus.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "menu_translations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"menu_id", "language"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuTranslation {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language", nullable = false)
    private Language language;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String slug;
}
