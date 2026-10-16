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
@Table(
        name = "price_history",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_coin_recorded_at",
                columnNames = {"coin_id", "recorded_at"}
        )
)
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id", nullable = false)
    Coin coin;

    @Column(nullable = false, precision = 19, scale = 8)
    BigDecimal price;

    @Column(nullable = false, updatable = false)
    Instant recordedAt;

    @PrePersist
    protected void onCreate() {
        if(recordedAt == null) recordedAt = Instant.now();
    }

}
