-- For Employees Module

-- createEmployee function in java
-- add_employee procedure
-- employees BEFORE INSERT trigger
USE `DBSALES26_G208`;
DROP procedure IF EXISTS `add_employee`;

USE `DBSALES26_G208`;
DROP procedure IF EXISTS `DBSALES26_G208`.`add_employee`;
;

DELIMITER $$
USE `DBSALES26_G208`$$
CREATE DEFINER=`DBADM_208`@`%` PROCEDURE `add_employee`(
	IN p_lastName varchar(50), 
	IN p_firstName varchar(50), 
	IN p_extension varchar(10), 
	IN p_email varchar(100), 
	IN p_jobTitle varchar(50), 
    IN p_employee_Type varchar(50),
    IN p_depCode INT,
	IN p_end_username varchar(45),
	IN p_end_userreason varchar(45)
)
BEGIN
	DECLARE employee_type ENUM('S','N');
    DECLARE employeeNumber1 INT;
    DECLARE forlock INT;

		IF p_employee_Type = 'Sales Representatives'  THEN
			SET employee_type := 'S';
		ELSEIF p_employee_Type = 'Inventory Manager' OR p_employee_Type = 'Sales Manager' THEN
			SET employee_type := 'N';
		ELSE
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ERROR 80A2: Invalid employee type';
		END IF;
		
	   
		INSERT INTO employees (lastName, firstName, extension, email, jobTitle, employee_type, end_username, end_userreason)
		VALUES (p_lastName, p_firstName, p_extension, p_email, p_jobTitle, employee_type, p_end_username, p_end_userreason);
		
        SELECT COUNT(1) INTO forLock FROM employees FOR UPDATE;
		SELECT MAX(employeeNumber) INTO employeeNumber1 FROM employees;
		IF p_employee_Type = 'Sales Representatives'  THEN
			INSERT INTO salesRepresentatives (employeeNumber) VALUES (employeeNumber1);
		ELSEIF p_employee_Type = 'Inventory Manager' THEN
			INSERT INTO Non_SalesRepresentatives (employeeNumber, deptCode) VALUES (employeeNumber1, p_depCode);
			INSERT INTO inventory_managers (employeeNumber) VALUES (employeeNumber1);
		ELSEIF p_employee_Type = 'Sales Manager' THEN
			INSERT INTO Non_SalesRepresentatives (employeeNumber, deptCode) VALUES (employeeNumber1, p_depCode);
			INSERT INTO sales_managers (employeeNumber) VALUES (employeeNumber1);
		ELSE
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ERROR 80A2: Invalid employee type';
		END IF;
END$$

DELIMITER ;
;



DROP TRIGGER IF EXISTS `DBSALES26_G208`.`employees_BEFORE_INSERT`;

DELIMITER $$
USE `DBSALES26_G208`$$
CREATE DEFINER=`DBADM_208`@`%` TRIGGER `employees_BEFORE_INSERT` BEFORE INSERT ON `employees` FOR EACH ROW BEGIN
	-- System Generated Identifier
    DECLARE current_year CHAR(4);
    DECLARE fixed_part INT;
    DECLARE lastNumber  INT;
    DECLARE NumberString VARCHAR(255);
    DECLARE lastThreeChars VARCHAR(3);
    
    DECLARE forLock INT;
    DECLARE newemployeeNumber INT; 
    
    SET current_year = DATE_FORMAT(CURDATE(), '%Y');
    SET fixed_part = '02';
	
    SELECT COUNT(1) INTO forLock FROM employees_audit FOR UPDATE;
	SELECT MAX(employeeNumber) INTO lastNumber FROM employees_audit;
    
    SET NumberString = CAST(lastNumber AS CHAR);
    SET lastThreeChars = RIGHT(NumberString, 3);
    
    SET newemployeeNumber = CAST(CONCAT(current_year, fixed_part, lastThreeChars) AS UNSIGNED) + 1;
	IF (lastNumber is NULL) THEN
		SET newemployeeNumber := CAST(CONCAT(current_year, fixed_part, '001') AS UNSIGNED);
	END IF;
		SET new.employeeNumber := newemployeeNumber;
        
	SET new.is_deactivated := 0;
END$$
DELIMITER ;



-- for reclassifyEmployee function in java
-- since all procedures update employees table, i put lock on java before calling procedure to change
-- for locks in getEmployeeType - no start and commit here -- locked inventory and sales manager table and salesrep
USE `DBSALES26_G208`;
DROP function IF EXISTS `getEmployeeType`;

USE `DBSALES26_G208`;
DROP function IF EXISTS `DBSALES26_G208`.`getEmployeeType`;
;

DELIMITER $$
USE `DBSALES26_G208`$$
CREATE DEFINER=`DBADM_208`@`%` FUNCTION `getEmployeeType`( v_employeeNumber INT) RETURNS smallint
    READS SQL DATA
    DETERMINISTIC
