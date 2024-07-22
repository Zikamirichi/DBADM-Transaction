import java.sql.*;
import java.util.*;

public class Products {
    String productCode;
    String productName;
    String productScale;
    String productVendor;
    String productDescription;
    int quantityInStock;
    double buyPrice;
    double MSRP;
    String productType;
    String productLine;
    String endUsername;
    String endUserReason;
    String productCategory; // 'C' for Continued Product, 'D' for Discontinued Product

    public Products() {}

    public int createProduct() {
        Scanner sc = new Scanner(System.in);
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            System.out.println("Enter Product Code:");
            productCode = sc.nextLine();

            System.out.println("Enter Product Name:");
            productName = sc.nextLine();

            System.out.println("Enter Product Scale:");
            productScale = sc.nextLine();

            System.out.println("Enter Product Vendor:");
            productVendor = sc.nextLine();

            System.out.println("Enter Product Description:");
            productDescription = sc.nextLine();

            System.out.println("Enter Buy Price:");
            buyPrice = sc.nextDouble();

            System.out.println("Enter Product Type (R for Retail, W for Wholesale):");
            productType = sc.next().toUpperCase();

            System.out.println("Enter Quantity In Stock:");
            quantityInStock = sc.nextInt();

            System.out.println("Enter MSRP:");
            MSRP = sc.nextDouble();

            sc.nextLine(); // Consume the newline character

            System.out.println("Enter Product Line:");
            productLine = sc.nextLine();

            System.out.println("Enter End Username:");
            endUsername = sc.nextLine();

            System.out.println("Enter End User Reason:");
            endUserReason = sc.nextLine();

            CallableStatement stmt = conn.prepareCall("{CALL add_product(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            stmt.setString(1, productCode);
            stmt.setString(2, productName);
            stmt.setString(3, productScale);
            stmt.setString(4, productVendor);
            stmt.setString(5, productDescription);
            stmt.setDouble(6, buyPrice);
            stmt.setString(7, productType);
            stmt.setInt(8, quantityInStock);
            stmt.setDouble(9, MSRP);
            stmt.setString(10, productLine);
            stmt.setString(11, endUsername);
            stmt.setString(12, endUserReason);

            stmt.executeUpdate();

            System.out.println("\nProduct record created successfully.");

            stmt.close();
            conn.close();

            return 1;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
    }

    public int classifyProductIntoMultipleProductLines() {
        Scanner sc = new Scanner(System.in);
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            System.out.println("Enter Product Code:");
            productCode = sc.nextLine();

            System.out.println("Enter New Product Line:");
            productLine = sc.nextLine();

            System.out.println("Enter End Username:");
            endUsername = sc.nextLine();

            System.out.println("Enter End User Reason:");
            endUserReason = sc.nextLine();

            PreparedStatement lockProductStmt = conn.prepareStatement("SELECT * FROM products WHERE productCode = ? LOCK IN SHARE MODE"); 
            lockProductStmt.setString(1, productCode);

            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO product_productlines (productCode, productLine, end_username, end_userreason) VALUES (?, ?, ?, ?)");
            insertStmt.setString(1, productCode);
            insertStmt.setString(2, productLine);
            insertStmt.setString(3, endUsername);
            insertStmt.setString(4, endUserReason);

            System.out.println("\nPress enter key to start classifying the product into multiple product lines");
            sc.nextLine();

            lockProductStmt.executeQuery();
            insertStmt.executeUpdate();

            System.out.println("\nProduct classified into multiple product lines successfully.");
            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            conn.commit();

            insertStmt.close();
            lockProductStmt.close();
            conn.close();

            return 1;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.out.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }

            return 0;
        }
    }

    public int updateProduct() {
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
    
        try {
            conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);
    
            System.out.println("Enter Product Code:");
            productCode = sc.nextLine();
    
            PreparedStatement selectStmt = conn.prepareStatement(
                "SELECT * FROM products WHERE productCode = ? FOR UPDATE");
            selectStmt.setString(1, productCode);
    
            ResultSet rs = selectStmt.executeQuery();
    
            if (rs.next()) {
                productName = rs.getString("productName");
                productVendor = rs.getString("productVendor");
                productDescription = rs.getString("productDescription");
                buyPrice = rs.getDouble("buyPrice");
                productCategory = rs.getString("product_category");
    
                System.out.println("Product Details:\n");
                System.out.println("Product Name: " + productName);
                System.out.println("Product Vendor: " + productVendor);
                System.out.println("Product Description: " + productDescription);
                System.out.println("Buy Price: " + buyPrice);
                System.out.println("Product Category: " + productCategory);
                System.out.println("\nEnter new details for the product:");
    
                System.out.println("Enter Product Name:");
                productName = sc.nextLine();
    
                System.out.println("Enter Product Vendor:");
                productVendor = sc.nextLine();
    
                System.out.println("Enter Product Description:");
                productDescription = sc.nextLine();
    
                System.out.println("Enter Buy Price:");
                buyPrice = sc.nextDouble();
                sc.nextLine(); // Consume the newline character
    
                PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE products SET productName = ?, productVendor = ?, productDescription = ?, buyPrice = ?, product_category = ? WHERE productCode = ?");
                updateStmt.setString(1, productName);
                updateStmt.setString(2, productVendor);
                updateStmt.setString(3, productDescription);
                updateStmt.setDouble(4, buyPrice);
                updateStmt.setString(5, productCategory);
                updateStmt.setString(6, productCode);
    
                System.out.println("\nPress enter key to start updating the product");
                sc.nextLine();
    
                updateStmt.executeUpdate();
                updateStmt.close();
    
                System.out.println("\nProduct updated successfully.");
    
                conn.commit();
            } else {
                System.out.println("Product not found.");
            }
    
            rs.close();
            selectStmt.close();
            conn.close();
    
            return 1;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
    
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.out.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
    
            return 0;
        }
    }

    public int discontinueOrReintroduceProduct() {
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
    
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsalesv2.5g208?useTimezone=true&serverTimezone=UTC&user=root&password=12345");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);
    
            System.out.println("Enter Product Code:");
            productCode = sc.nextLine();
    
            System.out.println("Discontinue or Reintroduce product? (D for Discontinue, C for Continue):");
            productCategory = sc.next().toUpperCase();
            sc.nextLine(); // Consume the newline character
    
            if ("D".equals(productCategory)) {
                System.out.println("Enter Inventory Manager ID:");
                int inventoryManagerId = sc.nextInt();
                sc.nextLine(); // Consume the newline character
    
                // Check if the inventory manager exists in the inventory_managers table
                PreparedStatement checkManagerStmt = conn.prepareStatement(
                    "SELECT 1 FROM inventory_managers WHERE employeeNumber = ?");
                checkManagerStmt.setInt(1, inventoryManagerId);
    
                ResultSet managerRs = checkManagerStmt.executeQuery();
                if (!managerRs.next()) {
                    System.out.println("Error: Inventory Manager ID not found in inventory_managers. Please ensure the ID is correct.");
                    managerRs.close();
                    checkManagerStmt.close();
                    conn.rollback();
                    return 0;
                }
                managerRs.close();
                checkManagerStmt.close();
    
                System.out.println("Enter reason for discontinuation:");
                String reason = sc.nextLine();
    
                System.out.println("Enter your username:");
                String endUsername = sc.nextLine();
    
                System.out.println("Enter reason for update:");
                String endUserReason = sc.nextLine();
    
                CallableStatement discontinueStmt = conn.prepareCall("{CALL discontinue_product(?, ?, ?, ?, ?)}");
                discontinueStmt.setString(1, productCode);
                discontinueStmt.setInt(2, inventoryManagerId);
                discontinueStmt.setString(3, reason);
                discontinueStmt.setString(4, endUsername);
                discontinueStmt.setString(5, endUserReason);
    
                System.out.println("\nPress enter key to discontinue the product");
                sc.nextLine();
    
                discontinueStmt.executeUpdate();
                discontinueStmt.close();
    
                System.out.println("\nProduct discontinued successfully.");
            } else if ("C".equals(productCategory)) {
                System.out.println("Enter your username:");
                String endUsername = sc.nextLine();
    
                System.out.println("Enter reason for update:");
                String endUserReason = sc.nextLine();
    
                CallableStatement reintroduceStmt = conn.prepareCall("{CALL reintroduce_product(?, ?, ?)}");
                reintroduceStmt.setString(1, productCode);
                reintroduceStmt.setString(2, endUsername);
                reintroduceStmt.setString(3, endUserReason);
    
                System.out.println("\nPress enter key to reintroduce the product");
                sc.nextLine();
    
                reintroduceStmt.executeUpdate();
                reintroduceStmt.close();
    
                System.out.println("\nProduct reintroduced successfully.");
            } else {
                System.out.println("Invalid choice. Please enter 'D' for Discontinue or 'C' for Continue.");
            }
    
            conn.commit();
            conn.close();
    
            return 1;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
    
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.out.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
    
            return 0;
        }
    }

    public int viewProductsWithPriceRange() {
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
    
        try {
            conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);
    
            System.out.println("Enter Product Code:");
            productCode = sc.nextLine();
            
            System.out.println("\nPress enter key to view the product with its MSRP price range");
            sc.nextLine();
    
            PreparedStatement selectStmt = conn.prepareStatement(
                "SELECT p.productName, cp.product_type FROM products p JOIN current_products cp ON p.productCode = cp.productCode WHERE p.productCode = ? LOCK IN SHARE MODE");
            selectStmt.setString(1, productCode);
    
            ResultSet rs = selectStmt.executeQuery();
    
            if (rs.next()) {
                productName = rs.getString("productName");
                productType = rs.getString("product_type");
    
                CallableStatement msrpStmt = conn.prepareCall("{? = CALL getMSRP(?)}");
                msrpStmt.registerOutParameter(1, Types.DECIMAL);
                msrpStmt.setString(2, productCode);
                msrpStmt.execute();
    
                double msrp = msrpStmt.getDouble(1);
    
                CallableStatement minPriceStmt = conn.prepareCall("{? = CALL getPrice(?, 'MIN')}");
                CallableStatement maxPriceStmt = conn.prepareCall("{? = CALL getPrice(?, 'MAX')}");
    
                minPriceStmt.registerOutParameter(1, Types.DOUBLE);
                minPriceStmt.setString(2, productCode);
                minPriceStmt.execute();
    
                maxPriceStmt.registerOutParameter(1, Types.DOUBLE);
                maxPriceStmt.setString(2, productCode);
                maxPriceStmt.execute();
    
                double minPrice = minPriceStmt.getDouble(1);
                double maxPrice = maxPriceStmt.getDouble(1);
    
                System.out.println("\nProduct Name: " + productName);
                System.out.println("Product Type: " + (productType.equals("R") ? "Retail" : "Wholesale"));
                System.out.println("MSRP: " + msrp);
                System.out.println("Minimum Price: " + minPrice);
                System.out.println("Maximum Price: " + maxPrice);
    
                minPriceStmt.close();
                maxPriceStmt.close();
                msrpStmt.close();
            } else {
                System.out.println("Product not found.");
            }
    
            rs.close();
            selectStmt.close();

            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();
    
            conn.commit();
            conn.close();
    
            return 1;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
    }

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        int choice = 0;

        System.out.println("Enter the number of your choice:\n[1] Create Product\n[2] Classify Product Into Multiple Product Lines\n" +
                "[3] Update Product\n[4] View a Product with its MSRP Price Range\n[5] Discontinue or Reintroduce Product");
        choice = sc.nextInt();
        Products p = new Products();

        if (choice == 1) p.createProduct();
        if (choice == 2) p.classifyProductIntoMultipleProductLines();
        if (choice == 3) p.updateProduct();
        if (choice == 4) p.viewProductsWithPriceRange();
        if (choice == 5) p.discontinueOrReintroduceProduct();

        System.out.println("Press enter key to continue....");
        sc.nextLine();
        sc.nextLine(); // Consume the newline character
    }
}
