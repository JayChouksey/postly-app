-- ===============================
-- Insert base roles
-- ===============================
INSERT INTO roles (name, description) VALUES
('AUTHOR', 'Normal registered user'),
('MODERATOR', 'Author with moderation privileges to review posts/comments'),
('ADMIN', 'Admin user with higher privileges'),
('SUPER_ADMIN', 'System super admin');
