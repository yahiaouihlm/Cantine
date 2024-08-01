CREATE OR REPLACE PROCEDURE  removeData()
LANGUAGE plpgsql AS $$
DECLARE
BEGIN
    RAISE NOTICE 'BEGINNING OF THE REMOVAL OF EXPIRED DATA';
    RAISE NOTICE ' ';
    RAISE NOTICE ' ';

    CALL removeExpiredOrders();
    CALL removeMeals();
    CALL removeMenus();
    call removeExpiredPayment();
    CALL removeStudent();

    RAISE NOTICE ' ';
    RAISE NOTICE ' ';
    RAISE NOTICE 'END OF THE REMOVAL OF EXPIRED DATA';

END; $$;



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
    RAISE NOTICE 'Opening cursor to select users (Students) to be removed';
    RAISE NOTICE ' ';
    OPEN cur;

        LOOP

            FETCH cur INTO user_id;
            EXIT WHEN NOT FOUND;
            RAISE NOTICE '';
            RAISE NOTICE '--------------------------- USER (STUDENT) % ------------------------------ \n ', user_id;
            RAISE NOTICE '';

            -- Call the stored procedure to remove orders for the current user
            CALL  removeOrdersByStudent(user_id);
            RAISE NOTICE 'ALL ORDERS OF USER % REMOVED', user_id;
            call  removeRoleByStudent(user_id);
            RAISE NOTICE 'ROLE OF USER % REMOVED', user_id;
            call  removeStudentAndhisImage(user_id);
        END LOOP;

    CLOSE cur;
    RAISE NOTICE 'Cursor closed';

END; $$;


CREATE OR REPLACE PROCEDURE removeStudentAndhisImage(student_id UUID)
LANGUAGE plpgsql AS $$
DECLARE
       user_image_id UUID;
BEGIN
    SELECT image_id INTO user_image_id FROM luser WHERE luser.id = student_id;
    DELETE FROM luser WHERE luser.id = student_id;
    RAISE NOTICE 'IMAGE %  OF USER % REMOVED',user_image_id ,student_id;
    DELETE FROM image WHERE image.id = user_image_id;
    RAISE NOTICE 'USER % REMOVED', student_id;
END; $$;



CREATE OR REPLACE PROCEDURE removeRoleByStudent(student_id UUID)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM role WHERE role.user_id = student_id;
END; $$;



CREATE OR REPLACE PROCEDURE removeOrdersByStudent(student UUID)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM lorder_has_meal WHERE order_id IN (SELECT id FROM lorder WHERE student_id = student);
    DELETE FROM lorder_has_menu WHERE order_id IN (SELECT id FROM lorder WHERE student_id = student);
    DELETE FROM lorder WHERE student_id = student;
END; $$;

/******************************** REMOVE  ORDERS ********************************/


CREATE OR REPLACE PROCEDURE removeExpiredOrders()
LANGUAGE plpgsql AS $$

DECLARE
    v_order_id UUID;
    cur CURSOR FOR
     SELECT lorder.id FROM lorder
     WHERE lorder.creation_date <= NOW() - INTERVAL '1 YEAR';
BEGIN
    RAISE NOTICE 'Opening cursor to select OrderS to be removed';
    RAISE NOTICE ' ';
    OPEN cur;
    LOOP
        FETCH cur INTO v_order_id;
            EXIT WHEN NOT FOUND;
            RAISE NOTICE '';
            RAISE NOTICE '--------------------------- ORDER  % ------------------------------ \n ', v_order_id;
            RAISE NOTICE '';
            DELETE FROM lorder_has_meal WHERE lorder_has_meal.order_id = v_order_id;
            DELETE FROM lorder_has_menu WHERE lorder_has_menu.order_id = v_order_id;
            DELETE FROM lorder WHERE lorder.id = v_order_id;
            RAISE NOTICE 'ORDER % REMOVED WITH  ALL  ITS OCCURRENCE IN lorder_has_meal AND lorder_has_menu TABLES', v_order_id;
    END LOOP;
    CLOSE cur;
   RAISE NOTICE 'Cursor closed';

END; $$;

/******************************** REMOVE  PAYMENT ********************************/

CREATE OR REPLACE PROCEDURE removeExpiredPayment()
LANGUAGE plpgsql AS $$

DECLARE
    v_payment_id UUID;
    v_student_id UUID;
    v_admin_id UUID;
    v_origin TransactionType;
    cur CURSOR FOR
        SELECT payment.id FROM payment
        WHERE payment.payment_date <= NOW() - INTERVAL '1 YEAR';
BEGIN
    RAISE NOTICE 'Opening cursor to select payment to be removed';
    RAISE NOTICE ' ';
    OPEN cur;
        LOOP
            FETCH cur INTO v_payment_id;
                EXIT WHEN NOT FOUND;
                RAISE NOTICE '';
                RAISE NOTICE '--------------------------- PAYMENT  % ------------------------------ \n ', v_payment_id;
                RAISE NOTICE '';
                SELECT student_id, admin_id, origin INTO v_student_id, v_admin_id, v_origin FROM payment WHERE payment.id = v_payment_id;
                DELETE FROM payment WHERE payment.id = v_payment_id;
                RAISE NOTICE 'PAYMENT %  OCCURRED BETWEEN STUDENT % AND  ADMIN  %  HAS ORIGIN = %  REMOVED', v_payment_id, v_student_id, v_admin_id, v_origin;
        END LOOP;
    CLOSE cur;
    RAISE NOTICE 'Cursor closed';

