import java.sql.*;
import java.util.*;

public class Employees {
    String employeeNumber;
    String lastName;
    String firstName;
    String extension;
    String email;
    String jobTitle;
    String employeeType;  
    String officeCode;
    String startDate;
    String endDate;
    String reason;
    Double quota;
    int salesManagerNumber;      

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
        return 1;
    }

    public int getSalesRepAssignmentsInfo() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Sales Rep Number:");
        employeeNumber = sc.nextLine();

        // TODO determine get previous assignment or newly?

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.5g208?useTimezone=true&serverTimezone=UTC&user=root&password=root");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM salesRepAssignments WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, Integer.parseInt(employeeNumber));

            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                officeCode = rs.getString("officeCode");
                startDate = rs.getString("startDate");
                endDate = rs.getString("endDate");
                reason = rs.getString("reason");
                quota = rs.getDouble("quota");
                salesManagerNumber = rs.getInt("salesManagerNumber");
            }

            System.out.println("Office Code: " + officeCode);
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);
            System.out.println("Reason: " + reason);
            System.out.println("Quota: " + quota);
            System.out.println("Sales Manager Number: " + salesManagerNumber);

            rs.close();

            

            System.out.println("Press enter key to end transaction");
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
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Employee Number:");
        employeeNumber = sc.nextLine();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.5g208?useTimezone=true&serverTimezone=UTC&user=root&password=root");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM employees WHERE employeeNumber = ? LOCK IN SHARE MODE");
            pstmt.setInt(1, Integer.parseInt(employeeNumber));

            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                lastName = rs.getString("lastName");
                firstName = rs.getString("firstName");
                extension = rs.getString("extension");
                email = rs.getString("email");
                jobTitle = rs.getString("jobTitle");
                employeeType = rs.getString("employee_type");
            }

            rs.close(); 

            System.out.println("Last Name: " + lastName);
            System.out.println("First Name: " + firstName);
            System.out.println("Extension: " + extension);
            System.out.println("Email: " + email);
            System.out.println("Job Title: " + jobTitle);
            System.out.println("Employee Type: " + employeeType);

            System.out.println("Press enter key to end transaction");
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