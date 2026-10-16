package com.k955.kdex.mapper;

import com.k955.kdex.dtos.Coin.CoinResponse;
import com.k955.kdex.entity.Coin;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoinMapper {

    CoinResponse toCoinResponse(Coin coin);

}
