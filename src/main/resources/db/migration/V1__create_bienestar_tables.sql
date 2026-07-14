CREATE TABLE diary_entries (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    mood VARCHAR(100),
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_diary_entries_user_id ON diary_entries(user_id);

CREATE TABLE exercise_completions (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    categoria VARCHAR(100) NOT NULL DEFAULT 'RELAJACION',
    completed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_exercise_completions_user_id ON exercise_completions(user_id);
