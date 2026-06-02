ALTER TABLE seats
    ADD active BOOLEAN;

ALTER TABLE seats
    ADD display_order INTEGER;

ALTER TABLE seats
    ADD row_label VARCHAR(10);

ALTER TABLE seats
    ADD seat_label VARCHAR(20);

ALTER TABLE seats
    ADD seat_number INTEGER;

ALTER TABLE seats
    ALTER COLUMN active SET NOT NULL;

ALTER TABLE seats
    ALTER COLUMN display_order SET NOT NULL;

ALTER TABLE seats
    ALTER COLUMN row_label SET NOT NULL;

ALTER TABLE seats
    ALTER COLUMN seat_label SET NOT NULL;

ALTER TABLE seats
    ALTER COLUMN seat_number SET NOT NULL;

ALTER TABLE seats
    ADD CONSTRAINT uc_7e9346f86ca1541de3af5c3ca UNIQUE (room_id, seat_label);