-- User badges
CREATE TABLE user_badges (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    badge_type VARCHAR(50) NOT NULL,
    awarded_reason VARCHAR(500),
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, badge_type)
);

-- Leaderboard points
CREATE TABLE leaderboard_points (
    id SERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_points INTEGER DEFAULT 0,
    profile_completion_points INTEGER DEFAULT 0,
    projects_points INTEGER DEFAULT 0,
    collaborations_points INTEGER DEFAULT 0,
    followers_points INTEGER DEFAULT 0,
    code_reviews_points INTEGER DEFAULT 0,
    ai_engagement_points INTEGER DEFAULT 0,
    hackathon_points INTEGER DEFAULT 0,
    verification_points INTEGER DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notifications
CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    reference_id BIGINT,
    reference_type VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_leaderboard_points ON leaderboard_points(total_points DESC);
CREATE INDEX idx_notifications_user ON notifications(user_id, is_read, is_deleted);
CREATE INDEX idx_user_badges_user ON user_badges(user_id);