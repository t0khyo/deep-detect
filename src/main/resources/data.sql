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