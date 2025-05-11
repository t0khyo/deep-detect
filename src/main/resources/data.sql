INSERT INTO role(name) VALUES ('USER'), ('ADMIN');

INSERT INTO USERS (CREATED_AT, UPDATED_AT, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD) VALUES
('2025-02-25 02:44:59.955871', '2025-02-25 02:44:59.955871', 'Mohamed', 'Ahmed', 'mohamed.ahmed@example.com', '$2a$10$4hpvtGQZSFwKW/kcyGcY.exxOsLJOZgCdn4fRLMkYajDgXLTsgEOG'),
('2025-02-25 02:45:00.000000', '2025-02-25 02:45:00.000000', 'Ahmed', 'Hassan', 'ahmed.hassan@example.com', '$2a$10$4hpvtGQZSFwKW/kcyGcY.exxOsLJOZgCdn4fRLMkYajDgXLTsgEOG'),
('2025-02-25 02:45:01.000000', '2025-02-25 02:45:01.000000', 'Khalid', 'meaz', 'khalid.meaz@example.com', '$2a$10$4hpvtGQZSFwKW/kcyGcY.exxOsLJOZgCdn4fRLMkYajDgXLTsgEOG'),
('2025-02-25 02:45:02.000000', '2025-02-25 02:45:02.000000', 'Ziad', 'Kamal', 'ziad.kamal@example.com', '$2a$10$4hpvtGQZSFwKW/kcyGcY.exxOsLJOZgCdn4fRLMkYajDgXLTsgEOG'),
('2025-02-25 02:45:03.000000', '2025-02-25 02:45:03.000000', 'Amr', 'Youssef', 'amr.youssef@example.com', '$2a$10$4hpvtGQZSFwKW/kcyGcY.exxOsLJOZgCdn4fRLMkYajDgXLTsgEOG'),
('2025-02-25 02:45:04.000000', '2025-02-25 02:45:04.000000', 'Omar', 'Ibrahim', 'omar.ibrahim@example.com', '$2a$10$4hpvtGQZSFwKW/kcyGcY.exxOsLJOZgCdn4fRLMkYajDgXLTsgEOG');

INSERT INTO USER_ROLE (ROLE_ID, USER_ID) VALUES
(1, 1), -- Mohamed Ahmed is an ADMIN
(2, 2), -- Ahmed Hassan is a USER
(2, 3), -- Khalid Meaz is a USER
(2, 4), -- Ziad Kamal is a USER
(2, 5), -- Amr Youssef is a USER
(2, 6); -- Omar Ibrahim is a USER

INSERT INTO CUSTOMER (FIRST_NAME, LAST_NAME, SIGNATURE_URL) VALUES
('Mohamed', 'Ali', 'https://example.com/signatures/mohamed_ali.png'),
('Ahmed', 'Hassan', 'https://example.com/signatures/ahmed_hassan.png'),
('Khalid', 'Saeed', 'https://example.com/signatures/khalid_saeed.png'),
('Ziad', 'Mohamed', 'https://example.com/signatures/ziad_kamal.png'),
('Amr', 'Youssef', 'https://example.com/signatures/amr_youssef.png'),
('Omar', 'Ibrahim', 'https://example.com/signatures/omar_ibrahim.png');

-- Insert initial admin user
INSERT INTO users (id, email, password, first_name, last_name, role, created_at, updated_at)
VALUES (
    1,
    'admin@validata.com',
    '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', -- password: admin123
    'Admin',
    'User',
    'ADMIN',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert test user
INSERT INTO users (id, email, password, first_name, last_name, role, created_at, updated_at)
VALUES (
    2,
    'user@validata.com',
    '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', -- password: admin123
    'Test',
    'User',
    'USER',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert sample customers
INSERT INTO customers (id, first_name, last_name, email, phone, address, created_at, updated_at)
VALUES (
    1,
    'John',
    'Doe',
    'john.doe@example.com',
    '+1234567890',
    '123 Main St, City, Country',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO customers (id, first_name, last_name, email, phone, address, created_at, updated_at)
VALUES (
    2,
    'Jane',
    'Smith',
    'jane.smith@example.com',
    '+1987654321',
    '456 Oak Ave, Town, Country',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO customers (id, first_name, last_name, email, phone, address, created_at, updated_at)
VALUES (
    3,
    'Robert',
    'Johnson',
    'robert.johnson@example.com',
    '+1122334455',
    '789 Pine Rd, Village, Country',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert sample customer signatures (Note: These are placeholder binary data)
-- In a real application, you would need to insert actual signature image data
INSERT INTO customer_signatures (id, customer_id, signature_data, file_name, content_type, file_size, created_at, updated_at)
VALUES (
    1,
    1,
    E'\\x89504E470D0A1A0A0000000D49484452000000010000000108060000001F15C4890000000D4944415478DA63FAFFFF3F0300000500010D0A2DB40000000049454E44AE426082',
    'john_signature.png',
    'image/png',
    67,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO customer_signatures (id, customer_id, signature_data, file_name, content_type, file_size, created_at, updated_at)
VALUES (
    2,
    2,
    E'\\x89504E470D0A1A0A0000000D49484452000000010000000108060000001F15C4890000000D4944415478DA63FAFFFF3F0300000500010D0A2DB40000000049454E44AE426082',
    'jane_signature.png',
    'image/png',
    67,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Reset sequences to avoid conflicts with inserted data
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('customers_id_seq', (SELECT MAX(id) FROM customers));
SELECT setval('customer_signatures_id_seq', (SELECT MAX(id) FROM customer_signatures));