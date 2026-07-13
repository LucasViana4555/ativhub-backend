-- Alter column room_code type to VARCHAR(10) to support longer prefix-based codes (e.g. JAV-029)
ALTER TABLE classrooms ALTER COLUMN room_code TYPE VARCHAR(10);
