CREATE TABLE activities (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    xp_reward INTEGER NOT NULL DEFAULT 10,
    professor_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (professor_id) REFERENCES users(id)
);

CREATE TABLE activity_submissions (
    id UUID PRIMARY KEY,
    activity_id UUID NOT NULL,
    student_id UUID NOT NULL,
    answer TEXT NOT NULL,
    feedback TEXT,
    grade INTEGER,
    submitted_at TIMESTAMP NOT NULL,
    FOREIGN KEY (activity_id) REFERENCES activities(id),
    FOREIGN KEY (student_id) REFERENCES users(id)
);