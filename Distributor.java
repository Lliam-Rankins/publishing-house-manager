
/**
 * The Distributor class handles operations related to distributors
 * in the database, such as inserting new distributor records.
 */
import java.sql.*;

public class Distributor {
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String RESET = "\033[0m";
    private Connection connection;

    public Distributor() {
        // nothing
    }

    public Distributor(Connection connection) {
        this.connection = connection;

    }

    /**
     * Adds a new distributor to the database. For this it is necessary to gather
     * all the requiered information
     *
     * @param distribID   unique identifier of the distributor
     * @param balance     current balance of the distributor
     * @param contactName name of the contact person
     * @param phoneNumber phone number of the distributor
     * @param category    category/type of distributor
     * @param name        name of the distributor
     * @param street      street address
     * @param city        city of the distributor
     * @param state       state of the distributor
     * @return true if the distributor was successfully added, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean addDistributor(int distribID, float balance, String contactName, String phoneNumber, String category,
            String name, String street, String city, String state) throws SQLException {

        // System.out.println("DEBUG: Entering addDistributor");
        // System.out.println("DEBUG: distribID=" + distribID + ", balance=" + balance);

        DBManager.beginTransaction();
        // only add null if there is no a value
        String phoneVal = (phoneNumber == null || phoneNumber.trim().isEmpty()) ? "NULL"
                : "'" + phoneNumber.trim().replace("'", "''") + "'";
        String catVal = (category == null || category.trim().isEmpty()) ? "NULL"
                : "'" + category.trim().replace("'", "''") + "'";
        String nameVal = (name == null || name.trim().isEmpty()) ? "NULL"
                : "'" + name.trim().replace("'", "''") + "'";
        // always add quotes to NOT NULL values
        String contactVal = "'" + contactName.trim().replace("'", "''") + "'";
        String streetVal = "'" + street.trim().replace("'", "''") + "'";
        String cityVal = "'" + city.trim().replace("'", "''") + "'";
        String stateVal = "'" + state.trim().replace("'", "''") + "'";
        String sql = String.format(java.util.Locale.US,
                "INSERT INTO Distributor VALUES(%d, %f, %s, %s, %s, %s, %s, %s, %s)",
                distribID, balance, contactVal, phoneVal, catVal, nameVal, streetVal, cityVal, stateVal);
        System.out.println("DEBUG SQL: " + sql);
        if (!DBManager.executeUpdate(sql)) {
            System.out.println(RED + "Couldn't add this distributor" + RESET);
            DBManager.rollbackTransaction();
            return false;
        }
        DBManager.commitTransaction();
        return true;
    }

    /**
     * Update Distributor, the user enter the field they want to update and then the
     * value
     *
     * @param distribID unique identifier of the distributor
     * @param field     which field of distributor information you want to update:
     *                  contactName. phoneNumber, etc
     * @param value     the value you want to introduce in that field
     * @throws SQLException if a database access error occurs
     * 
     */
    public void updateDistributor(String field, String value, int distribID) throws SQLException {
        DBManager.beginTransaction();
        String sql = "UPDATE Distributor SET " + field + " = ? WHERE distribID = ?";
        // use prepared statment here because the field is unkwon and we cannot know if
        // it accept NULL or not
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, value);
        pstmt.setInt(2, distribID);
        int rows = pstmt.executeUpdate();
        if (rows == 0) {
            System.out.println(RED + "No distributor found with ID " + distribID + RESET);
            DBManager.rollbackTransaction();
        } else {
            DBManager.commitTransaction();
            System.out.println(GREEN + "Change made Successfully!" + RESET);
        }
        pstmt.close();
    }

    /**
     * Delete a Distributor
     * 
     * @param distribID unique identifier of the distributor
     * @throws SQLException
     * 
     */

    public void deleteDistributor(int distribID) throws SQLException {
        DBManager.beginTransaction();
        String sql = "DELETE FROM Distributor WHERE distribID = ?; ";
        // Use prepared statment to be aware of not existing data
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, distribID);
        int rows = pstmt.executeUpdate();
        pstmt.close();
        if (rows == 0) {
            System.out.println(RED + "No distributor found with ID " + distribID + RESET);
        } else {
            System.out.println(GREEN + "Distributor deleted successfully." + RESET);
        }
        DBManager.commitTransaction();
    }

    /**
     * Input order from a distributor for a book edition using the ISBN
     * 
     * @param oID            unique identifier of an order
     * @param ISBN           unique identifier of an edition
     * @param dueBy          date when the order is supposed to be payed
     * @param shippingCost   cost of shipping
     * @param datePlaced     date when the order was placed
     * @param deliveryStatus this value stores if a order was delivered or not
     * @param paymentStatus  this value stores the current status of an order, such
     *                       as payed, processed, etc
     * @param copies         this value stores the number of copies that were
     *                       required in an order
     * @return true is created, otherwise false
     * @throws SQLException
     */

    public boolean inputOrderISBN(int distribID, int oID, long ISBN, String dueBy, Float shippingCost,
            String datePlaced, String deliveryStatus,
            String paymentStatus, int copies) throws SQLException {
        DBManager.beginTransaction();
        // control that values are logical
        if (copies <= 0 || (shippingCost != null && shippingCost < 0)) {
            System.out.println(RED + "Not valid values for copies or shipping cost" + RESET);
            return false;
        }

        // Handle NULL values for optional fields
        String dueByVal = (dueBy == null || dueBy.trim().isEmpty()) ? "NULL" : "'" + dueBy.trim() + "'";
        String shippingCostVal = (shippingCost == null) ? "NULL"
                : String.format(java.util.Locale.US, "%f", shippingCost);

        String sql = String.format(java.util.Locale.US,
                "INSERT INTO `Order` VALUES(%d, %s, %s, '%s', '%s', '%s', %d)",
                oID, dueByVal, shippingCostVal, datePlaced.trim(),
                deliveryStatus.trim(), paymentStatus.trim(), copies);

        if (!DBManager.executeUpdate(sql)) {
            System.out.println(RED + "Couldn't insert order" + RESET);
            DBManager.rollbackTransaction();
            return false;
        }
        // Which publication (edition) is related to this orders
        String sql2 = String.format(
                "INSERT INTO ContainsISBN VALUES(%d, %d)",
                oID, ISBN);

        if (!DBManager.executeUpdate(sql2)) {
            System.out.println(RED + "Couldn't insert ISBN relation" + RESET);
            DBManager.rollbackTransaction();
            return false;
        }

        // Who placed the order

        String sql3 = String.format("INSERT INTO PlacedBy VALUES (%d, %d)", distribID, oID);

        if (!DBManager.executeUpdate(sql3)) {
            System.out.println(RED + "Couldn't add this order");
            DBManager.rollbackTransaction();
            return false;
        }

        DBManager.commitTransaction();
        return true;
    }

    /**
     * Input a new order related to an specifid Issue of a magazine or periodic
     * publication
     * 
     * @param oID            unique identifier of an order
     * @param pubID          unique identifier of a publication
     * @param issueTitle     title of an specific issue
     * @param dueBy          date when the order is supposed to be delivered
     * @param shippingCost   cost of shipping
     * @param datePlaced     date when the order was placed
     * @param deliveryStatus delivery status of the current order, such as
     *                       processed, delivered, etc
     * @param paymentStatus  payment status of the current oder, such as payed,
     *                       billed, etc
     * @param copies         number of copies of that certain issue that were
     *                       requiered
     * @return true if the new order is created, false otherwise
     * @throws SQLException
     */

    public boolean inputOrderIssue(int distribID, int oID, int pubID, String issueTitle,
            String dueBy, Float shippingCost,
            String datePlaced, String deliveryStatus,
            String paymentStatus, int copies) throws SQLException {
        DBManager.beginTransaction();
        // control for logic values
        if (copies <= 0 || (shippingCost != null && shippingCost < 0)) {
            System.out.println(RED + "Not valid values for copies or shipping cost" + RESET);
            return false;
        }
        // Handle NULL values for optional fields
        String dueByVal = (dueBy == null || dueBy.trim().isEmpty()) ? "NULL" : "'" + dueBy.trim() + "'";
        String shippingCostVal = (shippingCost == null) ? "NULL"
                : String.format(java.util.Locale.US, "%f", shippingCost);

        String sql = String.format(java.util.Locale.US,
                "INSERT INTO `Order` VALUES(%d, %s, %s, '%s', '%s', '%s', %d)",
                oID, dueByVal, shippingCostVal, datePlaced.trim(),
                deliveryStatus.trim(), paymentStatus.trim(), copies);

        if (!DBManager.executeUpdate(sql)) {
            System.out.println(RED + "Couldn't insert order" + RESET);
            DBManager.rollbackTransaction();
            return false;
        }

        String sql2 = String.format(
                "INSERT INTO ContainsIssue VALUES(%d, %d, '%s')",
                oID, pubID, issueTitle.trim().replace("'", "''"));

        if (!DBManager.executeUpdate(sql2)) {
            System.out.println(RED + "Couldn't insert issue relation" + RESET);
            DBManager.rollbackTransaction();
            return false;
        }

        // Who placed the order

        String sql3 = String.format("INSERT INTO PlacedBy VALUES (%d, %d)", distribID, oID);
        if (!DBManager.executeUpdate(sql3)) {
            System.out.println(RED + "Couldn't add this order");
            DBManager.rollbackTransaction();
            return false;
        }

        DBManager.commitTransaction();
        return true;
    }

    /**
     * Mark an order as billed
     * 
     * @param oID           unique identifier of an order
     * @param distribID     unique idenfier of a distributor
     * @param paymentStatus payment status: which will be updated to billed
     * @return true if the paymentStatus is changed, otherwise, false.
     * @throws SQLException
     */
    // Bill distributor for an order.
    public boolean billDistributor(int oID, int distribID) throws SQLException {
        if (!checkPlacedBy(oID, distribID)) {
            // if false not execute
            return false;
        }
        DBManager.beginTransaction();
        String sql2 = String.format(
                "UPDATE `Order` SET paymentStatus = 'Billed' WHERE oID = %d",
                oID);
        if (!DBManager.executeUpdate(sql2)) {
            System.out.println(RED + "Couldn't update payment status" + RESET);
            DBManager.rollbackTransaction();
            return false;
        }
        DBManager.commitTransaction();
        return true;
    }

    /**
     * This function checks if a certain order was placed by a ceratin author
     * 
     * @param oID       unique identificator of an order
     * @param distribID unique identificator of a distributor
     * @return true if there exist a relation between the order and distributor
     * @throws SQLException
     */

    public boolean checkPlacedBy(int oID, int distribID) throws SQLException {
        String sql = "SELECT o.paymentStatus FROM `Order` o " +
                "JOIN PlacedBy p ON o.oID = p.oID " +
                "WHERE o.oID = ? AND p.distribID = ?";
        // PreparedStatement is used to improve clarity and structure of parameter
        // handling
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, oID);
        pstmt.setInt(2, distribID);
        ResultSet rs = pstmt.executeQuery();

        boolean isValid = false;

        if (rs.next()) {
            String status = rs.getString("paymentStatus");
            // CHECK IF PAYED OR ALREADY BILLES
            if ("Paid".equalsIgnoreCase(status) || "Billed".equalsIgnoreCase(status)) {
                System.out.println("Error: The order is already " + status + ".");
            } else {
                isValid = true; // It is of the distributor and we can bill it
            }
        } else {
            // If the result is empty is because this order is not associated with the
            // distributor
            System.out.println("Error: Order ID " + oID + " does not belong to Distributor ID " + distribID
                    + " or does not exist.");
        }

        rs.close();
        pstmt.close();
        return isValid;
    }

    /**
     * Receive a payment and change the outstanding balance of a distributor.
     * 
     * @param distribID unique identifier of a distributor
     * @return true if paid, false otherwise
     * @throws SQLException
     */
    public boolean receivePayment(int oID, int distribID) throws SQLException {

        if (!checkPlacedBy(oID, distribID)) {
            // if false not execute
            return false;
        }

        DBManager.beginTransaction();
        // check if the order is not already paid
        try {
            String checkStatusSql = String.format("SELECT paymentStatus FROM `Order` WHERE oID = %d", oID);
            ResultSet rsStatus = DBManager.executeQuery(checkStatusSql);

            if (rsStatus.next()) {
                String status = rsStatus.getString("paymentStatus");
                // Use equalsIgnoreCase to be safe with casing (e.g. "paid", "Paid", "PAID")
                if (status != null && status.equalsIgnoreCase("Paid")) {
                    System.out.println("Error: (Order is already Paid).");
                    rsStatus.close();
                    DBManager.rollbackTransaction();
                    return false;
                }
            }
            rsStatus.close();
        } catch (SQLException e) {
            System.out.println("Error checking payment status.");
            e.printStackTrace();
            DBManager.rollbackTransaction();
            return false;
        }
        // Changing the status
        String sql = String.format(
                "UPDATE `Order` SET paymentStatus = '%s' WHERE oID = %d",
                "Paid", oID);

        if (!DBManager.executeUpdate(sql)) {
            System.out.println("It is not possible to pay this order");
            DBManager.rollbackTransaction();
            return false;
        }
        // Getting the balance
        String sql2 = String.format(
                "SELECT balance FROM Distributor WHERE distribID = %d",
                distribID);
        ResultSet rs = DBManager.executeQuery(sql2);
        float balance = 0.0f;
        float orderTotal = 0.0f;
        //
        try {
            if (rs.next()) {
                balance = rs.getFloat("balance");
            }
            rs.close();

            String isbnSql = String.format(
                    "SELECT COALESCE(ROUND(o.copies * e.price, 2), 0) AS orderTotal " +
                            "FROM `Order` o " +
                            "JOIN ContainsISBN ci ON o.oID = ci.oID " +
                            "JOIN Edition e ON ci.ISBN = e.ISBN " +
                            "WHERE o.oID = %d",
                    oID);
            ResultSet rsIsbn = DBManager.executeQuery(isbnSql);
            if (rsIsbn.next() && rsIsbn.getObject("orderTotal") != null) {
                orderTotal = rsIsbn.getFloat("orderTotal");
            }
            rsIsbn.close();

            if (orderTotal == 0.0f) {
                // using COALESCE TO PROTECT OPERATIONS FROM NULL
                String issueSql = String.format(
                        "SELECT COALESCE(SUM(o.copies * i.price), 0) AS orderTotal " +
                                "FROM `Order` o " +
                                "JOIN ContainsIssue ci ON o.oID = ci.oID " +
                                "JOIN Issue i ON ci.pubID = i.pubID AND ci.issueTitle = i.issueTitle " +
                                "WHERE o.oID = %d " +
                                "GROUP BY o.oID",
                        oID);

                ResultSet rsIssue = DBManager.executeQuery(issueSql);
                if (rsIssue.next() && rsIssue.getObject("orderTotal") != null) {
                    orderTotal = rsIssue.getFloat("orderTotal");
                }
                rsIssue.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            DBManager.rollbackTransaction();
            return false;
        }
        // calculating the new balance
        float newBalance = balance - orderTotal;

        String sql3 = String.format(java.util.Locale.US,
                "UPDATE Distributor SET balance = %f WHERE distribID = %d",
                newBalance, distribID);

        if (!DBManager.executeUpdate(sql3)) {
            System.out.println("Failed to update distributor balance");
            DBManager.rollbackTransaction();
            return false;
        }

        DBManager.commitTransaction();

        System.out.println("Payment received. New balance: $" + newBalance);
        return true;
    }

    /**
     * This function returns the total of an order by adding the shipping cost}
     * and the the price of individual copies
     * 
     * @param orderID
     * @return the total of an order
     */
    public static float getOrderTotal(int orderID) {
        // Try issues first
        String query = "SELECT (o.copies * i.price) AS orderTotal " +
                "FROM `Order` o " +
                "JOIN ContainsIssue ci ON o.oID = ci.oID " +
                "JOIN Issue i ON ci.pubID = i.pubID AND ci.issueTitle = i.issueTitle " +
                "WHERE o.oID = " + orderID;

        ResultSet rs = DBManager.executeQuery(query);

        try {
            if (rs.next()) {
                return rs.getFloat("orderTotal");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Try editions
        query = "SELECT (o.copies * e.price ) AS orderTotal " +
                "FROM `Order` o " +
                "JOIN ContainsISBN cisbn ON o.oID = cisbn.oID " +
                "JOIN Edition e ON cisbn.ISBN = e.ISBN " +
                "WHERE o.oID = " + orderID;

        rs = DBManager.executeQuery(query);

        try {
            if (rs.next()) {
                return rs.getFloat("orderTotal");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0f;
    }

    /**
     * This function list all unpaid order form a certain author
     * 
     * @param distribID
     * @return
     */
    public static boolean listUnpaidOrders(int distribID) {
        String query = "SELECT o.oID, o.datePlaced, o.dueBy, o.shippingCost, o.copies, " +
                "o.deliveryStatus, o.paymentStatus " +
                "FROM `Order` o " +
                "NATURAL JOIN PlacedBy pb " +
                "WHERE o.paymentStatus != 'Paid' " +
                "AND pb.distribID = %d;";

        query = String.format(query, distribID);

        ResultSet rs = DBManager.executeQuery(query);

        try {
            System.out.println("\n=== Unpaid Orders - Distributor " + distribID + " ===\n");

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                int oID = rs.getInt("oID");
                String datePlaced = rs.getString("datePlaced");
                String dueBy = rs.getString("dueBy");
                float shippingCost = rs.getFloat("shippingCost");
                int copies = rs.getInt("copies");
                String deliveryStatus = rs.getString("deliveryStatus");
                String paymentStatus = rs.getString("paymentStatus");

                System.out.println("Order ID: " + oID +
                        " | Date Placed: " + datePlaced +
                        " | Due By: " + dueBy +
                        " | Copies: " + copies +
                        " | Shipping: $" + shippingCost +
                        " | Delivery Status: " + deliveryStatus +
                        " | Payment Status: " + paymentStatus);
            }

            if (!hasResults) {
                System.out.println("No unpaid orders found in that date range.");
            }

            return hasResults;

        } catch (SQLException e) {
            System.err.println("Error querying unpaid orders: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Identify distributors whose total billed amount does not match the sum of
     * their recorded payments.
     * 
     * @throws SQLException
     */
    public void identifyMismatchedDistributors() throws SQLException {

        String sql = "SELECT d.distribID, d.name, d.balance, " +
                "COALESCE(calc.CalculatedBalance, 0) AS CalculatedBalance " +
                "FROM Distributor d " +
                "LEFT JOIN ( " +
                "SELECT CombinedResults.distribID, SUM(SubBalance) AS CalculatedBalance " +
                "FROM ( " +
                "SELECT Distributor.distribID, " +
                "SUM((`Order`.copies * Issue.price)) AS SubBalance " +
                "FROM Distributor " +
                "NATURAL JOIN PlacedBy " +
                "NATURAL JOIN `Order` " +
                "NATURAL JOIN ContainsIssue " +
                "NATURAL JOIN Issue " +
                "WHERE `Order`.paymentStatus != 'Paid' " +
                "GROUP BY Distributor.distribID " +
                "UNION ALL " +
                "SELECT Distributor.distribID, " +
                "SUM((`Order`.copies * Edition.price)) AS SubBalance " +
                "FROM Distributor " +
                "NATURAL JOIN PlacedBy " +
                "NATURAL JOIN `Order` " +
                "NATURAL JOIN ContainsISBN " +
                "NATURAL JOIN Edition " +
                "WHERE `Order`.paymentStatus != 'Paid' " +
                "GROUP BY Distributor.distribID " +
                ") AS CombinedResults " +
                "GROUP BY CombinedResults.distribID " +
                ") AS calc ON d.distribID = calc.distribID " +
                "WHERE ABS(d.balance - COALESCE(calc.CalculatedBalance, 0)) >= 0.01";
        ResultSet rs = DBManager.executeQuery(sql);

        while (rs.next()) {
            System.out.println("distribID: " + rs.getInt("distribID") +
                    " | Name: " + rs.getString("name") +
                    " | Balance: " + rs.getFloat("balance") +
                    " | Calculated Balance: " + rs.getFloat("CalculatedBalance"));

        }

        rs.close();
        // pstmt.close();
    }

    /**
     * List all distributors of a specific type located in a given city.
     * 
     * @param category the category of distributors you want to list
     * @param city     the city in which you want to search
     * @throws SQLException
     */
    public void listDistributors(String category, String city) throws SQLException {

        String sql = String.format(
                "SELECT * FROM Distributor WHERE category = '%s' AND city = '%s'",
                category, city);
        ResultSet rs = DBManager.executeQuery(sql);
        while (rs.next()) {
            System.out.println("distribID: " + rs.getInt("distribID") +
                    " | Name: " + rs.getString("name") +
                    " | Category: " + rs.getString("category") +
                    " | City: " + rs.getString("city"));
        }
        rs.close();
    }

}