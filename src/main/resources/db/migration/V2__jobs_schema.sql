-- Jobs table
CREATE TABLE jobs (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    company_name VARCHAR(200) NOT NULL,
    company_logo_url VARCHAR(500),
    description TEXT NOT NULL,
    job_type VARCHAR(20) NOT NULL,
    role_category VARCHAR(30),
    work_mode VARCHAR(20),
    location VARCHAR(200),
    salary_range VARCHAR(100),
    apply_link VARCHAR(1000) NOT NULL,
    last_date DATE,
    posted_by BIGINT REFERENCES users(id),
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    views_count INTEGER DEFAULT 0,
    clicks_count INTEGER DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Job tags
CREATE TABLE job_tags (
    job_id BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    tag VARCHAR(100) NOT NULL
);

-- Saved jobs
CREATE TABLE saved_jobs (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    job_id BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, job_id)
);

CREATE INDEX idx_jobs_active ON jobs(is_active, is_deleted);
CREATE INDEX idx_jobs_type ON jobs(job_type);
CREATE INDEX idx_jobs_role ON jobs(role_category);