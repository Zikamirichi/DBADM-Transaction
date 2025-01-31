import java.sql.*;
import java.util.*;

/**
 * (Sales module) -- CARLOS
 * Create an order
 * Update order products
 * Update order itself (cancel ship eme)
 * Record products ordered
 * View product together with allowable pricing /
 * View entire order /
 */

public class Sales {
    String orderNumber;
    String orderDate;
    String requiredDate;
    String shippedDate;
    String status;
    String comments;
    int customerNumber;
    String end_username;
    String end_userreason;

    /*
    ArrayList<String> productCode = new ArrayList<>();
    ArrayList<Integer> quantityOrdered = new ArrayList<>();
    ArrayList<Double> priceEach = new ArrayList<>();
    ArrayList<Integer> orderLineNumber = new ArrayList<>();
    ArrayList<Integer> referenceNo = new ArrayList<>();

    ArrayList<String> productName = new ArrayList<>();*/

    String productCode;
    int quantityOrdered;
    double priceEach;
    int orderLineNumber;
    int referenceNo;

    String productName;


    public Sales() {}

    public Sales(String productCode, int quantityOrdered, double priceEach, int orderLineNumber, int referenceNo,
                 String productName, String end_username, String end_userreason) {
        this.productCode = productCode;
        this.quantityOrdered = quantityOrdered;
        this.priceEach = priceEach;
        this.orderLineNumber = orderLineNumber;
        this.referenceNo = referenceNo;
        this.productName = productName;
        this.end_username = end_username;
        this.end_userreason = end_userreason;

    }

    public int createOrder() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Required Date:");
        requiredDate = sc.nextLine();

        System.out.println("Enter Comments:");
        comments = sc.nextLine();

        System.out.println("Enter Customer Number:");
        customerNumber = sc.nextInt();

        sc.nextLine();

        System.out.println("Enter End Username: ");
        end_username = sc.nextLine();

        System.out.println("Enter End User Reason: ");
        end_userreason = sc.nextLine();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            System.out.println("Press enter key to start creating the data");
            sc.nextLine();

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM orders_audit FOR UPDATE");
            pstmt.executeQuery();



            pstmt = conn.prepareStatement(
                    "INSERT INTO orders (`requiredDate`, `comments`, `customerNumber`, end_username, end_userreason) " +
                            "VALUES (?, ?, ?, ?, ?)");

            pstmt.setString(1, requiredDate);
            pstmt.setString(2, comments);
            pstmt.setInt(3, customerNumber);
            pstmt.setString(4, end_username);
            pstmt.setString(5, end_userreason);

            pstmt.executeUpdate();


            System.out.println("Order created");

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
    public int recordProductsOrdered() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter Order Number:");
        orderNumber = sc.nextLine();

        System.out.println("Enter Product Code:");
        productCode = sc.nextLine();

        System.out.println("Enter Quantity Ordered:");
        quantityOrdered = sc.nextInt();
        sc.nextLine();

        System.out.println("Enter Price Each:");
        priceEach = sc.nextDouble();
        sc.nextLine();

        System.out.println("Enter End Username: ");
        end_username = sc.nextLine();

        System.out.println("Enter End User Reason: ");
        end_userreason = sc.nextLine();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            System.out.println("Press enter key to start transaction");
            sc.nextLine();

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM current_products WHERE productCode=? FOR UPDATE");
            pstmt.setString(1, productCode);
            pstmt.executeQuery();

            pstmt = conn.prepareStatement("SELECT * FROM orders WHERE orderNumber=? LOCK IN SHARE MODE");
            pstmt.setInt(1, Integer.parseInt(orderNumber));
            pstmt.executeQuery();

            pstmt = conn.prepareStatement(
                    "INSERT INTO orderdetails (`orderNumber`, `productCode`, `quantityOrdered`, `priceEach`, `end_username`, `end_userreason`) " +
                            "VALUES (?, ?, ?, ?, ?, ?)");

