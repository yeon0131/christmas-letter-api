CREATE TABLE trees (
    id BIGSERIAL PRIMARY KEY,
    slug VARCHAR(12) NOT NULL UNIQUE,
    owner_name VARCHAR(30) NOT NULL,
    theme VARCHAR(20) NOT NULL,
    admin_key_hash VARCHAR(64) NOT NULL,
    reveal_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE letters (
    id BIGSERIAL PRIMARY KEY,
    tree_id BIGINT NOT NULL REFERENCES trees(id) ON DELETE CASCADE,
    sender_name VARCHAR(20) NOT NULL,
    content VARCHAR(500) NOT NULL,
    ornament_type VARCHAR(30) NOT NULL,
    position_x INTEGER NOT NULL,
    position_y INTEGER NOT NULL,
    identity_revealed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_letters_tree_id ON letters(tree_id);
