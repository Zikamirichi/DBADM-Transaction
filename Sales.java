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
                 String productName) {
        this.productCode = productCode;
        this.quantityOrdered = quantityOrdered;
        this.priceEach = priceEach;
        this.orderLineNumber = orderLineNumber;
        this.referenceNo = referenceNo;
        this.productName = productName;
    }

    public int createOrder() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Required Date:");
        requiredDate = sc.nextLine();

        System.out.println("Enter Comments:");
        comments = sc.nextLine();

        System.out.println("Enter Customer Number:");
        customerNumber = sc.nextInt();


        // TODO determine get previous assignment or newly?

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.5g208?useTimezone=true&serverTimezone=UTC&user=root&password=root");
            System.out.println("Connection Successful");

            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO orders (`requiredDate`, `comments`, `customerNumber`) " +
                            "VALUES (?, ?, ?)");

            pstmt.setString(1, requiredDate);
            pstmt.setString(2, comments);
            pstmt.setInt(3, customerNumber);

            pstmt.executeUpdate();


            System.out.println("Order created");

            System.out.println("Press enter key to end transaction");
            sc.nextLine();

            pstmt.close();
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

        System.out.println("Enter Price Each:");
        priceEach = sc.nextDouble();



        // TODO determine get previous assignment or newly?

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.5g208?useTimezone=true&serverTimezone=UTC&user=root&password=root");
            System.out.println("Connection Successful");

            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO orderdetails (`orderNumber`, `productCode`, `quantityOrdered`, `priceEach`) " +
                            "VALUES (?, ?, ?, ?)");

            pstmt.setInt(1, Integer.parseInt(orderNumber));
            pstmt.setString(2, productCode);
            pstmt.setInt(3, quantityOrdered);
            pstmt.setDouble(4, priceEach);

            pstmt.executeUpdate();


            System.out.println("Ordered Product created");

            System.out.println("Press enter key to end transaction");
            sc.nextLine();

            pstmt.close();
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

        // TODO determine get previous assignment or newly?

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.5g208?useTimezone=true&serverTimezone=UTC&user=root&password=root");
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

            }
            rs.close();

            System.out.println("Update Order\n");
            System.out.println("Order Date: " + orderDate);
            System.out.println("Required Date: " + requiredDate);
            System.out.println("Shipped Date: " + shippedDate);
            System.out.println("status: " + status);
            System.out.println("Comments: " + comments);
            System.out.println("Customer Number: " + customerNumber);

            System.out.println("Press enter key to enter new values for order");
            sc.nextLine();

            System.out.println("Enter status: ");
            status = sc.nextLine();

            if (Objects.equals(status, "Shipped")) {
                System.out.println("Enter Shipped Date: ");
                shippedDate = sc.nextLine();
            }

            System.out.println("Enter Comment: ");
            comments = sc.nextLine();

            pstmt = conn.prepareStatement ("UPDATE orders SET status=?,  shippedDate = ?, comments = ? WHERE orderNumber=?");
            pstmt.setString(1,  status);
            pstmt.setString(2, shippedDate);
            pstmt.setString(3,  comments);
            pstmt.setInt(4, Integer.parseInt(orderNumber));
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

        // TODO determine get previous assignment or newly?

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.5g208?useTimezone=true&serverTimezone=UTC&user=root&password=root");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT * FROM orderdetails " +
                            "WHERE orderNumber=? AND productCode=? FOR UPDATE ");

            pstmt.setInt(1, Integer.parseInt(orderNumber));
            pstmt.setString(2, productCode);


            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();


            while (rs.next()) {
                productCode = rs.getString("productCode");
                quantityOrdered = rs.getInt("quantityOrdered");
                priceEach = rs.getDouble("priceEach");
                orderLineNumber = rs.getInt("orderLineNumber");
                referenceNo = rs.getInt("referenceNo");

            }
            rs.close();

            //System.out.println("Product Code: " + temp.productCode);
            //System.out.println("Product Name: " + productName);
            System.out.println("Quantity Ordered: " + quantityOrdered);
            System.out.println("Price Each: " + priceEach);
            System.out.println("Order Line Number: " + orderLineNumber);
            System.out.println("Reference Number: " + referenceNo);
            System.out.println("\n");

            System.out.println("Press enter key to enter new values for order");
            sc.nextLine();

            System.out.println("Enter Quantity Ordered: ");
            quantityOrdered = sc.nextInt();

            System.out.println("Enter Price Each: ");
            priceEach = sc.nextDouble();


            System.out.println("Enter Reference Number (type -1 if this product isn't intended to be shipped): ");
            referenceNo = sc.nextInt();

            if (referenceNo == -1) {
                pstmt = conn.prepareStatement ("UPDATE orderdetails SET quantityOrdered=?,  priceEach = ? WHERE orderNumber=? AND productCode=?");
                pstmt.setInt(1,  quantityOrdered);
                pstmt.setDouble(2, priceEach);
                pstmt.setInt(3, Integer.parseInt(orderNumber));
                pstmt.setString(4, productCode);
            } else {
                pstmt = conn.prepareStatement ("UPDATE orderdetails SET quantityOrdered=?,  priceEach = ?, referenceNo=? WHERE orderNumber=? AND productCode=?");
                pstmt.setInt(1,  quantityOrdered);
                pstmt.setDouble(2, priceEach);
                pstmt.setInt(3,  referenceNo);
                pstmt.setInt(4, Integer.parseInt(orderNumber));
                pstmt.setString(5, productCode);
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

    public int getOrder() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Order Number:");
        orderNumber = sc.nextLine();

        // TODO determine get previous assignment or newly?

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.5g208?useTimezone=true&serverTimezone=UTC&user=root&password=root");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT * FROM orders JOIN orderdetails ON orders.orderNumber = orderdetails.orderNumber " +
                            "JOIN  current_products ON orderdetails.productCode = current_products.productCode " +
                            "JOIN  products ON current_products.productCode = products.productCode " +
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
                    flag = false;
                }

                productCode = rs.getString("productCode");
                productName = rs.getString("productName");
                quantityOrdered = rs.getInt("quantityOrdered");
                priceEach = rs.getDouble("priceEach");
                orderLineNumber = rs.getInt("orderLineNumber");
                referenceNo = rs.getInt("referenceNo");
                tempSalesList.add(new Sales(productCode, quantityOrdered, priceEach, orderLineNumber, referenceNo, productName));

            }

            System.out.println("Order View\n");
            System.out.println("Order Date: " + orderDate);
            System.out.println("Required Date: " + requiredDate);
            System.out.println("Shipped Date: " + shippedDate);
            System.out.println("status: " + status);
            System.out.println("Comments: " + comments);
            System.out.println("Customer Number: " + customerNumber);
            System.out.println("------------------------------------------------\n");
            System.out.println("Products Ordered:\n");

            for (int i = 0; i < tempSalesList.size(); i++) {
                temp = tempSalesList.get(i);

                System.out.println("Product Code: " + temp.productCode);
                System.out.println("Product Name: " + temp.productName);
                System.out.println("Quantity Ordered: " + temp.quantityOrdered);
                System.out.println("Price Each: " + temp.priceEach);
                System.out.println("Order Line Number: " + temp.orderLineNumber);
                System.out.println("Reference Number: " + temp.referenceNo);
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

    public int getProductPriceRange() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Product Number:");
        productCode = sc.nextLine();

        // TODO determine get previous assignment or newly?

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbsalesv2.5g208?useTimezone=true&serverTimezone=UTC&user=root&password=root");
            System.out.println("Connection Successful");
            conn.setAutoCommit(false);

            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT getPrice(?, 'MIN') AS minPrice, getPrice(?, 'MAX') AS maxPrice LOCK IN SHARE MODE ");

            pstmt.setString(1, productCode);
            pstmt.setString(2, productCode);

            System.out.println("Press enter key to start retrieving the data");
            sc.nextLine();

            ResultSet rs = pstmt.executeQuery();
            double minPrice = 0;
            double maxPrice = 0;


            while (rs.next()) {
                minPrice = rs.getDouble("minPrice");
                maxPrice = rs.getDouble("maxPrice");


            }

            System.out.println("Product Price Range\n");
            System.out.println("Product Code: " + productCode);
            System.out.println("Minimum Price: " + minPrice);
            System.out.println("Maximum Price: " + maxPrice);

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

    public static void main (String args[]) {
        Scanner sc     = new Scanner (System.in);
        int     choice = 0;
        // Letting the use choose between the two functions
        System.out.println("Enter [1] Create an order  [2] Record products ordered \n" +
                "[3] Update order  [4] Update ordered products \n" +
                "[5] Get entire order  [6] Get product together with allowable pricing ");
        choice = sc.nextInt();
        Sales s = new Sales();
        if (choice==1) s.createOrder();
        if (choice==2) s.recordProductsOrdered();
        if (choice==3) s.updateOrder();
        if (choice==4) s.updateOrderedProducts();
        if (choice==5) s.getOrder();
        if (choice==6) s.getProductPriceRange();

        System.out.println("Press enter key to continue....");
        sc.nextLine();
    }
}