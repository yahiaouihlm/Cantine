


/******************************** REMOVE  STUDENT  ********************************/

CREATE OR REPLACE PROCEDURE  removeStudent()
LANGUAGE plpgsql AS $$
DECLARE
     user_id UUID;
     cur CURSOR FOR
         SELECT luser.id FROM luser
         INNER JOIN role ON luser.id = role.user_id
         WHERE role.label = 'STUDENT'
         AND luser.status = 0
         AND luser.disable_date <= NOW() - INTERVAL '1 YEAR';

BEGIN
    -- Open the cursor and loop through each user ID
    RAISE NOTICE 'Opening cursor to select users to be removed';
    RAISE NOTICE ' ';
    OPEN cur;

        LOOP

            FETCH cur INTO user_id;
            EXIT WHEN NOT FOUND;
            RAISE NOTICE ''
            RAISE NOTICE '--------------------------- USER  % ------------------------------ \n ', user_id;
            RAISE NOTICE ''

            -- Call the stored procedure to remove orders for the current user
            CALL  removeOrdersByStudent(user_id);
            RAISE NOTICE 'ALL ORDERS OF USER % REMOVED', user_id;
            call  removeImageByStudent(user_id);
            RAISE NOTICE 'IMAGE OF USER % REMOVED', user_id;
            call  removeRoleByStudent(user_id);
            RAISE NOTICE 'ROLE OF USER % REMOVED', user_id;
            call  removeStudent(user_id);
            RAISE NOTICE 'USER % REMOVED', user_id;

        END LOOP;

    CLOSE cur;
    RAISE NOTICE 'Cursor closed';

END; $$;


CREATE OR REPLACE PROCEDURE removeStudent(student_id UUID)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM luser WHERE  luser.id = student_id;
END; $$;



CREATE OR REPLACE PROCEDURE removeRoleByStudent(student_id UUID)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM role WHERE role.user_id = student_id;
END; $$;


CREATE OR REPLACE PROCEDURE removeImageByStudent(user_id UUID)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM image WHERE image.id = user_id;  /*   user_id from  luser table  referenece  to  image by foreign ke (image,ID)  */
END; $$;


CREATE OR REPLACE PROCEDURE removeOrdersByStudent(student UUID)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM lorder_has_meal WHERE order_id IN (SELECT id FROM lorder WHERE student_id = student);
    DELETE FROM lorder_has_menu WHERE order_id IN (SELECT id FROM lorder WHERE student_id = student);
    DELETE FROM lorder WHERE student_id = student;
END; $$;

/******************************** REMOVE  MEALS :  MENU   ********************************/

CREATE OR REPLACE PROCEDURE removeMenus()
LANGUAGE plpgsql AS $$
DECLARE
    v_menu_id UUID;
    cur CURSOR FOR
        SELECT menu_id FROM menu
        WHERE  status = 2;
BEGIN
        -- Open the cursor and loop through each user ID
    RAISE NOTICE 'Opening cursor to select Menus to be removed';
    RAISE NOTICE ' ';
    OPEN cur;
    LOOP

        FETCH cur INTO v_menu_id;
            EXIT WHEN NOT FOUND;
            RAISE NOTICE ''
            RAISE NOTICE '--------------------------- MENU  % ------------------------------ \n ', v_menu_id;
            RAISE NOTICE ''

            -- Call the stored procedure to remove orders for the current user
            IF isMenuContainedInOrder(v_menu_id) THEN
                RAISE NOTICE 'MENU % CONTAINED IN ORDER(S) CAN  NOT BE DELETED', v_menu_id;
            ELSE
                DELETE FROM menu WHERE menu.id = v_menu_id;
                RAISE NOTICE 'MENU % REMOVED', v_menu_id;
            END IF;


    END LOOP;
    CLOSE cur;
    RAISE NOTICE 'Cursor closed';

END; $$;




CREATE OR REPLACE FUNCTION isMenuContainedInOrder(menu_id UUID)
RETURNS BOOLEAN AS $$
DECLARE
        v_menu_id UUID;
BEGIN
      SELECT menu_id INTO v_menu_id FROM lorder_has_menu WHERE lorder_has_menu.menu_id = menu_id;
        IF v_menu_id IS NOT NULL THEN
            RETURN TRUE;
        ELSE
            RETURN FALSE;
        END IF;

END;
    $$ LANGUAGE plpgsql;














/******************************************* REMOVE  MEAL *******************************************/

CREATE OR REPLACE FUNCTION isMealContainedInOrder(meal_id UUID)
RETURNS BOOLEAN AS $$
DECLARE
        v_meal_id UUID;
BEGIN
        SELECT meal_id INTO v_meal_id FROM lorder_has_meal WHERE lorder_has_meal.meal_id = menu_id;
        IF v_menu_id IS NOT NULL THEN
            RETURN TRUE;
        ELSE
            RETURN FALSE;
        END IF;

END;
    $$ LANGUAGE plpgsql;