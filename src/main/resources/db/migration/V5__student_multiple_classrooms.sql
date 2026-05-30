-- 1. Cria a tabela de junção student_classrooms
CREATE TABLE student_classrooms (
    student_id UUID NOT NULL,
    classroom_id UUID NOT NULL,
    PRIMARY KEY (student_id, classroom_id),
    CONSTRAINT fk_student_classrooms_student FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_student_classrooms_classroom FOREIGN KEY (classroom_id) REFERENCES classrooms(id) ON DELETE CASCADE
);

-- 2. Migra os relacionamentos existentes de classroom_id da tabela students para a tabela de junção
INSERT INTO student_classrooms (student_id, classroom_id)
SELECT user_id, classroom_id 
FROM students 
WHERE classroom_id IS NOT NULL;

-- 3. Remove a coluna classroom_id da tabela students
ALTER TABLE students DROP COLUMN classroom_id;
