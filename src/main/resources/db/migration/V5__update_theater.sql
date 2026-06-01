ALTER TABLE theaters
    ADD description TEXT;

ALTER TABLE theaters
    ADD email VARCHAR(255);

ALTER TABLE theaters
    ADD latitude DOUBLE PRECISION;

ALTER TABLE theaters
    ADD longitude DOUBLE PRECISION;

ALTER TABLE theaters
    ADD owner_id BIGINT;

ALTER TABLE theaters
    ADD phone_number VARCHAR(255);

ALTER TABLE theaters
    ADD status VARCHAR(255);

ALTER TABLE theaters
    ADD thumbnail_url VARCHAR(255);

ALTER TABLE theaters
    ALTER COLUMN latitude SET NOT NULL;

ALTER TABLE theaters
    ALTER COLUMN longitude SET NOT NULL;

ALTER TABLE theaters
    ALTER COLUMN owner_id SET NOT NULL;

ALTER TABLE theaters
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE theaters
    ADD CONSTRAINT FK_THEATERS_OWNER FOREIGN KEY (owner_id) REFERENCES users (id);