            pstmt.setInt(1, Integer.parseInt(orderNumber));
            pstmt.setString(2, productCode);
            pstmt.setInt(3, quantityOrdered);
            pstmt.setDouble(4, priceEach);
            pstmt.setString(5, end_username);
            pstmt.setString(6, end_userreason);

            pstmt.executeUpdate();


            System.out.println("Ordered Product created");

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

    public int updateOrder() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Order Number:");
        orderNumber = sc.nextLine();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT * FROM orders " +
                            "WHERE orderNumber=? FOR UPDATE ");

            pstmt.setInt(1, Integer.parseInt(orderNumber));

            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();


            while (rs.next()) {
                orderDate = rs.getString("orderDate");
                requiredDate = rs.getString("requiredDate");
                shippedDate = rs.getString("shippedDate");
                status = rs.getString("status");
                comments = rs.getString("comments");
                customerNumber = rs.getInt("customerNumber");
                end_username = rs.getString("end_username");
                end_userreason = rs.getString("end_userreason");

            }
            rs.close();

            System.out.println("Update Order\n");
            System.out.println("Order Date: " + orderDate);
            System.out.println("Required Date: " + requiredDate);
            System.out.println("Shipped Date: " + shippedDate);
            System.out.println("status: " + status);
            System.out.println("Comments: " + comments);
            System.out.println("Customer Number: " + customerNumber);
            System.out.println("End Username: " + end_username);
            System.out.println("End User Reason: " + end_userreason);

            System.out.println("Press enter key to enter new values for order");
            sc.nextLine();

            System.out.println("Enter Required Date:");
            requiredDate = sc.nextLine();

            boolean isValid = false;
            do {
                System.out.println("Enter status: ");
                status = sc.nextLine();
                if (Objects.equals(status, "In Process") || Objects.equals(status, "Shipped") ||
                        Objects.equals(status, "Cancelled") || Objects.equals(status, "Disputed") ||
                        Objects.equals(status, "Resolved") || Objects.equals(status, "Completed"))
                    isValid = true;

            } while (!isValid);

            if (Objects.equals(status, "Shipped")) {
                System.out.println("Enter Shipped Date: ");
                shippedDate = sc.nextLine();
            } else if (Objects.equals(status, "In Process")){
                shippedDate = null;
            }

            System.out.println("Enter Comment: ");
            comments = sc.nextLine();

            System.out.println("Enter End Username: ");
            end_username = sc.nextLine();

            System.out.println("Enter End User Reason: ");
            end_userreason = sc.nextLine();

            if (Objects.equals(status, "Cancelled")) {
                pstmt = conn.prepareStatement("UPDATE orderdetails SET end_username=?, end_userreason='Order Cancelled' WHERE orderNumber=?");
                pstmt.setString(1, end_username);
                pstmt.setInt(2, Integer.parseInt(orderNumber));
                pstmt.executeUpdate();
            }

            pstmt = conn.prepareStatement ("UPDATE orders SET requiredDate=?, status=?,  shippedDate = ?, comments = ?, end_username=?, end_userreason=? WHERE orderNumber=?");
            pstmt.setString(1,  requiredDate);
            pstmt.setString(2,  status);
            pstmt.setString(3, shippedDate);
            pstmt.setString(4,  comments);
            pstmt.setString(5, end_username);
            pstmt.setString(6, end_userreason);
            pstmt.setInt(7, Integer.parseInt(orderNumber));
            pstmt.executeUpdate();



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

    public int updateOrderedProducts() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Order Number:");
        orderNumber = sc.nextLine();

        System.out.println("Enter Product Code:");
        productCode = sc.nextLine();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT * FROM orderdetails " +
                            "WHERE orderNumber=? AND productCode=? FOR UPDATE ");

            pstmt.setInt(1, Integer.parseInt(orderNumber));
            pstmt.setString(2, productCode);

            PreparedStatement lockCurrentProduct = conn.prepareStatement("SELECT * FROM current_products WHERE productCode= ? FOR UPDATE");
            lockCurrentProduct.setString(1, productCode);
            PreparedStatement lockOrder = conn.prepareStatement("SELECT * FROM orders WHERE orderNumber= ? LOCK IN SHARE MODE");
            lockOrder.setInt(1, Integer.parseInt(orderNumber));


            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();

            lockCurrentProduct.executeQuery();
            lockOrder.executeQuery();
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                productCode = rs.getString("productCode");
                quantityOrdered = rs.getInt("quantityOrdered");
                priceEach = rs.getDouble("priceEach");
                orderLineNumber = rs.getInt("orderLineNumber");
                referenceNo = rs.getInt("referenceNo");
                end_username = rs.getString("end_username");
                end_userreason = rs.getString("end_userreason");

            }
            rs.close();

            //System.out.println("Product Code: " + temp.productCode);
            //System.out.println("Product Name: " + productName);
            System.out.println("Quantity Ordered: " + quantityOrdered);
            System.out.println("Price Each: " + priceEach);
            System.out.println("Order Line Number: " + orderLineNumber);
            System.out.println("Reference Number: " + referenceNo);
            System.out.println("End Username: " + end_username);
            System.out.println("End User Reason: " + end_userreason);
            System.out.println("\n");

            System.out.println("Press enter key to enter new values for order");
            sc.nextLine();

            System.out.println("Enter Quantity Ordered: ");
            quantityOrdered = sc.nextInt();
            sc.nextLine();

            System.out.println("Enter Price Each: ");
            priceEach = sc.nextDouble();
            sc.nextLine();


            System.out.println("Enter Reference Number (type -1 if this product isn't intended to be shipped): ");
            referenceNo = sc.nextInt();
            sc.nextLine();

            pstmt = conn.prepareStatement(
                    "SELECT * FROM shipments " +
                            "WHERE referenceNo=? LOCK IN SHARE MODE ");

            pstmt.setInt(1, referenceNo);
            pstmt.executeQuery();

            System.out.println("Enter End Username: ");
            end_username = sc.nextLine();

            System.out.println("Enter End User Reason: ");
            end_userreason = sc.nextLine();

            if (referenceNo == -1) {
                pstmt = conn.prepareStatement ("UPDATE orderdetails SET quantityOrdered=?,  priceEach = ?, end_username=?, end_userreason=? WHERE orderNumber=? AND productCode=?");
                pstmt.setInt(1,  quantityOrdered);
                pstmt.setDouble(2, priceEach);
                pstmt.setString(3, end_username);
                pstmt.setString(4, end_userreason);
                pstmt.setInt(5, Integer.parseInt(orderNumber));
                pstmt.setString(6, productCode);

            } else {
                pstmt = conn.prepareStatement ("UPDATE orderdetails SET quantityOrdered=?,  priceEach = ?, referenceNo=?, end_username=?, end_userreason=? WHERE orderNumber=? AND productCode=?");
                pstmt.setInt(1,  quantityOrdered);
                pstmt.setDouble(2, priceEach);
                pstmt.setInt(3,  referenceNo);
                pstmt.setString(4, end_username);
                pstmt.setString(5, end_userreason);
                pstmt.setInt(6, Integer.parseInt(orderNumber));
                pstmt.setString(7, productCode);

            }
            pstmt.executeUpdate();



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

    public int getSpecificOrder() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Order Number:");
        orderNumber = sc.nextLine();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT * FROM orders LEFT JOIN orderdetails ON orders.orderNumber = orderdetails.orderNumber " +
                            "LEFT JOIN  current_products ON orderdetails.productCode = current_products.productCode " +
                            "LEFT JOIN  products ON current_products.productCode = products.productCode " +
                            "WHERE orders.orderNumber = ? LOCK IN SHARE MODE ");

            pstmt.setInt(1, Integer.parseInt(orderNumber));

            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();
            boolean flag = true;
            ArrayList<Sales> tempSalesList = new ArrayList<>();
            Sales temp;
            
            while (rs.next()) {
                if (flag) {
                    orderDate = rs.getString("orderDate");
                    requiredDate = rs.getString("requiredDate");
                    shippedDate = rs.getString("shippedDate");
                    status = rs.getString("status");
                    comments = rs.getString("comments");
                    customerNumber = rs.getInt("customerNumber");
                    end_username = rs.getString("end_username");
                    end_userreason = rs.getString("end_userreason");
                    flag = false;

                    System.out.println("Order View\n");
                    System.out.println("Order Date: " + orderDate);
                    System.out.println("Required Date: " + requiredDate);
                    System.out.println("Shipped Date: " + shippedDate);
                    System.out.println("status: " + status);
                    System.out.println("Comments: " + comments);
                    System.out.println("Customer Number: " + customerNumber);
                    System.out.println("End Username: " + end_username);
                    System.out.println("End User Reason: " + end_userreason);
                    System.out.println("------------------------------------------------\n");
                }

                if (rs.getString("productCode") != null) {
                    productCode = rs.getString("productCode");
                    productName = rs.getString("productName");
                    quantityOrdered = rs.getInt("quantityOrdered");
                    priceEach = rs.getDouble("priceEach");
                    orderLineNumber = rs.getInt("orderLineNumber");
                    referenceNo = rs.getInt("referenceNo");
                    end_username = rs.getString("end_username");
                    end_userreason = rs.getString("end_userreason");
                    tempSalesList.add(new Sales(productCode, quantityOrdered, priceEach, orderLineNumber, referenceNo, productName, end_username, end_userreason));
                }
            }


            System.out.println("Products Ordered:\n");

            for (int i = 0; i < tempSalesList.size(); i++) {
                temp = tempSalesList.get(i);

                System.out.println("Product Code: " + temp.productCode);
                System.out.println("Product Name: " + temp.productName);
                System.out.println("Quantity Ordered: " + temp.quantityOrdered);
                System.out.println("Price Each: " + temp.priceEach);
                System.out.println("Order Line Number: " + temp.orderLineNumber);
                System.out.println("Reference Number: " + temp.referenceNo);
                System.out.println("End Username: " + temp.end_username);
                System.out.println("End User Reason: " + temp.end_userreason);
                System.out.println("\n");

            }

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

    public int getAllOrderRecords() {
        Scanner sc = new Scanner(System.in);
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM orders LOCK IN SHARE MODE ");

            System.out.println("\nPress enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()) {
                orderNumber = rs.getString("orderNumber");
                orderDate = rs.getString("orderDate");
                requiredDate = rs.getString("requiredDate");
                shippedDate = rs.getString("shippedDate");
                status = rs.getString("status");
                comments = rs.getString("comments");
                customerNumber = rs.getInt("customerNumber");
                end_username = rs.getString("end_username");
                end_userreason = rs.getString("end_userreason");

                System.out.println("\nOrder Number: " + orderNumber);
                System.out.println("Order Date: " + orderDate);
                System.out.println("Required Date: " + requiredDate);
                System.out.println("Shipped Date: " + shippedDate);
                System.out.println("status: " + status);
                System.out.println("Comments: " + comments);
                System.out.println("Customer Number: " + customerNumber);
                System.out.println("End Username: " + end_username);
                System.out.println("End User Reason: " + end_userreason);
            }

            System.out.println("\nPress enter key to end transaction");
            sc.nextLine();

            rs.close();

            pstmt.close();
            conn.commit();
            conn.close();

            return 1;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int getOrder() {
        Scanner sc = new Scanner(System.in);
        System.out.println("[1] View All Orders [2] View Specific Order");
        int choice = sc.nextInt();
        if (choice == 1) return getAllOrderRecords();
        if (choice == 2) return getSpecificOrder();

        return 0;
    }

    public int getAllProductRecords() {
        Scanner sc = new Scanner(System.in);
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products p LEFT JOIN current_products cp ON cp.productCode = p.productCode WHERE p.product_category != 'D' LOCK IN SHARE MODE");

            System.out.println("\nPress enter key to view all product records");
            sc.nextLine();

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                productCode = rs.getString("productCode");
                productName = rs.getString("productName");

                stmt = conn.prepareStatement("SELECT * FROM product_productlines pp LEFT JOIN productlines p ON pp.productLine = p.productLine WHERE pp.productCode = ? LOCK IN SHARE MODE");
                stmt.setString(1, productCode);
                ResultSet productLines = stmt.executeQuery();

                System.out.println("\nProduct Code: " + productCode);
                System.out.println("Product Name: " + productName);
                System.out.println("Quantity: " + rs.getInt("quantityInStock"));

                System.out.println("Product Lines:");
                while (productLines.next()) {
                    System.out.println(productLines.getString("productLine"));
                }
                productLines.close();
            }

            System.err.println("\nPress enter key to end transaction");
            sc.nextLine();

            rs.close();
            stmt.close();
            conn.commit();
            conn.close();

            return 1;

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
    }

    public int getSpecificProduct() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Product Code:");
        productCode = sc.nextLine();
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-176128-0.cloudclusters.net:10107/DBSALES26_G208?useTimezone=true&serverTimezone=UTC&user=DBADM_208&password=DLSU1234!");
            conn.setAutoCommit(false);
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products p LEFT JOIN current_products cp ON cp.productCode = p.productCode WHERE p.product_category != 'D' AND p.productCode = ? LOCK IN SHARE MODE");
            stmt.setString(1, productCode);

            System.out.println("\nPress enter key to view specific product record");
            sc.nextLine();

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                productCode = rs.getString("productCode");
                productName = rs.getString("productName");

                stmt = conn.prepareStatement("SELECT * FROM product_productlines pp LEFT JOIN productlines p ON pp.productLine = p.productLine WHERE pp.productCode = ? LOCK IN SHARE MODE");
                stmt.setString(1, productCode);
                ResultSet productLines = stmt.executeQuery();

                System.out.println("\nProduct Code: " + productCode);
                System.out.println("Product Name: " + productName);
                System.out.println("Quantity: " + rs.getInt("quantityInStock"));

                System.out.println("Product Lines:");
                while (productLines.next()) {
                    System.out.println(productLines.getString("productLine"));
                }
                productLines.close();

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
                System.out.println("MSRP: " + msrp);
                System.out.println("Minimum Price: " + minPrice);
                System.out.println("Maximum Price: " + maxPrice);
            }

            System.err.println("\nPress enter key to end transaction");
            sc.nextLine();

            rs.close();
            stmt.close();
            conn.commit();
            conn.close();

            return 1;

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
    }

    public int getProductInfo() {
        Scanner sc = new Scanner(System.in);
        System.out.println("[1] View All Products [2] View Specific Product");
        int choice = sc.nextInt();

        if (choice == 1) return getAllProductRecords();
        if (choice == 2) return getSpecificProduct();

        return 0;
    }

    public static void main (String args[]) {
        Scanner sc     = new Scanner (System.in);
        int     choice = 0;
        // Letting the use choose between the two functions
        System.out.println("Enter \n[1] Create an order\n[2] Update order\n" +
                "[3] Record products ordered\n[4] Update ordered products\n" +
                "[5] Get order info\n[6] Get product info");
        choice = sc.nextInt();
        Sales s = new Sales();
        if (choice==1) s.createOrder();
        if (choice==2) s.updateOrder();
        if (choice==3) s.recordProductsOrdered();
        if (choice==4) s.updateOrderedProducts();
        if (choice==5) s.getOrder();
        if (choice==6) s.getProductInfo();

        System.out.println("Press enter key to continue....");
        sc.nextLine();
    }
}
