-- V9__create_chat_tables.sql
-- Reserix Chatbot AI initial schema
-- If V9 is already used in your project, rename this file to the next Flyway version.

CREATE TABLE chat_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_chat_sessions_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT ck_chat_sessions_status
        CHECK (status IN ('ACTIVE', 'CLOSED', 'EXPIRED'))
);

CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    role VARCHAR(30) NOT NULL,
    content TEXT NOT NULL,
    intent VARCHAR(60) NULL,
    tool_name VARCHAR(80) NULL,
    tool_status VARCHAR(40) NULL,
    tool_input_json TEXT NULL,
    tool_result_json TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_chat_messages_session
        FOREIGN KEY (session_id)
        REFERENCES chat_sessions(id)
        ON DELETE CASCADE,

    CONSTRAINT ck_chat_messages_role
        CHECK (role IN ('SYSTEM', 'USER', 'ASSISTANT', 'TOOL')),

    CONSTRAINT ck_chat_messages_tool_status
        CHECK (
            tool_status IS NULL OR tool_status IN (
                'SUCCESS',
                'EMPTY',
                'VALIDATION_FAILED',
                'AUTH_REQUIRED',
                'FORBIDDEN',
                'CONFLICT',
                'ERROR'
            )
        )
);

CREATE TABLE chat_pending_actions (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    user_id BIGINT NULL,
    action_type VARCHAR(80) NOT NULL,
    action_payload_json TEXT NOT NULL,
    summary TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    consumed_at TIMESTAMP NULL,

    CONSTRAINT fk_chat_pending_actions_session
        FOREIGN KEY (session_id)
        REFERENCES chat_sessions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_chat_pending_actions_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT ck_chat_pending_actions_status
        CHECK (status IN ('PENDING', 'CONSUMED', 'EXPIRED', 'CANCELED')),

    CONSTRAINT ck_chat_pending_actions_type
        CHECK (action_type IN ('HOLD_SEATS', 'CANCEL_RESERVATION'))
);

CREATE INDEX idx_chat_sessions_user_id
    ON chat_sessions(user_id);

CREATE INDEX idx_chat_sessions_status
    ON chat_sessions(status);

CREATE INDEX idx_chat_messages_session_id_created_at
    ON chat_messages(session_id, created_at);

CREATE INDEX idx_chat_messages_intent
    ON chat_messages(intent);

CREATE INDEX idx_chat_pending_actions_session_status
    ON chat_pending_actions(session_id, status);

CREATE INDEX idx_chat_pending_actions_expires_at
    ON chat_pending_actions(expires_at);
