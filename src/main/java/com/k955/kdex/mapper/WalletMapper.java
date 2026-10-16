package com.k955.kdex.mapper;

import com.k955.kdex.dtos.Wallet.WalletResponse;
import com.k955.kdex.entity.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletResponse toWalletResponse(Wallet wallet);

}
