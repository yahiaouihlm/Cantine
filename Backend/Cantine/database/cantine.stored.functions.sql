CREATE OR REPLACE FUNCTION check_payment_integrity()
RETURNS TRIGGER AS $$
    BEGIN
      IF is_admin(NEW.admin_id) AND is_student(NEW.student_id) THEN
        RETURN NEW;
      ELSE
        RAISE EXCEPTION 'INVALID PAYMENT, THE PAYMENT IS REQUIRES ADMIN AND STUDENT';
      END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER check_payment_integrity_trigger
    BEFORE  INSERT ON payment
    FOR EACH ROW
    EXECUTE FUNCTION check_payment_integrity();





CREATE OR REPLACE FUNCTION is_admin(user_id UUID)
RETURNS BOOLEAN AS $$
    DECLARE
       v_label VARCHAR(200);
    BEGIN
        SELECT label
        INTO  v_label
        FROM role
        WHERE role.user_id = is_admin.user_id;

    IF v_label ='ADMIN' THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;

END;
    $$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION is_student(user_id UUID)
RETURNS BOOLEAN AS $$
    DECLARE
        v_label VARCHAR(200);
    BEGIN
        SELECT label
        INTO  v_label
        FROM role
        WHERE role.user_id = is_student.user_id;
    IF v_label ='STUDENT' THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;

END;
    $$ LANGUAGE plpgsql;


/******************************** remove  user  has invalid role  ********************************/
CREATE OR REPLACE FUNCTION check_user_integrity_role()
RETURNS TRIGGER AS $$
    BEGIN
        IF NEW.label = 'ADMIN' AND NOT check_admin_integrity_role(NEW.user_id) THEN
            DELETE FROM role WHERE user_id = NEW.user_id;
            CALL remove_user_with_his_image_for_wrong_role(NEW.user_id);
            RAISE NOTICE 'WRONG ADMIN ROLE FOR USER %s, THE USER AND HIS IMAGE IS REMOVED', NEW.user_id;
        END IF;
        IF NEW.label = 'STUDENT' AND NOT check_student_integrity_role(NEW.user_id) THEN
            DELETE FROM role WHERE user_id = NEW.user_id;
            CALL remove_user_with_his_image_for_wrong_role(NEW.user_id);
            RAISE NOTICE 'WRONG STUDENT ROLE FOR USER %s, THE USER AND HIS IMAGE IS REMOVED', NEW.user_id;
        END IF;
        IF NEW.label NOT IN ('ADMIN', 'STUDENT') THEN
            DELETE FROM role WHERE user_id = NEW.user_id;
            CALL remove_user_with_his_image_for_wrong_role(NEW.user_id);
            RAISE NOTICE '%s IS UNKNOWN ROLE, WRONG ROLE FOR USER %s, THE USER AND HIS IMAGE IS REMOVED', NEW.label, NEW.user_id;
        END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER user_integrity_controls_according_to_role_trigger
        AFTER  INSERT ON role
        FOR EACH ROW
        EXECUTE FUNCTION check_user_integrity_role();


    /******************************** remove  user  has invalid role  ********************************/


CREATE OR REPLACE PROCEDURE remove_user_with_his_image_for_wrong_role(user_id UUID)
LANGUAGE plpgsql AS $$
    DECLARE
        v_image_id UUID;
    BEGIN
        SELECT image_id INTO v_image_id FROM luser WHERE id = user_id;
        DELETE FROM luser WHERE id = user_id;
        IF v_image_id IS NOT NULL THEN
            DELETE FROM image WHERE id = v_image_id;
        END IF;

        EXCEPTION
            WHEN others THEN
                RAISE NOTICE 'ERROR WHILE REMOVING USER WITH ID % ON REMOVE_USER_WITH_HIS_IMAGE PROCEDURE FOR EXCEPTION: %', user_id, SQLERRM;
    END;
$$;

    /******************************** check if the admin data integrity ********************************/


CREATE OR REPLACE FUNCTION check_admin_integrity_role(user_id UUID)
    RETURNS BOOLEAN AS $$
    DECLARE
        v_function_id UUID;
        v_class_id UUID;
        v_wallet DECIMAL(5,2);
    BEGIN
        SELECT function_id, class_id , wallet
        INTO  v_function_id, v_class_id, v_wallet
        FROM luser
        WHERE id = user_id;

        IF v_function_id IS NOT NULL AND v_wallet IS NULL AND v_class_id IS NULL THEN
            RETURN TRUE;
        ELSE
            RETURN FALSE;
        END IF;

    END;
    $$ LANGUAGE plpgsql;

    /******************************** check if the student data integrity ********************************/

CREATE OR REPLACE FUNCTION check_student_integrity_role(user_id UUID)
    RETURNS BOOLEAN AS $$
    DECLARE
        v_function_id UUID;
        v_class_id UUID;
        v_wallet DECIMAL(5,2);
    BEGIN
        SELECT function_id, class_id, wallet
        INTO v_function_id, v_class_id, v_wallet
        FROM luser
        WHERE id = user_id;

        IF v_class_id IS NOT NULL AND v_wallet IS NOT NULL AND v_function_id IS NULL THEN
            RETURN TRUE;
        ELSE
            RETURN FALSE;
        END IF;
    END;
$$ LANGUAGE plpgsql;

