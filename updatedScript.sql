-- For Employees Module

-- createEmployee function in java
USE `dbsalesv2.6`;
DROP procedure IF EXISTS `add_employee`;

USE `dbsalesv2.6`;
DROP procedure IF EXISTS `dbsalesv2.6`.`add_employee`;
;

DELIMITER $$
USE `dbsalesv2.6`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_employee`(
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
    DECLARE dummy INT;
	
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
		RESIGNAL;
	END;
	
	START TRANSACTION;

		IF p_employee_Type = 'Sales Representatives'  THEN
			SET employee_type := 'S';
		ELSEIF p_employee_Type = 'Inventory Manager' OR p_employee_Type = 'Sales Manager' THEN
			SET employee_type := 'N';
		ELSE
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'ERROR 80A2: Invalid employee type';
		END IF;
		
	   
		INSERT INTO employees (lastName, firstName, extension, email, jobTitle, employee_type, end_username, end_userreason)
		VALUES (p_lastName, p_firstName, p_extension, p_email, p_jobTitle, employee_type, p_end_username, p_end_userreason);
		
        SELECT COUNT(1) INTO dummy FROM employees FOR UPDATE;
		DO SLEEP(15);
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
        
	COMMIT;
END$$

DELIMITER ;
;

