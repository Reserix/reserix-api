CREATE TABLE theater_thumbnails (
    id BIGSERIAL PRIMARY KEY,
    theater_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_theater_thumbnails_theater
        FOREIGN KEY (theater_id)
            REFERENCES theaters(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_theater_thumbnails_theater_id
    ON theater_thumbnails(theater_id);

INSERT INTO theater_thumbnails (
    theater_id,
    image_url,
    sort_order,
    is_primary,
    created_at,
    updated_at
)
SELECT
    id,
    thumbnail_url,
    0,
    TRUE,
    created_at,
    updated_at
FROM theaters
WHERE thumbnail_url IS NOT NULL
  AND thumbnail_url <> '';

ALTER TABLE theaters
DROP COLUMN thumbnail_url;