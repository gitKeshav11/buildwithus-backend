-- Verification requests
CREATE TABLE verification_requests (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    github_linked BOOLEAN DEFAULT FALSE,
    linkedin_linked BOOLEAN DEFAULT FALSE,
    portfolio_submitted BOOLEAN DEFAULT FALSE,
    profile_complete BOOLEAN DEFAULT FALSE,
    has_projects BOOLEAN DEFAULT FALSE,
    admin_notes TEXT,
    reviewed_by BIGINT REFERENCES users(id),
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Hackathons
CREATE TABLE hackathons (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    organizer VARCHAR(200),
    description TEXT,
    registration_link VARCHAR(500),
    event_date DATE,
    end_date DATE,
    location VARCHAR(200),
    is_online BOOLEAN DEFAULT FALSE,
    team_size INTEGER,
    prize_info TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    posted_by BIGINT REFERENCES users(id),
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Hackathon required roles
CREATE TABLE hackathon_required_roles (
    hackathon_id BIGINT NOT NULL REFERENCES hackathons(id) ON DELETE CASCADE,
    required_role VARCHAR(30) NOT NULL
);

-- Hackathon tags
CREATE TABLE hackathon_tags (
    hackathon_id BIGINT NOT NULL REFERENCES hackathons(id) ON DELETE CASCADE,
    tag VARCHAR(100) NOT NULL
);

-- Team finder posts
CREATE TABLE team_finder_posts (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hackathon_id BIGINT REFERENCES hackathons(id),
    post_type VARCHAR(30) NOT NULL,
    title VARCHAR(200),
    message TEXT,
    preferred_experience VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Team finder roles needed
CREATE TABLE team_finder_roles_needed (
    post_id BIGINT NOT NULL REFERENCES team_finder_posts(id) ON DELETE CASCADE,
    role_needed VARCHAR(30) NOT NULL
);

-- Team finder skills required
CREATE TABLE team_finder_skills_required (
    post_id BIGINT NOT NULL REFERENCES team_finder_posts(id) ON DELETE CASCADE,
    skill VARCHAR(100) NOT NULL
);

-- Team join requests
CREATE TABLE team_join_requests (
    id SERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES team_finder_posts(id) ON DELETE CASCADE,
    requester_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    response_message TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_hackathons_active ON hackathons(is_active, is_deleted);
CREATE INDEX idx_team_finder_active ON team_finder_posts(is_active, is_deleted);