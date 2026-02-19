CREATE OR REPLACE PACKAGE ROUTER AS
    e_iznimka EXCEPTION;
    PRAGMA EXCEPTION_INIT(e_iznimka, -20001);
    PROCEDURE p_main(p_in IN CLOB, p_out OUT CLOB);
END ROUTER;
/

CREATE OR REPLACE PACKAGE BODY ROUTER AS
    PROCEDURE p_main(p_in IN CLOB, p_out OUT CLOB) AS
        l_obj       JSON_OBJECT_T;
        l_string    VARCHAR2(4000);
        l_procedura VARCHAR2(40);
    BEGIN
        l_obj := JSON_OBJECT_T(p_in);
        l_string := l_obj.TO_CLOB();
        l_procedura := JSON_VALUE(l_string, '$.procedura' RETURNING VARCHAR2);

        CASE l_procedura
            WHEN 'p_login'         THEN DOHVATI.p_login(l_obj);
            WHEN 'p_zupanije'      THEN DOHVATI.p_zupanije(l_obj);
            WHEN 'p_test'          THEN DOHVATI.p_test(l_obj);
            WHEN 'p_get_products'  THEN DOHVATI.p_get_products(l_obj);
            WHEN 'p_create_order'  THEN DOHVATI.p_create_order(l_obj);
            ELSE
                l_obj.PUT('h_message', 'Nepoznata metoda ' || l_procedura);
                l_obj.PUT('h_errcod', 997);
            END CASE;

        p_out := l_obj.TO_CLOB();
    EXCEPTION
        WHEN e_iznimka THEN
            p_out := l_obj.TO_CLOB();
        WHEN OTHERS THEN
            DECLARE
                l_error PLS_INTEGER := SQLCODE;
            BEGIN
                CASE l_error
                    WHEN -2292 THEN
                        l_obj.put('h_message', 'Navedeni zapis se ne može obrisati jer postoje veze na druge zapise');
                        l_obj.put('h_errcod', 99);  -- Fixed: h_errcode -> h_errcod
                    ELSE
                        common.p_errlog('p_main', DBMS_UTILITY.FORMAT_ERROR_BACKTRACE, SQLCODE, SQLERRM, l_string);
                        l_obj.put('h_message', 'Greška u obradi podataka');
                        l_obj.put('h_errcod', 100);
                        ROLLBACK;
                    END CASE;
                p_out := l_obj.TO_CLOB();
            END;
    END p_main;
END ROUTER;
/