BEGIN
	DECLARE v_tableName SMALLINT(1);
    DECLARE forLock INT;
    
	/*
		1 - sales_representatives
        2 - sales_managers
        3 - inventory_managers
    */
    
    
    -- Check in sales_representatives
    SELECT COUNT(1) INTO forLock FROM salesRepresentatives FOR UPDATE;
    IF EXISTS (SELECT 1 FROM salesRepresentatives WHERE employeeNumber = v_employeeNumber) THEN
        SET v_tableName = 1;
    -- Check in sales_managers
    SELECT COUNT(1) INTO forLock FROM sales_managers FOR UPDATE;
    ELSEIF EXISTS (SELECT 1 FROM sales_managers WHERE employeeNumber = v_employeeNumber) THEN
        SET v_tableName = 2;
    -- Check in inventory_managers
    SELECT COUNT(1) INTO forLock FROM inventory_managers FOR UPDATE;
    ELSEIF EXISTS (SELECT 1 FROM inventory_managers WHERE employeeNumber = v_employeeNumber) THEN
        SET v_tableName = 3;
    ELSE
        SET v_tableName = NULL;
    END IF;

    RETURN v_tableName;
END$$

DELIMITER ;
;

-- for resignEmployee function in java
-- deactivateEmployee procedure (employees and salesRepAssignments are WRITE LOCK)
-- inventory manager, sales manager, non salesrep, and salesRep tables are WRITE LOCK in java
USE `DBSALES26_G208`;
DROP procedure IF EXISTS `deactivateEmployee`;

USE `DBSALES26_G208`;
DROP procedure IF EXISTS `DBSALES26_G208`.`deactivateEmployee`;
;

DELIMITER $$
USE `DBSALES26_G208`$$
CREATE DEFINER=`DBADM_208`@`%` PROCEDURE `deactivateEmployee`(
	IN p_employeeNumber INT,
	IN p_end_username varchar(45),
	IN p_end_userreason varchar(45)
)
BEGIN
	DECLARE forLock INT;
	SELECT COUNT(1) INTO forLock FROM employees FOR UPDATE;
	DO SLEEP(15);
	UPDATE employees SET is_deactivated = 1, end_username = p_end_username, end_userreason = p_end_userreason where employeeNumber = p_employeeNumber;
    
    DELETE FROM inventory_managers WHERE employeeNumber= p_employeeNumber;
	DELETE FROM sales_managers WHERE employeeNumber= p_employeeNumber;
	DELETE FROM Non_SalesRepresentatives WHERE employeeNumber= p_employeeNumber;
    DELETE FROM salesRepresentatives WHERE employeeNumber= p_employeeNumber;
    
    SELECT COUNT(1) INTO forLock FROM salesRepAssignments FOR UPDATE;
	DO SLEEP(15);
    UPDATE salesRepAssignments SET endDate = CURDATE(), end_username = p_end_username, end_userreason = p_end_userreason
	WHERE employeeNumber = p_employeeNumber AND NOW() >= startDate AND NOW() <= endDate;
END$$

DELIMITER ;
;

-- for (insert function) in java


-- For Product Management Module
-- Stored Procedure (add_product; Write Lock)

USE `dbsalesv2.5g208`;
DROP procedure IF EXISTS `add_product`;

USE `dbsalesv2.5g208`;
DROP procedure IF EXISTS `dbsalesv2.5g208`.`add_product`;
;

