CREATE UNIQUE INDEX uq_active_seat
    ON reservation_seats(screening_id, seat_id)
    WHERE status IN ('PENDING', 'CONFIRMED');