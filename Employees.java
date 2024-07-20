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
    int isDeactivated;
    String officeCode;
    String startDate;
    String endDate;
    String reason;
    Double quota;
    int salesManagerNumber;      
    String city;
    String country;

    public Employees() {}
    
    public int createEmployee() {
        return 1;
    }
    public int reclassifyEmployee() {
        return 1;
    }

    public int resignEmployee() {
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

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.6?useTimezone=true&serverTimezone=UTC&user=root&password=root");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO salesRepAssignments(employeeNumber, officeCode, startDate, endDate, reason, quota, salesManagerNumber) VALUES (?, ?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, employeeNumber);
            pstmt.setString(2, officeCode);
            pstmt.setString(3, startDate);
            pstmt.setString(4, endDate);
            pstmt.setString(5, reason);
            pstmt.setDouble(6, quota);
            pstmt.setInt(7, salesManagerNumber);

            sc.nextLine();
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

                System.out.println("\nOffice Code: " + officeCode);
                System.out.println("  |- " + city + ", " + country);
                System.out.println("Start Date: " + startDate);
                System.out.println("End Date: " + endDate);
                System.out.println("Reason: " + reason);
                System.out.println("Quota: " + quota);
                System.out.println("Sales Manager Number: " + salesManagerNumber);
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

                System.out.println("\nOffice Code: " + officeCode);
                System.out.println("  |- " + city + ", " + country);
                System.out.println("Start Date: " + startDate);
                System.out.println("End Date: " + endDate);
                System.out.println("Reason: " + reason);
                System.out.println("Quota: " + quota);
                System.out.println("Sales Manager Number: " + salesManagerNumber);
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

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM employees LOCK IN SHARE MODE");

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

                System.out.println("\nLast Name: " + lastName);
                System.out.println("First Name: " + firstName);
                System.out.println("Extension: " + extension);
                System.out.println("Email: " + email);
                System.out.println("Job Title: " + jobTitle);
                System.out.println("Employee Type: " + employeeType);
                System.out.println("Is Deactivated: " + isDeactivated);

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
    
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM employees WHERE employeeNumber = ? LOCK IN SHARE MODE");
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
                }
    
                rs.close(); 
    
                System.out.println("\nLast Name: " + lastName);
                System.out.println("First Name: " + firstName);
                System.out.println("Extension: " + extension);
                System.out.println("Email: " + email);
                System.out.println("Job Title: " + jobTitle);
                System.out.println("Employee Type: " + employeeType);
                System.out.println("Is Deactivated: " + isDeactivated);
    
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
    
    public static void main (String args[]) {
        Scanner sc     = new Scanner (System.in);
        int     choice = 0;
        // Letting the use choose between the two functions
        System.out.println("Enter [1] Create Employee  [2] Reclassify Employee \n" +
        "[3] Resign Employee  [4] Create Sales Rep Assignments \n" + 
        "[5] Get Sales Rep Assignments Info  [6] Get Employee Info: ");
        choice = sc.nextInt();
        Employees e = new Employees();
        if (choice==1) e.createEmployee();
        if (choice==2) e.reclassifyEmployee();
        if (choice==3) e.resignEmployee();
        if (choice==4) e.createSalesRepAssignments();
        if (choice==5) e.getSalesRepAssignmentsInfo();
        if (choice==6) e.getEmployeeInfo();
        
        System.out.println("Press enter key to continue....");
        sc.nextLine();
    }
    
}