DELIMITER $$
USE `dbsalesv2.5g208`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_product`(
    IN v_productCode        VARCHAR(15), 
    IN v_productName        VARCHAR(70), 
    IN v_productScale       VARCHAR(10), 
    IN v_productVendor      VARCHAR(50), 
    IN v_productDescription TEXT, 
    IN v_buyprice           DOUBLE, 
    IN v_productType        ENUM('R', 'W'), 
    IN v_quantityInStock    SMALLINT, 
    IN v_MSRP               DECIMAL(9,2),
    IN v_productLine        VARCHAR(50),
    IN v_end_username       VARCHAR(45),
    IN v_end_userreason     VARCHAR(45)
)
BEGIN
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- Lock the row to ensure it cannot be modified by other transactions
    SELECT * FROM products WHERE productCode = v_productCode FOR UPDATE;
    SELECT * FROM current_products WHERE productCode = v_productCode FOR UPDATE;
    
    -- Insert into products table
    INSERT INTO products (productCode, productName, productScale, productVendor, productDescription, buyPrice, productStatus, createdBy, createdReason) 
    VALUES (v_productCode, v_productName, v_productScale, v_productVendor, v_productDescription, v_buyprice, 'C', v_end_username, v_end_userreason);
    
    -- Insert into current_products table
    INSERT INTO current_products (productCode, productType, quantityInStock, createdBy, createdReason) 
    VALUES (v_productCode, v_productType, v_quantityInStock, v_end_username, v_end_userreason);
    
    IF (v_productType = 'R') THEN
        -- Insert into product_retail table
        INSERT INTO product_retail (productCode, createdBy, createdReason) 
        VALUES (v_productCode, v_end_username, v_end_userreason);
        
        -- Insert into product_pricing table
        INSERT INTO product_pricing (productCode, startDate, endDate, MSRP, createdBy, createdReason) 
        VALUES (v_productCode, DATE(NOW()), DATE_ADD(NOW(), INTERVAL 7 DAY), v_MSRP, v_end_username, v_end_userreason);
    ELSE
        -- Insert into product_wholesale table
        INSERT INTO product_wholesale (productCode, MSRP, createdBy, createdReason) 
        VALUES (v_productCode, v_MSRP, v_end_username, v_end_userreason);
    END IF;
    
    IF (NOT isProductLineExist(v_productLine) AND v_productLine != '') THEN
        -- Insert into productlines table
        INSERT INTO productlines (productLine, textDescription, htmlDescription, image, createdBy, createdReason) 
        VALUES (v_productLine, NULL, NULL, NULL, v_end_username, 'Added from add_products');
    END IF;
    
    -- Insert into product_productlines table
    INSERT INTO product_productlines (productCode, productLine, createdBy, createdReason) 
    VALUES (v_productCode, v_productLine, v_end_username, v_end_userreason);
    
    COMMIT;
    
    
END$$

DELIMITER ;
;

-- (Added Read Lock) getMSRP
USE `dbsalesv2.5g208`;
DROP function IF EXISTS `getMSRP`;

USE `dbsalesv2.5g208`;
DROP function IF EXISTS `dbsalesv2.5g208`.`getMSRP`;
;

DELIMITER $$
USE `dbsalesv2.5g208`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `getMSRP`(v_productCode VARCHAR(15)) RETURNS decimal(9,2)
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE v_MSRP DECIMAL(9,2);
    DECLARE currentDate DATETIME;
    DECLARE p_type ENUM('R', 'W');
    DECLARE temp_productCode VARCHAR(15); -- Used for determining if the productCode exists.
    
    -- Set currentDate to today
    SET currentDate = NOW();
    
    -- Assuming product_type can be NULL
    SELECT productCode INTO temp_productCode
    FROM current_products
    WHERE productCode = v_productCode
    LOCK IN SHARE MODE;
    
    -- Checks if productCode does not exist
    IF (temp_productCode IS NULL) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "ERROR 7001: Product Code does not exist!";
    ELSE -- If productCode exists
        -- Retrieve the product type from current_products
        SELECT product_type INTO p_type FROM current_products 
        WHERE productCode = v_productCode
        LOCK IN SHARE MODE;
        
        IF (p_type = 'R') THEN
            IF currentDate < (SELECT MIN(startdate) FROM product_pricing WHERE productCode = v_productCode LOCK IN SHARE MODE) THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ERROR 7002: Selling Price start date is set on a later date!';
            ELSEIF currentDate > (SELECT MAX(enddate) FROM product_pricing WHERE productCode = v_productCode LOCK IN SHARE MODE) THEN
                SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ERROR 7003: Selling Price has expired. Update its entry!';
            ELSE
                SELECT pp.MSRP INTO v_MSRP 
                FROM product_pricing pp 
                JOIN product_retail pr ON pp.productCode = pr.productCode
                WHERE pp.productCode = v_productCode
                LOCK IN SHARE MODE;
                RETURN v_MSRP;
            END IF;
        ELSEIF (p_type = 'W') THEN
            SELECT MSRP INTO v_MSRP 
            FROM product_wholesale
            WHERE productCode = v_productCode
            LOCK IN SHARE MODE;
            RETURN v_MSRP;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "ERROR 7004: Product Code does not have a product type assigned!";
        END IF; -- End of IF(p_type = 'R')
    END IF; -- End of (temp_productCode IS NULL)
    RETURN 1;
END$$

DELIMITER ;
;


-- (Added Read Lock) getPrice
USE `dbsalesv2.5g208`;
DROP function IF EXISTS `getPrice`;

USE `dbsalesv2.5g208`;
DROP function IF EXISTS `dbsalesv2.5g208`.`getPrice`;
;

DELIMITER $$
USE `dbsalesv2.5g208`$$
CREATE DEFINER=`root`@`localhost` FUNCTION `getPrice`(v_productCode VARCHAR(15), v_mode VARCHAR(10)) RETURNS double
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE curr_productType CHAR(1);
    DECLARE min_price DOUBLE;
    DECLARE max_price DOUBLE;
    
    SELECT product_type INTO curr_productType FROM current_products 
    WHERE productCode = v_productCode
    LOCK IN SHARE MODE;
    
    IF (curr_productType = 'W') THEN
        SELECT (MSRP*2), (MSRP*0.8) INTO max_price, min_price 
        FROM product_wholesale 
        WHERE productCode = v_productCode
        LOCK IN SHARE MODE;
    ELSEIF (curr_productType = 'R') THEN
        SELECT (MSRP*2), (MSRP*0.8) INTO max_price, min_price
        FROM product_pricing 
        WHERE productCode = v_productCode
        AND DATE(NOW()) <= endDate 
        AND DATE(NOW()) >= startDate
        LOCK IN SHARE MODE;
    END IF;
    
    IF (max_price IS NULL) OR (min_price IS NULL) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = "ERROR 01M1: Price of the product cannot be determined";
    END IF;
    
    IF (v_mode = 'MAX') THEN 
        RETURN max_price;
    ELSE
        RETURN min_price;
    END IF;
END$$

DELIMITER ;
;



-- For Sales Order Module





