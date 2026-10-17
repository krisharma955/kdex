package com.k955.kdex.service;

import com.k955.kdex.dtos.Transaction.TransactionResponse;
import com.k955.kdex.dtos.User.PortfolioResponse;
import com.k955.kdex.enums.Transaction.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface UserService {

    PortfolioResponse getUserPortfolio();

    Page<TransactionResponse> getUserTransactions(String symbol, Type type, Instant from, Instant to, Pageable pageable);

}
