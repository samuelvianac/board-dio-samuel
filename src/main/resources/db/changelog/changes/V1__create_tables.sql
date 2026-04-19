CREATE TABLE boards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL
);

CREATE TABLE board_columns (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    kind VARCHAR(30) NOT NULL,
    position_index INT NOT NULL,
    CONSTRAINT fk_board_columns_board
        FOREIGN KEY (board_id) REFERENCES boards(id)
        ON DELETE CASCADE
);

CREATE TABLE cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    column_id BIGINT NOT NULL,
    title VARCHAR(120) NOT NULL,
    description TEXT,
    blocked BOOLEAN NOT NULL DEFAULT FALSE,
    block_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cards_column
        FOREIGN KEY (column_id) REFERENCES board_columns(id)
        ON DELETE CASCADE
);

CREATE TABLE card_block_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id BIGINT NOT NULL,
    action_type VARCHAR(20) NOT NULL,
    reason VARCHAR(255),
    action_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_card_block_history_card
        FOREIGN KEY (card_id) REFERENCES cards(id)
        ON DELETE CASCADE
);
