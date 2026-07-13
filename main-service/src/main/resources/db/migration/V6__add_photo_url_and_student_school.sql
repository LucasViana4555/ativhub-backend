-- Adiciona a coluna photo_url na tabela users
ALTER TABLE users ADD COLUMN photo_url VARCHAR(500);

-- Adiciona a coluna school_name na tabela students
ALTER TABLE students ADD COLUMN school_name VARCHAR(255);
