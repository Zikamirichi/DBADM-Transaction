import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Employees {
    int employeeNumber;
    String lastName;
    String firstName;
    String extension;
    String email;
    String jobTitle;
    String employeeType;
    String end_username;
    String end_userreason;
    int isDeactivated;
    int isSalesRep;
    int isSalesManager;
    int isInventoryManager;

    String officeCode;
    String startDate;
    String endDate;
    String reason;
    Double quota;
    int salesManagerNumber;      
    String city;
    String country;

    int deptCode;
    String deptName;
    int deptManagerNumber;

    public Employees() {}
    
    public int createEmployee() {
        Scanner sc = new Scanner(System.in);
        Connection conn = null;

        try {
            // Establish database connection
            conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            conn.setAutoCommit(false);

            System.out.println("Enter Last Name:");
            lastName = sc.nextLine();

            System.out.println("Enter First Name:");
            firstName = sc.nextLine();

            System.out.println("Enter extension:");
            extension = sc.nextLine();

            System.out.println("Enter email:");
            email = sc.nextLine();

            System.out.println("Enter the job title:");
            jobTitle =  sc.nextLine();

            System.out.println("Enter the department code:");
            deptCode = sc.nextInt();

            System.out.println("Select Employee Type:");
            System.out.println("[1] Sales Representative");
            System.out.println("[2] Sales Manager");
            System.out.println("[3] Inventory Manager");

            boolean validChoice = false;
            int typeChoice = 0;
            while (!validChoice) {
                typeChoice = sc.nextInt();
                sc.nextLine(); 

                switch (typeChoice) {
                    case 1:
                        employeeType = "Sales Representatives";
                        validChoice = true;
                        break;
                    case 2:
                        employeeType = "Sales Manager"; 
                        validChoice = true;
                        break;
                    case 3:
                        employeeType = "Inventory Manager";
                        validChoice = true;
                        break;
                    default:
                        System.out.println("Invalid input. Please select a valid option.");
                        break;
                }
            }

            System.out.println("Enter your username:");
            end_username = sc.nextLine();

            System.out.println("Enter your reason:");
            end_userreason = sc.nextLine();

            System.out.println("\nPress enter key to start transaction");
            sc.nextLine();

            String jobTitlesQuery = "SELECT * FROM employees_jobTitles LOCK IN SHARE MODE";
            PreparedStatement pstmt = conn.prepareStatement(jobTitlesQuery);
            pstmt.executeQuery();

            String deptCodesQuery = "SELECT * FROM departments LOCK IN SHARE MODE";
            pstmt = conn.prepareStatement(deptCodesQuery);
            pstmt.executeQuery();

            //Stored procedure has lock
            PreparedStatement insertStmt = conn.prepareStatement("CALL add_employee(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            insertStmt.setString(1, lastName);
            insertStmt.setString(2, firstName);
            insertStmt.setString(3, extension);
            insertStmt.setString(4, email);
            insertStmt.setString(5, jobTitle);
            insertStmt.setString(6, employeeType);
            insertStmt.setInt(7, deptCode);
            insertStmt.setString(8, end_username);
            insertStmt.setString(9, end_userreason);

            insertStmt.executeUpdate();

            System.out.println("\nEmployee record created successfully.");

            // Commit transaction
            conn.commit();

            // Close resources
            pstmt.close();
            insertStmt.close();
            conn.close();

            return 1;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
    }


    public int reclassifyEmployee() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Choose an option:");
        System.out.println("[1] Change to Inventory Manager");
        System.out.println("[2] Change to Sales Manager");
        System.out.println("[3] Change to Sales Representative");
        int option = sc.nextInt();
        sc.nextLine(); 

        if (option == 1) {
            System.out.println("Enter Employee Number:");
            employeeNumber = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter Department Code:");
            deptCode = sc.nextInt();
            sc.nextLine();
        } else if (option == 2) {
            System.out.println("Enter Employee Number:");
            employeeNumber = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter Department Code:");
            deptCode = sc.nextInt();
            sc.nextLine();
        } else if (option == 3) {
            System.out.println("Enter Employee Number:");
            employeeNumber = sc.nextInt();
            sc.nextLine();
        } else {
            System.out.println("Invalid option");
            return 0;
        }

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            System.out.println("\nPress enter key to start transaction");
            sc.nextLine();

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM employees WHERE employeeNumber = ? FOR UPDATE");
            pstmt.setInt(1, employeeNumber);
            pstmt.executeQuery();

            CallableStatement cstmt = null;
            
            if (option == 1) {
                cstmt = conn.prepareCall("CALL employeeTypeToInventoryManagers(?, ?)");
                cstmt.setInt(1, employeeNumber);
                cstmt.setInt(2, deptCode);
            } else if (option == 2) {
                cstmt = conn.prepareCall("CALL employeeTypeToSalesManager(?, ?)");
                cstmt.setInt(1, employeeNumber);
                cstmt.setInt(2, deptCode);
            } else if (option == 3) {
                cstmt = conn.prepareCall("CALL employeeTypeToSalesRepresentative(?)");
                cstmt.setInt(1, employeeNumber);
            }

            cstmt.executeUpdate();

            pstmt.close();
            cstmt.close();
            conn.commit();
            conn.close();

            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }


    public int resignEmployee() {
        // isDeactivated function -- read lock employees table in java or db (?)
        // deactivateEmployee procedure - WRITE LOCK on employees, and salesRepAssignments
        // for delete in records, i lock in java because there are instances that the employeeNumber is not in the said table
        // i used for update in tables that should be deleted

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter Employee Number:");
        employeeNumber = sc.nextInt();
        sc.nextLine();
       
        System.out.println("Enter End Username:");
        end_username = sc.nextLine();

        System.out.println("Enter End User Reason:");
        end_userreason = sc.nextLine();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);
           
            System.out.println("\nPress enter key to start transaction");
            sc.nextLine();
            
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM employees WHERE employeeNumber = ? FOR UPDATE");
            pstmt.setInt(1, employeeNumber);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM inventory_managers WHERE employeeNumber = ? FOR UPDATE");
            pstmt.setInt(1, employeeNumber);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM sales_managers WHERE employeeNumber = ? FOR UPDATE");
            pstmt.setInt(1, employeeNumber);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM Non_SalesRepresentatives WHERE employeeNumber = ? FOR UPDATE");
            pstmt.setInt(1, employeeNumber);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM salesRepresentatives WHERE employeeNumber = ? FOR UPDATE");
            pstmt.setInt(1, employeeNumber);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM salesRepAssignments WHERE employeeNumber = ? AND NOW() >= startDate AND NOW() <= endDate FOR UPDATE");
            pstmt.setInt(1, employeeNumber);
            pstmt.executeQuery();

            CallableStatement cstmt = conn.prepareCall("CALL deactivateEmployee(?, ?, ?)");
            cstmt.setInt(1, employeeNumber);
            cstmt.setString(2, end_username);
            cstmt.setString(3, end_userreason);
            cstmt.executeUpdate();

            System.out.println("Employee record has been updated successfully.");
            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            conn.commit();

            pstmt.close();
            cstmt.close();
            conn.close();

            return 1; // Success
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return 0; 
        } finally {
            sc.close(); 
        }
        
    }

    public int createSalesRepAssignments() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Employee Number:");
        employeeNumber = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter Office Code:");
        officeCode = sc.nextLine();
        System.out.println("Enter Start Date:");
        startDate = sc.nextLine();
        System.out.println("Enter End Date:");
        endDate = sc.nextLine();
        System.out.println("Enter Reason:");
        reason = sc.nextLine();
        System.out.println("Enter Quota:");
        quota = sc.nextDouble();
        System.out.println("Enter Sales Manager Number:");
        salesManagerNumber = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter End Username: ");
        end_username = sc.nextLine();
        System.out.println("Enter End User Reason: ");
        end_userreason = sc.nextLine();


        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            System.out.println("\nPress enter key to start transaction");
            sc.nextLine();

            // lock to prevent manager from being deactivated
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM sales_managers WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, salesManagerNumber);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM employees WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, salesManagerNumber);
            ResultSet smRs = pstmt.executeQuery();

            if (smRs.next()) {
                isDeactivated = smRs.getInt("is_deactivated");
                if (isDeactivated == 1) {
                    System.out.println("Sales Manager is deactivated. Cannot assign sales rep.");
                    return 0;
                }
            }

            // lock to prevent sales rep from being deactivated
            pstmt = conn.prepareStatement("SELECT * FROM salesRepresentatives WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, employeeNumber);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM employees WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, employeeNumber);
            ResultSet empRs = pstmt.executeQuery();

            if (empRs.next()) {
                isDeactivated = empRs.getInt("is_deactivated");
                if (isDeactivated == 1) {
                    System.out.println("Sales Rep is deactivated. Cannot assign sales rep.");
                    return 0;
                }
            }

            pstmt = conn.prepareStatement("SELECT * FROM salesRepAssignments WHERE employeeNumber = ? ORDER BY endDate DESC LIMIT 1 LOCK IN SHARE MODE");
            pstmt.setInt(1, employeeNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String lastEndDate = rs.getString("endDate");
                // endDate format YYYY-MM-DD
                // startDate format YYYY-MM-DD
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                LocalDate formattedLastEndDate = LocalDate.parse(lastEndDate, formatter);
                LocalDate formattedStartDate = LocalDate.parse(startDate, formatter);

                if (lastEndDate.compareTo(startDate) > 0) {
                    // add 1 day to lastEndDate
                    formattedStartDate = formattedLastEndDate.plusDays(1);

                    startDate = formattedStartDate.format(formatter);
                }
            }

            pstmt = conn.prepareStatement("INSERT INTO salesRepAssignments(employeeNumber, officeCode, startDate, endDate, reason, quota, salesManagerNumber, end_username, end_userreason) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, employeeNumber);
            pstmt.setString(2, officeCode);
            pstmt.setString(3, startDate);
            pstmt.setString(4, endDate);
            pstmt.setString(5, reason);
            pstmt.setDouble(6, quota);
            pstmt.setInt(7, salesManagerNumber);
            pstmt.setString(8, end_username);
            pstmt.setString(9, end_userreason);

            pstmt.executeUpdate();

            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();

            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    private int getAllSalesRepAssignments() {
        Scanner sc = new Scanner(System.in);
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM salesRepAssignments s LEFT JOIN offices o ON s.officeCode = o.officeCode LOCK IN SHARE MODE");
            
            System.out.println("\nPress enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                officeCode = rs.getString("officeCode");
                startDate = rs.getString("startDate");
                endDate = rs.getString("endDate");
                reason = rs.getString("reason");
                quota = rs.getDouble("quota");
                salesManagerNumber = rs.getInt("salesManagerNumber");
                city = rs.getString("city");
                country = rs.getString("country");
                end_username = rs.getString("end_username");
                end_userreason = rs.getString("end_userreason");

                System.out.println("\nOffice Code: " + officeCode);
                System.out.println("  |- " + city + ", " + country);
                System.out.println("Start Date: " + startDate);
                System.out.println("End Date: " + endDate);
                System.out.println("Reason: " + reason);
                System.out.println("Quota: " + quota);
                System.out.println("Sales Manager Number: " + salesManagerNumber);
                System.out.println("End Username: " + end_username);
                System.out.println("End User Reason: " + end_userreason);
            }
            rs.close();

            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();

            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }        
    }

    private int getSpecificSalesRepAssignments() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Sales Rep Number:");

        employeeNumber = sc.nextInt();

        System.out.println("[1] Get Previous Assignment  [2] Get Current and New Assignment");

        int choice = sc.nextInt();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt;
            if (choice == 1) {
                pstmt = conn.prepareStatement("SELECT * FROM salesRepAssignments s LEFT JOIN offices o ON s.officeCode = o.officeCode WHERE employeeNumber = ? AND endDate < CURDATE() LOCK IN SHARE MODE");
            } else {
                pstmt = conn.prepareStatement("SELECT * FROM salesRepAssignments s LEFT JOIN offices o ON s.officeCode = o.officeCode WHERE employeeNumber = ? AND endDate >= CURDATE() LOCK IN SHARE MODE");
            }
            sc.nextLine();
            pstmt.setInt(1, employeeNumber);

            System.out.println("\nPress enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                officeCode = rs.getString("officeCode");
                startDate = rs.getString("startDate");
                endDate = rs.getString("endDate");
                reason = rs.getString("reason");
                quota = rs.getDouble("quota");
                salesManagerNumber = rs.getInt("salesManagerNumber");
                city = rs.getString("city");
                country = rs.getString("country");
                end_username = rs.getString("end_username");
                end_userreason = rs.getString("end_userreason");

                System.out.println("\nOffice Code: " + officeCode);
                System.out.println("  |- " + city + ", " + country);
                System.out.println("Start Date: " + startDate);
                System.out.println("End Date: " + endDate);
                System.out.println("Reason: " + reason);
                System.out.println("Quota: " + quota);
                System.out.println("Sales Manager Number: " + salesManagerNumber);
                System.out.println("End Username: " + end_username);
                System.out.println("End User Reason: " + end_userreason);
            }
            rs.close();

            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();

            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }

    }

    public int getSalesRepAssignmentsInfo() {
        int choice = 0;
        Scanner sc = new Scanner(System.in);

        System.out.println("\n[1] Get all records  [2] Get specific record");

        choice = sc.nextInt();

        if (choice == 1) 
            return getAllSalesRepAssignments();

        if (choice == 2)
            return getSpecificSalesRepAssignments();

        return 0;

        
        
    }

    private int getAllEmployeeRecords() {
        Scanner sc = new Scanner(System.in);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("SELECT e.*," +
                                "       IF(sr.employeeNumber IS NULL, 0, 1) AS isSalesRep," +
                                "       IF(sm.employeeNumber IS NULL, 0, 1) AS isSalesManager," +
                                "       IF(im.employeeNumber IS NULL, 0, 1) AS isInventoryManager " +
                                "FROM employees e " +
                                "LEFT JOIN salesRepresentatives sr ON e.employeeNumber = sr.employeeNumber " +
                                "LEFT JOIN Non_SalesRepresentatives ns ON e.employeeNumber = ns.employeeNumber " +
                                "LEFT JOIN sales_managers sm ON ns.employeeNumber = sm.employeeNumber " +
                                "LEFT JOIN inventory_managers im ON ns.employeeNumber = im.employeeNumber LOCK IN SHARE MODE");

            System.out.println("\nPress enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                lastName = rs.getString("lastName");
                firstName = rs.getString("firstName");
                extension = rs.getString("extension");
                email = rs.getString("email");
                jobTitle = rs.getString("jobTitle");
                employeeType = rs.getString("employee_type");
                isDeactivated = rs.getInt("is_deactivated");
                isSalesRep = rs.getInt("isSalesRep");
                isSalesManager = rs.getInt("isSalesManager");
                isInventoryManager = rs.getInt("isInventoryManager");
                end_username = rs.getString("end_username");
                end_userreason = rs.getString("end_userreason");

                System.out.println("\nLast Name: " + lastName);
                System.out.println("First Name: " + firstName);
                System.out.println("Extension: " + extension);
                System.out.println("Email: " + email);
                System.out.println("Job Title: " + jobTitle);
                System.out.println("Employee Type: " + employeeType);
                if (isSalesRep == 1) System.out.println("Current Type: Sales Representative");
                if (isSalesManager == 1) System.out.println("Current Type: Sales Manager");
                if (isInventoryManager == 1) System.out.println("Current Type: Inventory Manager");
                System.out.println("Is Deactivated: " + isDeactivated);
                System.out.println("End Username: " + end_username);
                System.out.println("End User Reason: " + end_userreason);

            }

            rs.close(); 

            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();
            return 1;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    private int getSpecificEmployeeRecord() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter Employee Number:");
            employeeNumber = sc.nextInt();    

            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
                System.out.println("Connection Successful");
                conn.setAutoCommit(false);
                
                PreparedStatement pstmt = conn.prepareStatement("SELECT e.*," +
                                "       IF(sr.employeeNumber IS NULL, 0, 1) AS isSalesRep," +
                                "       IF(sm.employeeNumber IS NULL, 0, 1) AS isSalesManager," +
                                "       IF(im.employeeNumber IS NULL, 0, 1) AS isInventoryManager " +
                                "FROM employees e " +
                                "LEFT JOIN salesRepresentatives sr ON e.employeeNumber = sr.employeeNumber " +
                                "LEFT JOIN Non_SalesRepresentatives ns ON e.employeeNumber = ns.employeeNumber " +
                                "LEFT JOIN sales_managers sm ON ns.employeeNumber = sm.employeeNumber " +
                                "LEFT JOIN inventory_managers im ON ns.employeeNumber = im.employeeNumber " +
                                "WHERE e.employeeNumber = ? LOCK IN SHARE MODE");

                pstmt.setInt(1, employeeNumber);
                
                sc.nextLine();
                System.out.println("\nPress enter key to start retrieving the data");
                sc.nextLine();
    
                ResultSet rs = pstmt.executeQuery();
    
                while (rs.next()) {
                    lastName = rs.getString("lastName");
                    firstName = rs.getString("firstName");
                    extension = rs.getString("extension");
                    email = rs.getString("email");
                    jobTitle = rs.getString("jobTitle");
                    employeeType = rs.getString("employee_type");
                    isDeactivated = rs.getInt("is_deactivated");
                    isSalesRep = rs.getInt("isSalesRep");
                    isSalesManager = rs.getInt("isSalesManager");
                    isInventoryManager = rs.getInt("isInventoryManager");
                    end_username = rs.getString("end_username");
                    end_userreason = rs.getString("end_userreason");
                }
    
                rs.close(); 
    
                System.out.println("\nLast Name: " + lastName);
                System.out.println("First Name: " + firstName);
                System.out.println("Extension: " + extension);
                System.out.println("Email: " + email);
                System.out.println("Job Title: " + jobTitle);
                System.out.println("Employee Type: " + employeeType);
                if (isSalesRep == 1) System.out.println("Current Type: Sales Representative");
                if (isSalesManager == 1) System.out.println("Current Type: Sales Manager");
                if (isInventoryManager == 1) System.out.println("Current Type: Inventory Manager");
                System.out.println("Is Deactivated: " + isDeactivated);
                System.out.println("End Username: " + end_username);
                System.out.println("End User Reason: " + end_userreason);
                
    
                System.out.println("\nPress enter key to end transaction");
                sc.nextLine();
    
                pstmt.close();
                conn.commit();
                conn.close();
                return 1;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return 0;
            }
    }

    public int getEmployeeInfo() {
        int choice = 0;

        Scanner sc = new Scanner(System.in);
        System.out.println("\n[1] Get all records  [2] Get specific record");

        choice = sc.nextInt();

        if (choice == 1) 
            return getAllEmployeeRecords();
        if (choice == 2) 
            return getSpecificEmployeeRecord();

        return 0;
    }

    public int assignDepartmentManager() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter Department Code:");
        deptCode = sc.nextInt();

        System.out.println("Enter New Department Manager Number:");
        deptManagerNumber = sc.nextInt();

        sc.nextLine();
        System.out.println("Enter End Username:");
        end_username = sc.nextLine();

        System.out.println("Enter End User Reason:");
        end_userreason = sc.nextLine();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            System.out.println("\nPress enter key to start transaction");
            sc.nextLine();

            // lock to prevent manager from being deactivated
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM employees WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, deptManagerNumber);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM Non_SalesRepresentatives WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, deptManagerNumber);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM departments WHERE deptCode = ? FOR UPDATE");
            pstmt.setInt(1, deptCode);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("UPDATE departments SET deptManagerNumber = ?, end_username = ?, end_userreason = ? WHERE deptCode = ?");
            pstmt.setInt(1, deptManagerNumber);
            pstmt.setString(2, end_username);
            pstmt.setString(3, end_userreason);
            pstmt.setInt(4, deptCode);

            pstmt.executeUpdate();

            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();

            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    private int getAllDepartmentManagerRecords() {
        Scanner sc = new Scanner(System.in);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM departments LOCK IN SHARE MODE");

            System.out.println("\nPress enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                deptCode = rs.getInt("deptCode");
                deptName = rs.getString("deptName");
                deptManagerNumber = rs.getInt("deptManagerNumber");
                end_username = rs.getString("end_username");
                end_userreason = rs.getString("end_userreason");

                System.out.println("\nDepartment Code: " + deptCode);
                System.out.println("Department Name: " + deptName);
                System.out.println("Department Manager Number: " + deptManagerNumber);
                System.out.println("End Username: " + end_username);
                System.out.println("End User Reason: " + end_userreason);
            }

            rs.close();

            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();

            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
        
    }

    private int getSpecificDepartmentManagerRecord() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter Department Code:");
        deptCode = sc.nextInt();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM departments WHERE deptCode = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, deptCode);

            sc.nextLine();
            System.out.println("\nPress enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                deptCode = rs.getInt("deptCode");
                deptName = rs.getString("deptName");
                deptManagerNumber = rs.getInt("deptManagerNumber");
                end_username = rs.getString("end_username");
                end_userreason = rs.getString("end_userreason");
            }

            System.out.println("\nDepartment Code: " + deptCode);
            System.out.println("Department Name: " + deptName);
            System.out.println("Department Manager Number: " + deptManagerNumber);
            System.out.println("End Username: " + end_username);
            System.out.println("End User Reason: " + end_userreason);

            rs.close();

            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();

            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int getDepartmentManagerInfo() {
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        System.out.println("\n[1] Get all records  [2] Get specific record");
        
        choice = sc.nextInt();

        if (choice == 1) 
            return getAllDepartmentManagerRecords();

        if (choice == 2)
            return getSpecificDepartmentManagerRecord();

        return 0;
    }

    public int updateSupervisingManager() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Employee Number:");
        employeeNumber = sc.nextInt();
        sc.nextLine();

        System.out.println("Enter Office Code:");
        officeCode = sc.nextLine();

        System.out.println("Enter Start Date:");
        startDate = sc.nextLine();

        System.out.println("Enter New Sales Manager Number:");
        salesManagerNumber = sc.nextInt();

        sc.nextLine();
        System.out.println("Enter End Username:");
        end_username = sc.nextLine();

        System.out.println("Enter End User Reason:");
        end_userreason = sc.nextLine();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);
           
            System.out.println("\nPress enter key to start transaction");
            sc.nextLine();

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM employees WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, salesManagerNumber);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM sales_managers WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, salesManagerNumber);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM salesRepAssignments WHERE employeeNumber = ? AND officeCode = ? AND startDate = ? FOR UPDATE");
            pstmt.setInt(1, employeeNumber);
            pstmt.setString(2, officeCode);
            pstmt.setString(3, startDate);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("UPDATE salesRepAssignments SET salesManagerNumber = ?, end_username = ?, end_userreason = ? WHERE employeeNumber = ? AND officeCode = ? AND startDate = ? ");
            pstmt.setInt(1, salesManagerNumber);
            pstmt.setString(2, end_username);
            pstmt.setString(3, end_userreason);
            pstmt.setInt(4, employeeNumber);
            pstmt.setString(5, officeCode);
            pstmt.setString(6, startDate);
            pstmt.executeUpdate();

            System.out.println("Employee record has been updated successfully.");
            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();

            return 1; // Success
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return 0; 
        } finally {
            sc.close(); 
        }
    }

    public int getOfficeInfo() {
        Scanner sc = new Scanner(System.in);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM offices LOCK IN SHARE MODE");
            
            System.out.println("\nPress enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                officeCode = rs.getString("officeCode");
                city = rs.getString("city");
                country = rs.getString("country");

                System.out.println("\nOffice Code: " + officeCode);
                System.out.println("City: " + city);
                System.out.println("Country: " + country);
            }

            rs.close();

            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();

            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public static void main (String args[]) {
        Scanner sc     = new Scanner (System.in);
        int     choice = 0;
        // Letting the use choose between the two functions
        System.out.println("\nEnter the number of your choice:\n[1] Create Employee\n[2] Reclassify Employee\n" +
        "[3] Resign Employee\n[4] Get Employee Info\n" + 
        "[5] Create Sales Rep Assignments\n[6] Get Sales Rep Assignments Info\n"+
        "[7] Assign Department Manager\n[8] Get Department Manager Info\n" +
        "[9] Update Supervising Manager \n[10] Get Office Info");
        choice = sc.nextInt();
        Employees e = new Employees();
        if (choice==1) e.createEmployee();
        if (choice==2) e.reclassifyEmployee();
        if (choice==3) e.resignEmployee();
        if (choice==4) e.getEmployeeInfo();
        if (choice==5) e.createSalesRepAssignments();
        if (choice==6) e.getSalesRepAssignmentsInfo();
        if (choice==7) e.assignDepartmentManager();
        if (choice==8) e.getDepartmentManagerInfo();
        if (choice==9) e.updateSupervisingManager();
        if (choice==10) e.getOfficeInfo();

        
        System.out.println("Press enter key to continue....");
        sc.nextLine();
    }
    
}