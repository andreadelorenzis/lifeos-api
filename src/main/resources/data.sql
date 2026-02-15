-- Seed default goal statuses
INSERT INTO goal_status (id, name) VALUES (1, 'active');
INSERT INTO goal_status (id, name) VALUES (2, 'completed');
INSERT INTO goal_status (id, name) VALUES (3, 'failed');
INSERT INTO goal_status (id, name) VALUES (4, 'paused');

-- Seed default frequencies for recurring habits
INSERT INTO frequency (id, name) VALUES (1, 'daily');
INSERT INTO frequency (id, name) VALUES (2, 'weekly');
INSERT INTO frequency (id, name) VALUES (3, 'monthly');
INSERT INTO frequency (id, name) VALUES (4, 'custom');
