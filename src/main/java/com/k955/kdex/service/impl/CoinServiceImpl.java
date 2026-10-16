package com.k955.kdex.service.impl;

import com.k955.kdex.dtos.Coin.*;
import com.k955.kdex.entity.Coin;
import com.k955.kdex.entity.User;
import com.k955.kdex.enums.User.Role;
import com.k955.kdex.exception.AccessDeniedException;
import com.k955.kdex.exception.BadRequestException;
import com.k955.kdex.exception.ErrorCode;
import com.k955.kdex.exception.ResourceNotFoundException;
import com.k955.kdex.mapper.CoinMapper;
import com.k955.kdex.repository.CoinRepository;
import com.k955.kdex.repository.PriceHistoryRepository;
import com.k955.kdex.repository.UserRepository;
import com.k955.kdex.security.JwtAuthUtil;
import com.k955.kdex.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoinServiceImpl implements CoinService {

    private final UserRepository userRepository;
    private final JwtAuthUtil jwtAuthUtil;
    private final CoinRepository coinRepository;
    private final CoinMapper coinMapper;
    private final PriceHistoryRepository priceHistoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CoinResponse> getCoins(Pageable pageable) {
        return coinRepository
                .findAll(pageable)
                .map(coinMapper::toCoinResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CoinResponse getCoinBySymbol(String symbol) {
        Coin coin = coinRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.COIN_NOT_FOUND, "This coin is unavailable or no longer exists."
                ));
        return coinMapper.toCoinResponse(coin);
    }

    @Override
    @Transactional(readOnly = true)
    public CoinHistory getCoinHistory(String symbol) {
        Coin coin = coinRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.COIN_NOT_FOUND, "We couldn't find a coin with the specified symbol."
                ));

        List<CoinHistory.PricePoint> history = priceHistoryRepository
                .findByCoinSymbolOrderByRecordedAtAsc(coin.getSymbol())
                .stream()
                .map(priceHistory -> new CoinHistory.PricePoint(
                        priceHistory.getPrice(),
                        priceHistory.getRecordedAt()
                ))
                .toList();

        return new CoinHistory(
                coin.getSymbol(),
                coin.getName(),
                coin.getCurrentPrice(),
                coin.getPreviousPrice(),
                history
        );
    }

    @Override
    @Transactional
    public CoinResponse updateCoinById(UUID coinId, UpdateCoinRequest updateCoinRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at the moment. Try Later!"
                ));

        if(!user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException(ErrorCode.REQUEST_FORBIDDEN, "Only Admins can add Coins");
        }

        Coin coin = coinRepository.findById(coinId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.COIN_NOT_FOUND, "The coin can't be loaded at the moment. Try Later!"
                ));

        if(updateCoinRequest.name() != null && !updateCoinRequest.name().isBlank()) {
            coin.setName(updateCoinRequest.name());
        }

        if(updateCoinRequest.volatilityPct() != null) {
            coin.setVolatilityPct(updateCoinRequest.volatilityPct());
        }

        Coin saved = coinRepository.save(coin);

        return coinMapper.toCoinResponse(saved);
    }

    @Override
    @Transactional
    public CoinResponse createCoin(CoinRequest coinRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at the moment. Try Later!"
                ));

        if(!user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException(ErrorCode.REQUEST_FORBIDDEN, "Only Admins can add Coins");
        }

        boolean check = coinRepository.existsBySymbol(coinRequest.symbol());
        if(check) throw new BadRequestException(ErrorCode.DUPLICATE_REQUEST, "A coin with this symbol already exists.");

        Coin coin = Coin.builder()
                .symbol(coinRequest.symbol())
                .name(coinRequest.name())
                .currentPrice(coinRequest.currentPrice())
                .previousPrice(coinRequest.previousPrice())
                .volatilityPct(coinRequest.volatilityPct())
                .build();
        Coin saved = coinRepository.save(coin);

        return coinMapper.toCoinResponse(saved);
    }

    @Override
    @Transactional
    public CoinResponse updateCoinStatus(UUID coinId, UpdateCoinStatusRequest updateCoinStatusRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at the moment. Try Later!"
                ));

        if(!user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException(ErrorCode.REQUEST_FORBIDDEN, "Only Admins can add Coins");
        }

        Coin coin = coinRepository.findById(coinId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.COIN_NOT_FOUND, "The coin can't be loaded at the moment. Try Later!"
                ));

        coin.setIsActive(updateCoinStatusRequest.isActive());

        Coin saved = coinRepository.save(coin);

        return coinMapper.toCoinResponse(saved);
    }

}
