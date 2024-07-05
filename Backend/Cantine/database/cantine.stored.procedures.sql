DELEMETER
$$

CREATE PROCEDURE removeOrder()
BEGIN
   DELETE  FROM  st_order_has_menu WHERE order_idorder  IN (SELECT id FROM `order` WHERE order_date >= DATE_SUB(NOW(), INTERVAL 12 MONTH));
   DELETE  FROM  st_order_has_meal WHERE order_idorder  IN (SELECT id FROM `order` WHERE order_date >= DATE_SUB(NOW(), INTERVAL 12 MONTH));
   DELETE  FROM `order` WHERE order_date >= DATE_SUB(NOW(), INTERVAL 6 MONTH);

END $$

call  removeOrder();