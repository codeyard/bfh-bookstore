INSERT INTO customer
(id, username, first_name, last_name, email,
 address_street, address_city, address_state_province, address_postal_code, address_country,
 credit_card_type, credit_card_number, credit_card_expiration_month, credit_card_expiration_year)
VALUES (10000, 'Igor', 'Igor', 'Stojanovic', 'nunigu@gmail.com', 'Stauffacherstr. 27', 'Bern', 'BE', '3014', 'CH',
        'VISA', '54001105080960', 12, 2026);


INSERT INTO payment
(id, payment_date, amount, credit_card_number, transaction_id)
VALUES (1000, CURRENT_TIMESTAMP, 77.85, '54001105080960', 1);


INSERT INTO book_order
(id, order_date, amount, status, customer_id,
 address_city, address_country, address_postal_code, address_state_province, address_street,
 payment_id)
VALUES (100000, CURRENT_TIMESTAMP, 77.85, 'ACCEPTED', 10000,
        'Bern', 'CH', '3014', 'BE', 'Stauffacherstr. 27',
        1000);


INSERT INTO order_item
(id, book_isbn, book_title, book_authors, book_publisher, book_price, quantity, order_id)
VALUES (1000, '978-3-404-13089-4', 'Shining', 'Stephen King', 'Bastei', 25.95, 3, 100000);
