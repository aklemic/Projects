CREATE OR REPLACE PACKAGE filter AS
    FUNCTION validate_customer(p_customer_id NUMBER) RETURN BOOLEAN;
    FUNCTION validate_stock(
        p_product_type VARCHAR2,
        p_product_id NUMBER,
        p_quantity NUMBER
    ) RETURN BOOLEAN;
    FUNCTION validate_order_status(p_status VARCHAR2) RETURN BOOLEAN;
    FUNCTION validate_payment_method(p_method VARCHAR2) RETURN BOOLEAN;
END filter;
/

CREATE OR REPLACE PACKAGE BODY filter AS
    FUNCTION validate_customer(p_customer_id NUMBER) RETURN BOOLEAN IS
        l_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO l_count
        FROM table_customers
        WHERE customer_id = p_customer_id;
        RETURN (l_count = 1);
    EXCEPTION
        WHEN OTHERS THEN
            common.p_errlog('validate_customer', DBMS_UTILITY.FORMAT_ERROR_BACKTRACE, SQLCODE, SQLERRM, NULL);
            RETURN FALSE;
    END;

    FUNCTION validate_stock(p_product_type VARCHAR2, p_product_id NUMBER, p_quantity NUMBER) RETURN BOOLEAN IS
        l_stock NUMBER;
    BEGIN
        CASE p_product_type
            WHEN 'Camera' THEN
                SELECT stock INTO l_stock FROM table_cameras WHERE camera_id = p_product_id;
            WHEN 'Lens' THEN
                SELECT stock INTO l_stock FROM table_lenses WHERE lens_id = p_product_id;
            WHEN 'table_equipment' THEN
                SELECT stock INTO l_stock FROM table_equipment WHERE table_equipment_id = p_product_id;
            WHEN 'Film' THEN
                SELECT stock INTO l_stock FROM table_films WHERE film_id = p_product_id;
            ELSE
                RETURN FALSE;
            END CASE;
        RETURN (l_stock >= p_quantity);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN FALSE;
        WHEN OTHERS THEN
            common.p_errlog('validate_stock', DBMS_UTILITY.FORMAT_ERROR_BACKTRACE, SQLCODE, SQLERRM, NULL);
            RETURN FALSE;
    END;

    FUNCTION validate_order_status(p_status VARCHAR2) RETURN BOOLEAN IS
    BEGIN
        RETURN p_status IN ('Pending', 'Processing', 'Shipped', 'Delivered', 'Cancelled');
    END;

    FUNCTION validate_payment_method(p_method VARCHAR2) RETURN BOOLEAN IS
    BEGIN
        RETURN p_method IN ('Credit Card', 'PayPal', 'Bank Transfer');
    END;
END filter;
/