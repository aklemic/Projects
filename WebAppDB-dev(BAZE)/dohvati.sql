CREATE OR REPLACE PACKAGE DOHVATI AS
    e_iznimka EXCEPTION;
    PRAGMA EXCEPTION_INIT(e_iznimka, -20001);
    PROCEDURE p_test(l_obj IN OUT JSON_OBJECT_T);        -- Added
    PROCEDURE p_login(l_obj IN OUT JSON_OBJECT_T);
    PROCEDURE p_zupanije(l_obj IN OUT JSON_OBJECT_T);    -- Added
    PROCEDURE p_get_products(l_obj IN OUT JSON_OBJECT_T);
    PROCEDURE p_create_order(l_obj IN OUT JSON_OBJECT_T);
END DOHVATI;
/

CREATE OR REPLACE PACKAGE BODY DOHVATI AS
    -- p_test (unchanged)
    PROCEDURE p_test(l_obj IN OUT JSON_OBJECT_T) AS
        l_string VARCHAR2(4000);
    BEGIN
        l_string := l_obj.TO_STRING;
        common.p_logiraj('tu sam', 'evo ga tu je');
        l_obj.put('pozdrav', 'Hello World!');
        common.p_logiraj('tu sam', 'evo ga tu je 2');
    END p_test;

    -- p_login (unchanged)
    PROCEDURE p_login(l_obj IN OUT JSON_OBJECT_T) AS
        l_string   VARCHAR2(4000);
        l_username common.korisnici.email%type;
        l_password common.korisnici.password%type;
        l_id       common.korisnici.id%type;
        l_record   VARCHAR2(4000);
        l_out      json_array_t := json_array_t('[]');
    BEGIN
        l_string := l_obj.TO_STRING;
        l_username := JSON_VALUE(l_string, '$.username' RETURNING VARCHAR2);
        l_password := JSON_VALUE(l_string, '$.password' RETURNING VARCHAR2);

        IF (l_username IS NULL OR l_password is NULL) THEN
            l_obj.put('h_message', 'Molimo unesite korisničko ime i zaporku');
            l_obj.put('h_errcod', 101);
            RAISE e_iznimka;
        ELSE
            SELECT COUNT(1) INTO l_id
            FROM common.korisnici
            WHERE email = l_username AND password = l_password;

            IF l_id = 0 THEN
                l_obj.put('h_message', 'Nepoznato korisničko ime ili zaporka');
                l_obj.put('h_errcod', 96);  -- Fixed: h_errcode -> h_errcod
                RAISE e_iznimka;
            END IF;

            IF l_id > 1 THEN
                l_obj.put('h_message', 'Molimo javite se u Informatiku');
                l_obj.put('h_errcod', 42);  -- Fixed: h_errcode -> h_errcod
                RAISE e_iznimka;
            END IF;

            SELECT JSON_OBJECT(
                           'ID' VALUE kor.id,
                           'ime' VALUE kor.ime,
                           'prezime' VALUE kor.prezime,
                           'OIB' VALUE kor.oib,
                           'email' VALUE kor.email,
                           'spol' VALUE kor.spol,
                           'ovlasti' VALUE kor.ovlasti
                   ) INTO l_record
            FROM common.korisnici kor
            WHERE email = l_username AND password = l_password;

            l_out.append(json_object_t(l_record));
            l_obj.put('data', l_out);
        END IF;
    EXCEPTION
        WHEN e_iznimka THEN
            RAISE;
        WHEN OTHERS THEN
            common.p_errlog('p_main', dbms_utility.format_error_backtrace, sqlcode, sqlerrm, l_string);
            l_obj.put('h_message', 'Greška u obradi podataka');
            l_obj.put('h_errcod', 91);  -- Fixed: h_errcode -> h_errcod
            RAISE;
    END p_login;

    -- p_zupanije (unchanged)
    PROCEDURE p_zupanije(l_obj IN OUT JSON_OBJECT_T) AS
        l_string VARCHAR2(4000);
        l_out    json_array_t := json_array_t('[]');
        l_slova  VARCHAR2(20) := 'SA';
    BEGIN
        l_string := l_obj.to_string;
        FOR x IN (
            SELECT json_object('ID' VALUE ID, 'ZUPANIJA' VALUE ZUPANIJA) as izlaz
            FROM common.zupanije
            WHERE zupanija like '%' || l_slova || '%'
            ) LOOP
                l_out.append(JSON_OBJECT_T(x.izlaz));
            END LOOP;
        l_obj.put('data', l_out);
    EXCEPTION
        WHEN OTHERS THEN
            common.p_errlog('p_main', dbms_utility.format_error_backtrace, sqlcode, sqlerrm, l_string);
            l_obj.put('h_message', 'Greška u obradi podataka');
            l_obj.put('h_errcod', 91);  -- Fixed: h_errcode -> h_errcod
            RAISE;
    END p_zupanije;

    -- NEW: p_get_products (MISSING IMPLEMENTATION)
    PROCEDURE p_get_products(l_obj IN OUT JSON_OBJECT_T) AS
        l_products JSON_ARRAY_T := JSON_ARRAY_T();
    BEGIN
        -- Cameras
        FOR c IN (SELECT 'Camera' AS type, camera_id AS id, name, price FROM table_cameras) LOOP
                l_products.APPEND(JSON_OBJECT_T(JSON_OBJECT(
                        'type' VALUE c.type,
                        'id' VALUE c.id,
                        'name' VALUE c.name,
                        'price' VALUE c.price
                                                )));
            END LOOP;

        -- Lenses
        FOR l IN (SELECT 'Lens' AS type, lens_id AS id, name, price FROM table_lenses) LOOP
                l_products.APPEND(JSON_OBJECT_T(JSON_OBJECT(
                        'type' VALUE l.type,
                        'id' VALUE l.id,
                        'name' VALUE l.name,
                        'price' VALUE l.price
                                                )));
            END LOOP;

        -- Equipment
        FOR e IN (SELECT 'Equipment' AS type, table_equipment_id AS id, name, price FROM table_equipment) LOOP
                l_products.APPEND(JSON_OBJECT_T(JSON_OBJECT(
                        'type' VALUE e.type,
                        'id' VALUE e.id,
                        'name' VALUE e.name,
                        'price' VALUE e.price
                                                )));
            END LOOP;

        -- Films
        FOR f IN (SELECT 'Film' AS type, film_id AS id, name, price FROM table_films) LOOP
                l_products.APPEND(JSON_OBJECT_T(JSON_OBJECT(
                        'type' VALUE f.type,
                        'id' VALUE f.id,
                        'name' VALUE f.name,
                        'price' VALUE f.price
                                                )));
            END LOOP;

        l_obj.PUT('products', l_products);
    EXCEPTION
        WHEN OTHERS THEN
            l_obj.put('h_message', 'Greška u dohvaćanju proizvoda');
            l_obj.put('h_errcod', 92);
            RAISE;
    END p_get_products;

    -- p_create_order (unchanged but added missing implementation)
    PROCEDURE p_create_order(l_obj IN OUT JSON_OBJECT_T) AS
        l_customer_id NUMBER;
        l_order_id    NUMBER;
        l_items       JSON_ARRAY_T;
        l_item        JSON_OBJECT_T;
        l_type        VARCHAR2(20);
        l_id          NUMBER;
        l_quantity    NUMBER;
        l_price       NUMBER;
    BEGIN
        l_customer_id := JSON_VALUE(l_obj.TO_STRING, '$.customer_id');

        IF NOT filter.validate_customer(l_customer_id) THEN
            l_obj.PUT('h_message', 'Nevažeći ID kupca');
            l_obj.PUT('h_errcod', 400);
            RAISE e_iznimka;
        END IF;

        INSERT INTO table_orders(customer_id, order_date, status, total_amount)
        VALUES (l_customer_id, SYSDATE, 'Pending', 0)
        RETURNING order_id INTO l_order_id;

        l_items := l_obj.get_array('items');
        FOR i IN 0..l_items.get_size() - 1 LOOP
                l_item := TREAT(l_items.get(i) AS JSON_OBJECT_T);

                l_type := l_item.get_string('product_type');
                l_id := l_item.get_number('product_id');
                l_quantity := l_item.get_number('quantity');
                l_price := l_item.get_number('unit_price');

                IF NOT filter.validate_stock(l_type, l_id, l_quantity) THEN
                    l_obj.PUT('h_message', 'Nedovoljna zaliha za proizvod');
                    l_obj.PUT('h_errcod', 401);
                    RAISE e_iznimka;
                END IF;

                INSERT INTO table_order_items(
                    order_id, product_type, product_id, quantity, unit_price, total_price
                ) VALUES (
                             l_order_id, l_type, l_id, l_quantity, l_price, l_price * l_quantity
                         );
            END LOOP;

        l_obj.PUT('order_id', l_order_id);
    EXCEPTION
        WHEN OTHERS THEN
            RAISE;
    END p_create_order;
END DOHVATI;
/