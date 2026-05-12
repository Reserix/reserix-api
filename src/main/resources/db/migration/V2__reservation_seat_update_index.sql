ALTER TABLE reservation_seats
    DROP CONSTRAINT IF EXISTS uc_88460c8d64ea8b9577089c87e;

CREATE UNIQUE INDEX uq_active_seat
    ON reservation_seats(screening_id, seat_id)
    WHERE status IN ('PENDING', 'CONFIRMED');