package com.k955.kdex.entity;

import com.k955.kdex.enums.Transaction.Type;
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
        name = "transactions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_idempotency_key",
                columnNames = {"user_id", "idempotency_key"}
        )
)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    Type type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id")
    Coin coin;

    @Column(precision = 19, scale = 8)
    BigDecimal quantity;

    @Column(precision = 19, scale = 8)
    BigDecimal price; //price of coin when bought

    @Column(nullable = false, precision = 19, scale = 4)
    BigDecimal amount; //total amount paid in one transaction

    @Column(nullable = false, precision = 19, scale = 4)
    BigDecimal balanceAfter;

    @Column(length = 100)
    String idempotencyKey;

    @Column(nullable = false, updatable = false)
    Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if(createdAt == null) createdAt = Instant.now();
    }

}
