-- prikaz svih dostupnih kamera
SELECT camera_id, name, price, stock
FROM table_cameras
WHERE stock > 0;

-- prikaz objektiva koji su ispod ili 135 eura, silazno
SELECT lens_id, name, aperture, price, stock
FROM table_lenses
WHERE price <= 135
ORDER BY PRICE DESC;

-- prikaz svih dostupnih bjeskalica
SELECT TABLE_EQUIPMENT_ID, name, price, stock
FROM TABLE_EQUIPMENT
WHERE CATEGORY = 'Bjeskalica' AND stock > 0;

-- prikaz svih filmova koji su ISO 400
SELECT film_id, name, price, stock
FROM TABLE_FILMS
WHERE NAME LIKE '% 400';

-- prikazuje narudzbe napravljene u posljednih 30 dana
SELECT order_id, order_date, status, total_amount
FROM table_orders
WHERE order_date >= SYSDATE - 30;

-- prikaz narudzbi i detalji o kupcu (inner join orders i customers)
SELECT o.order_id, c.first_name || ' ' || c.last_name AS customer_name, o.order_date, o.status, o.total_amount
FROM table_orders o
    JOIN table_customers c ON o.customer_id = c.customer_id;

-- prikaz svih proizvoda koji pripadaju nekim narudzabama
SELECT oi.order_item_id, oi.order_id, oi.product_type,
       COALESCE(c.name, l.name, e.name, f.name) AS product_name,
       oi.quantity, oi.unit_price, oi.total_price
FROM table_order_items oi
    LEFT JOIN table_cameras c ON oi.product_type = 'Camera' AND oi.product_id = c.camera_id
    LEFT JOIN table_lenses l ON oi.product_type = 'Lens' AND oi.product_id = l.lens_id
    LEFT JOIN table_equipment e ON oi.product_type = 'table_equipment' AND oi.product_id = e.table_equipment_id
    LEFT JOIN table_films f ON oi.product_type = 'Film' AND oi.product_id = f.film_id;

-- prikaz broja narudzbi po kupcu
SELECT c.customer_id, c.first_name || ' ' || c.last_name AS customer_name, COUNT(o.order_id) AS broj_narudzbi
FROM table_customers c
    LEFT JOIN table_orders o ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name;

-- koliko je svaki kupac ukupno potrosio
SELECT c.customer_id, c.first_name || ' ' || c.last_name AS customer_name,
       COALESCE(SUM(o.total_amount), 0) AS ukupna_potrosnja
FROM table_customers c
    LEFT JOIN table_orders o ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.first_name, c.last_name;

-- proizvodi koji imaju low stock
SELECT 'Camera' AS product_type, name, stock
FROM table_cameras
WHERE stock < 5
UNION ALL
SELECT 'Lens', name, stock
FROM table_lenses
WHERE stock < 5
UNION ALL
SELECT 'Equipment', name, stock
FROM table_equipment
WHERE stock < 5
UNION ALL
SELECT 'Film', name, stock
FROM table_films
WHERE stock < 5;