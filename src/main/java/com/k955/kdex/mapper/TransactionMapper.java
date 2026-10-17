package com.k955.kdex.mapper;

import com.k955.kdex.dtos.Transaction.TransactionResponse;
import com.k955.kdex.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "coin.symbol", target = "symbol")
    TransactionResponse toTransactionResponse(Transaction transaction);

}
