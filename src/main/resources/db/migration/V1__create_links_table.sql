CREATE TABLE links (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    original_url TEXT NOT NULL,
    manage_token_hash CHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at TIMESTAMPTZ
);