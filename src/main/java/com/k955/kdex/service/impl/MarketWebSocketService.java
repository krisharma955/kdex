package com.k955.kdex.service.impl;

import com.k955.kdex.dtos.Coin.MarketPriceUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class MarketWebSocketService {

    private static final String MARKET_TOPIC = "/topic/market/prices";

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishPriceUpdate(MarketPriceUpdate update) {
        messagingTemplate.convertAndSend(MARKET_TOPIC, update);
    }

}
