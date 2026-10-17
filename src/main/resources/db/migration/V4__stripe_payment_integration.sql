ALTER TABLE users
ADD COLUMN stripe_customer_id VARCHAR(255) UNIQUE;

ALTER TABLE transactions
ADD COLUMN stripe_session_id VARCHAR(255);

ALTER TABLE transactions
ADD CONSTRAINT uk_stripe_session_id UNIQUE (stripe_session_id);
