-- 1. Cria a tabela para os dados específicos dos professores
CREATE TABLE professors (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    school_name VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL
);

-- 2. Cria a tabela para as salas de aula, referenciando a nova tabela de professores
CREATE TABLE classrooms (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    room_code VARCHAR(6) NOT NULL UNIQUE,
    teacher_id UUID NOT NULL,
    FOREIGN KEY (teacher_id) REFERENCES professors(user_id) ON DELETE CASCADE
);

-- 3. Cria a tabela para os dados específicos dos alunos
CREATE TABLE students (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    classroom_id UUID REFERENCES classrooms(id) ON DELETE SET NULL
);

-- 4. Adiciona a coluna classroom_id na tabela de atividades
ALTER TABLE activities
ADD COLUMN IF NOT EXISTS classroom_id UUID;

-- 5. Adiciona a chave estrangeira para a tabela de salas de aula
ALTER TABLE activities
ADD CONSTRAINT fk_activities_classroom
FOREIGN KEY (classroom_id) REFERENCES classrooms(id) ON DELETE CASCADE;

-- 6. Garante que a coluna professor_id em activities referencie a tabela de professores
-- Primeiro, remove a FK antiga que apontava para 'users'
ALTER TABLE activities DROP CONSTRAINT IF EXISTS activities_professor_id_fkey;
-- Adiciona a nova FK correta que aponta para 'professors'
ALTER TABLE activities ADD CONSTRAINT fk_activities_professor FOREIGN KEY (professor_id) REFERENCES professors(user_id);
