import java.sql.*;
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
        System.out.println("Enter Last Name:");
        lastName = sc.nextLine();
        sc.nextLine();

        System.out.println("Enter First Name:");
        firstName = sc.nextLine();

        System.out.println("Enter extension:");
        extension = sc.nextLine();

        System.out.println("Enter email:");
        email = sc.nextLine();

        System.out.println("Select a Job Title:");
        // make a multiple choice
        // get from employees_jobtitles
        jobTitle = sc.nextLine();
        System.out.println("Enter employee type:");
        // choose from S or N
        employeeType = sc.nextLine();
        System.out.println("Enter department code:");
        employeeType = sc.nextLine();
        System.out.println("Enter your username:");
        employeeType = sc.nextLine();
        System.out.println("Enter your reason:");
        employeeType = sc.nextLine();
        

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.6?useTimezone=true&serverTimezone=UTC&user=root&password=root");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("CALL add_employee(?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, lastName);
            pstmt.setString(2, firstName);
            pstmt.setString(3, extension);
            pstmt.setString(4, email);
            pstmt.setString(5, jobTitle);
            pstmt.setString(6, employeeType);
            pstmt.setInt(7, deptCode);
            pstmt.setString(8, end_username);
            pstmt.setString(9, end_userreason);

            sc.nextLine();
            System.out.println("\nPress enter key to start creating an employee record");
            sc.nextLine();

            pstmt.executeUpdate();

            System.out.println("\nEmployee record created successfully.");
            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            pstmt.close();
            conn.commit();
            conn.close();

            return 1;
        } catch (Exception e) {
            System.out.println("Error creating employee: " + e.getMessage());
            return 0;
        }
    }


    public int reclassifyEmployee() {
        // getEmployeeType (add read lock)
        // check ReassignSalesRep1
        return 1;
    }

    public int resignEmployee() {
        // getEmployeeEmployeeNum (Read Lock)
        //deactivateEmployee procedure - WRITE LOCK

        return 1;
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
        System.out.println("Enter End Username: ");
        end_username = sc.nextLine();
        System.out.println("Enter End User Reason: ");
        end_userreason = sc.nextLine();


        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.6?useTimezone=true&serverTimezone=UTC&user=root&password=root");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            // lock to prevent manager from being deactivated
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM sales_managers WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, salesManagerNumber);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM employees WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, salesManagerNumber);
            pstmt.executeQuery();

            // lock to prevent sales rep from being deactivated
            pstmt = conn.prepareStatement("SELECT * FROM salesRepresentatives WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, employeeNumber);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM employees WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, employeeNumber);
            pstmt.executeQuery();

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

            System.out.println("\nPress enter key to start transaction");
            sc.nextLine();

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
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.6?useTimezone=true&serverTimezone=UTC&user=root&password=root");
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
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.6?useTimezone=true&serverTimezone=UTC&user=root&password=root");
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
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.6?useTimezone=true&serverTimezone=UTC&user=root&password=root");
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
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.6?useTimezone=true&serverTimezone=UTC&user=root&password=root");
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

        System.out.println("Enter End Username:");
        end_username = sc.nextLine();

        System.out.println("Enter End User Reason:");
        end_userreason = sc.nextLine();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.6?useTimezone=true&serverTimezone=UTC&user=root&password=root");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

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

            System.out.println("\nPress enter key to start transaction");
            sc.nextLine();

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
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.6?useTimezone=true&serverTimezone=UTC&user=root&password=root");
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
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.6?useTimezone=true&serverTimezone=UTC&user=root&password=root");
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

    public static void main (String args[]) {
        Scanner sc     = new Scanner (System.in);
        int     choice = 0;
        // Letting the use choose between the two functions
        System.out.println("Enter [1] Create Employee  [2] Reclassify Employee \n" +
        "[3] Resign Employee  [4] Create Sales Rep Assignments \n" + 
        "[5] Get Sales Rep Assignments Info  [6] Get Employee Info \n"+
        "[7] Assign Department Manager [8] Get Department Manager Info\n" +
        "[9] Update Supervising Manager");
        choice = sc.nextInt();
        Employees e = new Employees();
        if (choice==1) e.createEmployee();
        if (choice==2) e.reclassifyEmployee();
        if (choice==3) e.resignEmployee();
        if (choice==4) e.createSalesRepAssignments();
        if (choice==5) e.getSalesRepAssignmentsInfo();
        if (choice==6) e.getEmployeeInfo();
        if (choice==7) e.assignDepartmentManager();
        if (choice==8) e.getDepartmentManagerInfo();

        
        System.out.println("Press enter key to continue....");
        sc.nextLine();
    }
    
}