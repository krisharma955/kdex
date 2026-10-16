package com.k955.kdex.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "coins")
public class Coin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false, length = 10, unique = true)
    String symbol;

    @Column(nullable = false, length = 50, unique = true)
    String name;

    @Column(nullable = false, precision = 19, scale = 8)
    BigDecimal currentPrice;

    @Column(nullable = false, precision = 19, scale = 8)
    BigDecimal previousPrice;

    @Column(nullable = false, precision = 6, scale = 4)
    BigDecimal volatilityPct;

    @Builder.Default
    @Column(nullable = false)
    Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    Instant createdAt;

    @Column(nullable = false)
    Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if(createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

}
