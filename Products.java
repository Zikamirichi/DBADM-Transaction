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
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsalesv2.5g208?useTimezone=true&serverTimezone=UTC&user=root&password=12345");
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

            System.out.println("\nPress enter key to start creating a product record");
            sc.nextLine();

            stmt.executeUpdate();

            System.out.println("\nProduct record created successfully.");
            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            conn.commit();

            stmt.close();
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

    public int classifyProductIntoMultipleProductLines() {
        Scanner sc = new Scanner(System.in);
        Connection conn = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsalesv2.5g208?useTimezone=true&serverTimezone=UTC&user=root&password=12345");
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

            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO product_productlines (productCode, productLine, end_username, end_userreason) VALUES (?, ?, ?, ?)");
            insertStmt.setString(1, productCode);
            insertStmt.setString(2, productLine);
            insertStmt.setString(3, endUsername);
            insertStmt.setString(4, endUserReason);

            System.out.println("\nPress enter key to start classifying the product into multiple product lines");
            sc.nextLine();

            insertStmt.executeUpdate();

            System.out.println("\nProduct classified into multiple product lines successfully.");
            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            conn.commit();

            insertStmt.close();
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
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsalesv2.5g208?useTimezone=true&serverTimezone=UTC&user=root&password=12345");
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

                System.out.println("Discontinue product? (C for Continue, D for Discontinue):");
                productCategory = sc.next().toUpperCase();

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
                sc.nextLine(); // Consume the newline character

                updateStmt.executeUpdate();

                System.out.println("\nProduct updated successfully.");
                System.out.println("\nPress enter key to end transaction");
                sc.nextLine();

                conn.commit();

                updateStmt.close();
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

    public int viewProductsWithPriceRange() {
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
    
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsalesv2.5g208?useTimezone=true&serverTimezone=UTC&user=root&password=12345");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);
    
            System.out.println("Enter Product Code:");
            productCode = sc.nextLine();
    
            PreparedStatement selectStmt = conn.prepareStatement(
                "SELECT productName FROM products WHERE productCode = ? LOCK IN SHARE MODE");
            selectStmt.setString(1, productCode);
    
            ResultSet rs = selectStmt.executeQuery();
    
            if (rs.next()) {
                productName = rs.getString("productName");
    
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
                System.out.println("Minimum Price: " + minPrice);
                System.out.println("Maximum Price: " + maxPrice);
            } else {
                System.out.println("Product not found.");
            }
    
            rs.close();
            selectStmt.close();
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
                "[3] Update Product\n[4] View Products with Price Range MSRP");
        choice = sc.nextInt();
        Products p = new Products();

        if (choice == 1) p.createProduct();
        if (choice == 2) p.classifyProductIntoMultipleProductLines();
        if (choice == 3) p.updateProduct();
        if (choice == 4) p.viewProductsWithPriceRange();

        System.out.println("Press enter key to continue....");
        sc.nextLine();
        sc.nextLine(); // Consume the newline character
    }
}