ALTER TABLE users
    ALTER COLUMN password SET NOT NULL;

ALTER TABLE users
    DROP COLUMN provider_id;

ALTER TABLE users
    DROP COLUMN provider;