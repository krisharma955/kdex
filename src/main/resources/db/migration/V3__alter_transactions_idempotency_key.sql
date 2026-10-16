ALTER TABLE transactions
ALTER COLUMN idempotency_key TYPE UUID
USING idempotency_key::UUID;