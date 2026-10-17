package com.k955.kdex.payment;

import com.k955.kdex.dtos.Wallet.DepositRequest;
import com.k955.kdex.entity.Transaction;
import com.k955.kdex.entity.User;
import com.k955.kdex.entity.Wallet;
import com.k955.kdex.enums.Transaction.Type;
import com.k955.kdex.exception.BadRequestException;
import com.k955.kdex.exception.ErrorCode;
import com.k955.kdex.exception.ResourceNotFoundException;
import com.k955.kdex.repository.TransactionRepository;
import com.k955.kdex.repository.UserRepository;
import com.k955.kdex.repository.WalletRepository;
import com.k955.kdex.security.JwtAuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentProcessor implements PaymentProcessor {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final JwtAuthUtil jwtAuthUtil;
    private final TransactionRepository transactionRepository;

    @Value("${app.cors.allowed-origins}")
    private String frontendUrl;

    @Override
    public CheckoutResponse createCheckoutSessionUrl(DepositRequest depositRequest) {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at the moment. Try Again!"
                ));

        long amountInCents = depositRequest.amount()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();

        var params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/success")
                .setCancelUrl(frontendUrl + "/cancel")
                .putMetadata("user_id", userId.toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Wallet Deposit")
                                                                .build())
                                                .build())
                                .build());

        String stripeCustomerId = user.getStripeCustomerId();
        if (stripeCustomerId == null || stripeCustomerId.isEmpty()) {
            params.setCustomerEmail(user.getEmail());
        } else {
            params.setCustomer(stripeCustomerId);
        }

        try {
            Session session = Session.create(params.build());
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            log.error("Failed to create Stripe checkout session for user {}", userId, e);
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "We couldn't start the payment. Please try again.");
        }
    }

    @Override
    public PortalResponse openCustomerPortal() {
        UUID userId = jwtAuthUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at the moment. Try Again!"
                ));

        String stripeCustomerId = user.getStripeCustomerId();

        if (stripeCustomerId == null || stripeCustomerId.isEmpty()) {
            throw new BadRequestException(ErrorCode.STRIPE_CUSTOMER_ID_NOT_FOUND, "User does not have a Stripe Customer Id");
        }

        try {
            var portalSession = com.stripe.model.billingportal.Session.create(
                    com.stripe.param.billingportal.SessionCreateParams.builder()
                            .setCustomer(stripeCustomerId)
                            .setReturnUrl(frontendUrl)
                            .build()
            );
            return new PortalResponse(portalSession.getUrl());
        } catch (StripeException e) {
            log.error("Failed to open Stripe billing portal for user {}", userId, e);
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "We couldn't open the billing portal. Please try again.");
        }
    }

    @Override
    @Transactional
    public void handleWebhookEvent(Event event) {
        if (!"checkout.session.completed".equals(event.getType())) {
            log.debug("Ignoring Stripe webhook event type: {}", event.getType());
            return;
        }

        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
        if (!(stripeObject instanceof Session session)) {
            log.warn("Stripe webhook 'checkout.session.completed' had no checkout session payload");
            return;
        }

        if (transactionRepository.existsByStripeSessionId(session.getId())) {
            log.info("Stripe session {} already processed, skipping", session.getId());
            return;
        }

        String userIdValue = session.getMetadata() == null ? null : session.getMetadata().get("user_id");
        if (userIdValue == null) {
            log.warn("Stripe session {} is missing the user_id metadata", session.getId());
            return;
        }

        User user = userRepository.findById(UUID.fromString(userIdValue))
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "The User Details can't be loaded at the moment. Try Again!"
                ));

        if ((user.getStripeCustomerId() == null || user.getStripeCustomerId().isEmpty())
                && session.getCustomer() != null) {
            user.setStripeCustomerId(session.getCustomer());
            userRepository.save(user);
        }

        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.WALLET_NOT_FOUND, "We couldn't find your wallet. Please try again."
                ));

        long amountTotal = session.getAmountTotal() == null ? 0L : session.getAmountTotal();
        BigDecimal amount = BigDecimal.valueOf(amountTotal).movePointLeft(2);

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(Type.DEPOSIT)
                .amount(amount)
                .balanceAfter(wallet.getBalance())
                .stripeSessionId(session.getId())
                .build();
        transactionRepository.save(transaction);

        log.info("Credited {} to wallet of user {} for Stripe session {}", amount, user.getId(), session.getId());
    }

}
