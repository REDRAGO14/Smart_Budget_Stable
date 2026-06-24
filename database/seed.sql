USE smart_budget;

-- =========================
-- RESET TABLES (SAFE TESTING)
-- =========================
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE expenses;
TRUNCATE TABLE incomes;
TRUNCATE TABLE budgets;
TRUNCATE TABLE categories;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- USERS
-- =========================
INSERT INTO users (username, email, password)
VALUES 
('admin', 'admin@gmail.com', '1234'),
('john', 'john@gmail.com', '1234');

-- =========================
-- CATEGORIES
-- =========================
INSERT INTO categories (category_name)
VALUES 
('Food'),
('Transport'),
('Rent'),
('Education'),
('Entertainment');