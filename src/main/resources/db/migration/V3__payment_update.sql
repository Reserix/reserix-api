ALTER TABLE payments
DROP
CONSTRAINT fk_payments_on_user;

ALTER TABLE payments
    ADD amount DECIMAL(12, 2);

ALTER TABLE payments
    ADD provider VARCHAR(30);

ALTER TABLE payments
    ADD provider_payment_id VARCHAR(100);

ALTER TABLE payments
    ALTER COLUMN amount SET NOT NULL;

ALTER TABLE payments
    ALTER COLUMN provider SET NOT NULL;

ALTER TABLE payments
DROP
COLUMN user_id;

ALTER TABLE payments
ALTER
COLUMN status TYPE VARCHAR(30) USING (status::VARCHAR(30));