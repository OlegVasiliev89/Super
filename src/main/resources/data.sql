INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('USER') ON CONFLICT (name) DO NOTHING;

-- Assign 'USER' role to existing users, or during new user creation
-- Example for a user with email 'test@example.com' (you'll need to find their user_id)
-- Assuming user ID 1 is your test user:
-- INSERT INTO user_roles (user_id, role_id) VALUES (1, (SELECT id FROM roles WHERE name = 'USER')) ON CONFLICT DO NOTHING;