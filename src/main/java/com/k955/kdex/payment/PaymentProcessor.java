package com.k955.kdex.payment;

import com.k955.kdex.dtos.Wallet.DepositRequest;
import com.stripe.model.Event;

public interface PaymentProcessor {

    CheckoutResponse createCheckoutSessionUrl(DepositRequest depositRequest);

    void handleWebhookEvent(Event event);

}
