package com.k955.kdex.specification;

import com.k955.kdex.entity.Transaction;
import com.k955.kdex.enums.Transaction.Type;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionSpecification {

    public static Specification<Transaction> filterBy(
            UUID userId, String symbol, Type type, Instant from, Instant to
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(root.get("user").get("id"), userId)
            );

            if(symbol != null && !symbol.isBlank()) {
                predicates.add(
                        cb.equal(
                                cb.upper(
                                        root.join(
                                                "coin",
                                                jakarta.persistence.criteria.JoinType.LEFT
                                        ).get("symbol")
                                ),
                                symbol.trim().toUpperCase()
                        )
                );
            }

            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (from != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("createdAt"), from)
                );
            }

            if (to != null) {
                predicates.add(
                        cb.lessThan(root.get("createdAt"), to)
                );
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

}
