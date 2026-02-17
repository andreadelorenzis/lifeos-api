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
INSERT INTO frequency (id, name) VALUES (5, 'one-time');

-- Seed default units
INSERT INTO units (id, code, name, description) VALUES (1, 'h', 'Hours', 'Time in hours');
INSERT INTO units (id, code, name, description) VALUES (2, 'm', 'Minutes', 'Time in minutes');
INSERT INTO units (id, code, name, description) VALUES (3, 's', 'Seconds', 'Time in seconds');
INSERT INTO units (id, code, name, description) VALUES (4, 'd', 'Days', 'Time in days');
INSERT INTO units (id, code, name, description) VALUES (5, 'M', 'Months', 'Time in months');
INSERT INTO units (id, code, name, description) VALUES (6, 'y', 'Years', 'Time in years');
INSERT INTO units (id, code, name, description) VALUES (7, 'p', 'Pages', 'Number of pages');
