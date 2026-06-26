-- SQL Script to fix corrupted UTF-8 data in PostgreSQL
-- Run this script to fix "mojibake" characters like "Ð¢ÐµÑÑ" which should be "Тест"

-- The issue: UTF-8 data was double-encoded (UTF-8 bytes interpreted as Latin-1 and re-encoded as UTF-8)
-- Solution: Convert back using Latin-1 to UTF-8 conversion

-- First, check for corrupted data in reviews
SELECT id, review_text,
       convert_from(convert_to(review_text, 'LATIN1'), 'UTF8') as fixed_text
FROM reviews
WHERE review_text LIKE '%Ð%' OR review_text LIKE '%Ñ%';

-- Fix reviews table - review_text column
UPDATE reviews
SET review_text = convert_from(convert_to(review_text, 'LATIN1'), 'UTF8')
WHERE review_text LIKE '%Ð%' OR review_text LIKE '%Ñ%';

-- Check for corrupted data in users table
SELECT id, name, last_name,
       convert_from(convert_to(name, 'LATIN1'), 'UTF8') as fixed_name,
       convert_from(convert_to(last_name, 'LATIN1'), 'UTF8') as fixed_lastname
FROM users
WHERE name LIKE '%Ð%' OR name LIKE '%Ñ%' OR last_name LIKE '%Ð%' OR last_name LIKE '%Ñ%';

-- Fix users table - name and last_name columns
UPDATE users
SET name = convert_from(convert_to(name, 'LATIN1'), 'UTF8')
WHERE name LIKE '%Ð%' OR name LIKE '%Ñ%';

UPDATE users
SET last_name = convert_from(convert_to(last_name, 'LATIN1'), 'UTF8')
WHERE last_name LIKE '%Ð%' OR last_name LIKE '%Ñ%';

-- Check for corrupted data in products table
SELECT id, name,
       convert_from(convert_to(name, 'LATIN1'), 'UTF8') as fixed_name
FROM products
WHERE name LIKE '%Ð%' OR name LIKE '%Ñ%';

-- Fix products table - name column
UPDATE products
SET name = convert_from(convert_to(name, 'LATIN1'), 'UTF8')
WHERE name LIKE '%Ð%' OR name LIKE '%Ñ%';

-- Check for corrupted data in categories table
SELECT id, name,
       convert_from(convert_to(name, 'LATIN1'), 'UTF8') as fixed_name
FROM categories
WHERE name LIKE '%Ð%' OR name LIKE '%Ñ%';

-- Fix categories table
UPDATE categories
SET name = convert_from(convert_to(name, 'LATIN1'), 'UTF8')
WHERE name LIKE '%Ð%' OR name LIKE '%Ñ%';

-- Check for corrupted data in characteristics table
SELECT id, name,
       convert_from(convert_to(name, 'LATIN1'), 'UTF8') as fixed_name
FROM characteristics
WHERE name LIKE '%Ð%' OR name LIKE '%Ñ%';

-- Fix characteristics table
UPDATE characteristics
SET name = convert_from(convert_to(name, 'LATIN1'), 'UTF8')
WHERE name LIKE '%Ð%' OR name LIKE '%Ñ%';

-- Check for corrupted data in characteristic_values table
SELECT id, value,
       convert_from(convert_to(value, 'LATIN1'), 'UTF8') as fixed_value
FROM characteristic_values
WHERE value LIKE '%Ð%' OR value LIKE '%Ñ%';

-- Fix characteristic_values table
UPDATE characteristic_values
SET value = convert_from(convert_to(value, 'LATIN1'), 'UTF8')
WHERE value LIKE '%Ð%' OR value LIKE '%Ñ%';

-- Check for corrupted data in orders table
SELECT id, address,
       convert_from(convert_to(address, 'LATIN1'), 'UTF8') as fixed_address
FROM orders
WHERE address LIKE '%Ð%' OR address LIKE '%Ñ%';

-- Fix orders table
UPDATE orders
SET address = convert_from(convert_to(address, 'LATIN1'), 'UTF8')
WHERE address LIKE '%Ð%' OR address LIKE '%Ñ%';

-- Verify the fixes
SELECT 'reviews' as table_name, COUNT(*) as remaining_issues
FROM reviews WHERE review_text LIKE '%Ð%' OR review_text LIKE '%Ñ%'
UNION ALL
SELECT 'users', COUNT(*) FROM users WHERE name LIKE '%Ð%' OR last_name LIKE '%Ð%'
UNION ALL
SELECT 'products', COUNT(*) FROM products WHERE name LIKE '%Ð%' OR name LIKE '%Ñ%'
UNION ALL
SELECT 'categories', COUNT(*) FROM categories WHERE name LIKE '%Ð%' OR name LIKE '%Ñ%'
UNION ALL
SELECT 'characteristics', COUNT(*) FROM characteristics WHERE name LIKE '%Ð%' OR name LIKE '%Ñ%'
UNION ALL
SELECT 'characteristic_values', COUNT(*) FROM characteristic_values WHERE value LIKE '%Ð%' OR value LIKE '%Ñ%'
UNION ALL
SELECT 'orders', COUNT(*) FROM orders WHERE address LIKE '%Ð%' OR address LIKE '%Ñ%';