END; $$;






/******************************** REMOVE  MEALS :  MENU   ********************************/

CREATE OR REPLACE PROCEDURE removeMenus()
LANGUAGE plpgsql AS $$
DECLARE
    v_menu_id UUID;
    v_image_menu_id UUID;
    cur CURSOR FOR
        SELECT id FROM menu
        WHERE  status = 2;
BEGIN
        -- Open the cursor and loop through each user ID
    RAISE NOTICE 'Opening cursor to select Menus to be removed';
    RAISE NOTICE ' ';
    OPEN cur;
    LOOP

        FETCH cur INTO v_menu_id;
            EXIT WHEN NOT FOUND;
            RAISE NOTICE '';
            RAISE NOTICE '--------------------------- MENU  % ------------------------------ \n ', v_menu_id;
            RAISE NOTICE '';

            -- Call the stored procedure to remove orders for the current user
            IF isMenuContainedInOrder(v_menu_id) THEN
                RAISE NOTICE 'MENU % CONTAINED IN ORDER(S) CAN  NOT BE DELETED', v_menu_id;
            ELSE
                SELECT image_id  INTO v_image_menu_id  FROM menu WHERE menu.id = v_menu_id;
                DELETE FROM menu WHERE menu.id = v_menu_id;
                RAISE NOTICE 'MENU % REMOVED', v_menu_id;
                DELETE FROM image WHERE image.id = v_image_menu_id;
                RAISE NOTICE 'IMAGE % OF  MENU  %  IS  REMOVED',v_image_menu_id,v_menu_id;

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
      SELECT lorder_has_menu.menu_id INTO v_menu_id FROM lorder_has_menu WHERE lorder_has_menu.menu_id = isMenuContainedInOrder.menu_id;
        IF v_menu_id IS NOT NULL THEN
            RETURN TRUE;
        ELSE
            RETURN FALSE;
        END IF;

END;
    $$ LANGUAGE plpgsql;




/******************************************* REMOVE  MEAL *******************************************/

CREATE OR REPLACE PROCEDURE removeMeals()
LANGUAGE plpgsql AS $$
DECLARE
    v_meal_id UUID;
    v_image_meal_id UUID;
    cur CURSOR FOR
        SELECT id FROM meal
        WHERE  status = 2;
BEGIN
        -- Open the cursor and loop through each user ID
    RAISE NOTICE 'Opening cursor to select Meals to be removed';
    RAISE NOTICE ' ';
OPEN cur;
LOOP

    FETCH cur INTO v_meal_id;
            EXIT WHEN NOT FOUND;
            RAISE NOTICE '';
            RAISE NOTICE '--------------------------- MEAL  % ------------------------------ \n ', v_meal_id;
            RAISE NOTICE '';

            -- Call the stored procedure to remove orders for the current user
            IF isMealContainedInOrder(v_meal_id) THEN
                RAISE NOTICE 'MEAL % CONTAINED IN ORDER(S) CAN  NOT BE DELETED', v_meal_id;
            ELSIF  isMealContainedInMenu(v_meal_id) THEN
                RAISE NOTICE 'MEAL % CONTAINED IN MENU(S) CAN  NOT BE DELETED', v_meal_id;
            ELSE
                SELECT image_id  INTO v_image_meal_id  FROM meal WHERE meal.id = v_meal_id;
                DELETE FROM meal WHERE meal.id = v_meal_id;
                RAISE NOTICE 'MEAL % REMOVED', v_meal_id;
                DELETE FROM image WHERE image.id = v_image_meal_id;
                RAISE NOTICE 'IMAGE % OF  MEAL  %  IS  REMOVED',v_image_meal_id,v_meal_id;

            END IF;

    END LOOP;
CLOSE cur;
RAISE NOTICE 'Cursor closed';

END; $$;




CREATE OR REPLACE FUNCTION isMealContainedInOrder(meal_id UUID)
RETURNS BOOLEAN AS $$
DECLARE
        v_meal_id UUID;
BEGIN
        SELECT lorder_has_meal.meal_id INTO v_meal_id FROM lorder_has_meal WHERE lorder_has_meal.meal_id = isMealContainedInOrder.meal_id;
        IF v_meal_id IS NOT NULL THEN
            RETURN TRUE;
        ELSE
            RETURN FALSE;
        END IF;

END;
    $$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION isMealContainedInMenu(meal_id UUID)
RETURNS BOOLEAN AS $$
DECLARE
        v_meal_id UUID;
BEGIN
        SELECT menu_has_meal.meal_id INTO v_meal_id FROM menu_has_meal WHERE menu_has_meal.meal_id = isMealContainedInMenu.meal_id;
        IF v_meal_id IS NOT NULL THEN
            RETURN TRUE;
        ELSE
            RETURN FALSE;
        END IF;

END;
    $$ LANGUAGE plpgsql;