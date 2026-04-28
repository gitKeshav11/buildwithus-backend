-- Projects table
CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(250) UNIQUE,
    short_description VARCHAR(500),
    detailed_description TEXT,
    category VARCHAR(30),
    project_stage VARCHAR(20),
    collaboration_status VARCHAR(20),
    collaborators_needed INTEGER,
    is_visible BOOLEAN DEFAULT TRUE,
    owner_id BIGINT NOT NULL REFERENCES users(id),
    github_repo_url VARCHAR(500),
    live_demo_url VARCHAR(500),
    documentation_url VARCHAR(500),
    video_demo_url VARCHAR(500),
    website_url VARCHAR(500),
    views_count INTEGER DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Project tech stacks
CREATE TABLE project_tech_stacks (
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    tech_stack VARCHAR(100) NOT NULL
);

-- Project roles needed
CREATE TABLE project_roles_needed (
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    role_needed VARCHAR(30) NOT NULL
);

-- Project images
CREATE TABLE project_images (
    id SERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    display_order INTEGER DEFAULT 0,
    caption VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Project collaborators
CREATE TABLE project_collaborators (
    id SERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(30),
    is_owner BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (project_id, user_id)
);

-- Collaboration requests
CREATE TABLE collaboration_requests (
    id SERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    requester_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    requested_role VARCHAR(30),
    message TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    response_message TEXT,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_projects_visible ON projects(is_visible, is_deleted);
CREATE INDEX idx_projects_owner ON projects(owner_id);
CREATE INDEX idx_projects_slug ON projects(slug);