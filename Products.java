import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Products {
    int productId;
    String productName;
    double msrp;
    String category;
    String status;
    String productLine;

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public Products() {}

    public int createProduct() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Product Name:");
        productName = sc.nextLine();

        System.out.println("Enter MSRP:");
        msrp = sc.nextDouble();
        sc.nextLine();  // Consume newline

        System.out.println("Enter Category:");
        category = sc.nextLine();

        rwLock.writeLock().lock();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsalesv2.5g203?useTimezone=true&serverTimezone=UTC&user=root&password=12345")) {
            System.out.println("Connection Successful");
            String query = "INSERT INTO products (productName, MSRP, category, status) VALUES (?, ?, ?, 'Current')";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, productName);
                pstmt.setDouble(2, msrp);
                pstmt.setString(3, category);
                pstmt.executeUpdate();
                System.out.println("Product created successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            rwLock.writeLock().unlock();
        }
        return 1;
    }

    public int classifyProduct() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Product ID:");
        productId = sc.nextInt();
        sc.nextLine();  // Consume newline

        System.out.println("Enter Product Lines (comma separated):");
        String lines = sc.nextLine();
        List<String> productLines = Arrays.asList(lines.split(","));

        rwLock.writeLock().lock();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsalesv2.5g203?useTimezone=true&serverTimezone=UTC&user=root&password=12345")) {
            System.out.println("Connection Successful");
            String query = "INSERT INTO product_lines (productId, productLine) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                for (String line : productLines) {
                    pstmt.setInt(1, productId);
                    pstmt.setString(2, line.trim());
                    pstmt.executeUpdate();
                }
                System.out.println("Product classified into multiple lines successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            rwLock.writeLock().unlock();
        }
        return 1;
    }

    public int updateProductStatus() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Product ID:");
        productId = sc.nextInt();
        sc.nextLine();  // Consume newline

        System.out.println("Discontinue Product? (true/false):");
        boolean discontinue = sc.nextBoolean();

        rwLock.writeLock().lock();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsalesv2.5g203?useTimezone=true&serverTimezone=UTC&user=root&password=12345")) {
            System.out.println("Connection Successful");
            String query = "UPDATE products SET status = ? WHERE productId = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, discontinue ? "Discontinued" : "Current");
                pstmt.setInt(2, productId);
                pstmt.executeUpdate();
                System.out.println("Product status updated successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            rwLock.writeLock().unlock();
        }
        return 1;
    }

    public int viewProductsInPriceRange() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Minimum MSRP:");
        double minPrice = sc.nextDouble();
        System.out.println("Enter Maximum MSRP:");
        double maxPrice = sc.nextDouble();

        rwLock.readLock().lock();
        List<String> products = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbsalesv2.5g203?useTimezone=true&serverTimezone=UTC&user=root&password=12345")) {
            System.out.println("Connection Successful");
            String query = "SELECT productName FROM products WHERE MSRP BETWEEN ? AND ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setDouble(1, minPrice);
                pstmt.setDouble(2, maxPrice);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        products.add(rs.getString("productName"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            rwLock.readLock().unlock();
        }

        System.out.println("Products in MSRP Range:");
        for (String product : products) {
            System.out.println(product);
        }
        return 1;
    }

    public static void main(String[] args) {
        Products pm = new Products();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nProduct Management Menu");
            System.out.println("1. Create a Product");
            System.out.println("2. Classify Product into Multiple Product Lines");
            System.out.println("3. Update Product Status (Discontinue or Continue)");
            System.out.println("4. View Products with MSRP Range");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    pm.createProduct();
                    break;
                case 2:
                    pm.classifyProduct();
                    break;
                case 3:
                    pm.updateProductStatus();
                    break;
                case 4:
                    pm.viewProductsInPriceRange();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
