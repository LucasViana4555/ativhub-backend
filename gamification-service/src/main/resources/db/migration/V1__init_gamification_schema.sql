CREATE TABLE students (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    xp INTEGER NOT NULL DEFAULT 0,
    level INTEGER NOT NULL DEFAULT 1,
    submissions_count INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE classrooms (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    room_code VARCHAR(6) NOT NULL UNIQUE
);

CREATE TABLE student_classrooms (
    student_id UUID NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    classroom_id UUID NOT NULL REFERENCES classrooms(id) ON DELETE CASCADE,
    PRIMARY KEY (student_id, classroom_id)
);
