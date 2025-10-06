INSERT INTO users (email, username, password, role_id, created_at, updated_at)
VALUES (
    'superadmin@example.com',
    'superadmin',
    '$2a$10$5ONtP8hDNA3IsCFCpSEZIu5sdf.gEXMn3ydVCuUzD48gjyeIwni7W',
    (SELECT id FROM roles WHERE name = 'SUPER_ADMIN'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);