CREATE TABLE coins (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    symbol VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL UNIQUE,
    current_price NUMERIC(19,8) NOT NULL,
    previous_price NUMERIC(19,8) NOT NULL,
    volatility_pct NUMERIC(6,4) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT chk_current_price CHECK (current_price > 0),
    CONSTRAINT chk_previous_price CHECK (previous_price > 0),
    CONSTRAINT chk_volatility_pct CHECK (volatility_pct > 0 AND volatility_pct <= 20)
);

CREATE TABLE price_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coin_id UUID NOT NULL REFERENCES coins(id),
    price NUMERIC(19,8) NOT NULL,
    recorded_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT chk_price CHECK (price > 0),
    CONSTRAINT uk_coin_recorded_at UNIQUE (coin_id, recorded_at)
);

CREATE TABLE holdings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    coin_id UUID NOT NULL REFERENCES coins(id),
    quantity NUMERIC(19,8) NOT NULL,
    avg_buy_price NUMERIC(19,8) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT chk_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_avg_buy_price CHECK (avg_buy_price >= 0),
    CONSTRAINT uk_user_coin UNIQUE (user_id, coin_id)
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    type VARCHAR(100) NOT NULL,
    coin_id UUID REFERENCES coins(id),
    quantity NUMERIC(19,8),
    price NUMERIC(19,8),
    amount NUMERIC(19,4) NOT NULL,
    balance_after NUMERIC(19,4) NOT NULL,
    idempotency_key VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT chk_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_price CHECK (price > 0),
    CONSTRAINT chk_amount CHECK (amount > 0),
    CONSTRAINT chk_balance_after CHECK (balance_after >= 0),
    CONSTRAINT uk_user_idempotency_key UNIQUE (user_id, idempotency_key),
    CONSTRAINT chk_type CHECK (type IN ('SIGNUP_BONUS', 'DEPOSIT', 'BUY', 'SELL'))
);