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
        name = "holdings",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_coin",
                columnNames = {"user_id", "coin_id"}
        )
)
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id", nullable = false)
    Coin coin;

    @Column(nullable = false, precision = 19, scale = 8)
    BigDecimal quantity;

    @Column(nullable = false, precision = 19, scale = 8, name = "avg_buy_price")
    BigDecimal avgBuyPrice;

    @Version
    @Column(nullable = false)
    Long version;

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
