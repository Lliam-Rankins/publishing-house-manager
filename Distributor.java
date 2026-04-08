/**
 * The Distributor class handles operations related to distributors
 * in the database, such as inserting new distributor records.
 */
import java.sql.*;
public class Distributor{
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String RESET = "\033[0m";
    private Connection connection;

    public Distributor(Connection connection){
        this.connection = connection;

        
    }
    /**
     * Adds a new distributor to the database.
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
    public boolean addDistributor(String distribID, float balance, String contactName, String phoneNumber, String category, String name, String street, String city, String state) throws SQLException {
        String sql = "INSERT INTO Distributor VALUES('%s', %f, '%s', '%s', '%s', '%s', '%s', '%s', '%s') ";
        sql = String.format(sql,distribID,balance,contactName,phoneNumber,category,name, street, city, state);
        if (!DBManager.executeUpdate(sql)) {
            System.out.println("Couldn't add this distributor");
            return false;
        }

        return true;
    }

    /**
     * Update Distributor
     *
     * @param distribID   unique identifier of the distributor
     * @param field       which field of distributor information you want to update: contactName. phoneNumber, etc
     * @param value       the value you want to introduce in that field
     * @throws SQLException if a database access error occurs
     * 
     */
    public void updateDistributor(String field, String value, int distribID) throws SQLException {
        String sql = "UPDATE Distributor SET " + field + " = ? WHERE distribID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, value);
        pstmt.setInt(2, distribID);
        pstmt.executeUpdate();
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
        String sql = "DELETE FROM Distributor WHERE distribID = ?; ";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, distribID);
        int rows = pstmt.executeUpdate();
        pstmt.close();
        if (rows == 0) {
            System.out.println(RED + "No distributor found with ID " + distribID + RESET);
        } else {
            System.out.println(GREEN+ "Distributor deleted successfully."+ RESET);
        }
    }

    /**
     * Input order from a distributor for a book edition using the ISBN
     * 
     * @param oID               unique identifier of an order
     * @param ISBN              unique identifier of an edition
     * @param dueBy             date when the order is supposed to be payed
     * @param shippingCost      cost of shipping
     * @param datePlaced        date when the order was placed
     * @param deliveryStatus    this value stores if a order was delivered or not
     * @param paymentStatus     this value stores the current status of an order, such as payed, processed, etc
     * @param copies            this value stores the number of copies that were required in an order
     * @return                  true is created, otherwise false
     * @throws SQLException
     */

    public boolean inputOrderISBN(int oID, long ISBN, String dueBy, Float shippingCost,
                                String datePlaced, String deliveryStatus,
                                String paymentStatus, int copies) throws SQLException {

        String sql = String.format(
            "INSERT INTO `Order` VALUES(%d, '%s', %f, '%s', '%s', '%s', %d)",
            oID, dueBy, shippingCost, datePlaced, deliveryStatus, paymentStatus, copies
        );

        if (!DBManager.executeUpdate(sql)) {
            System.out.println("Couldn't insert order");
            return false;
        }

        String sql2 = String.format(
            "INSERT INTO ContainsISBN VALUES(%d, %d)",
            oID, ISBN
        );

        if (!DBManager.executeUpdate(sql2)) {
            System.out.println("Couldn't insert ISBN relation");
            return false;
        }

        return true;
    }

    /**
     * Input a new order related to an specifid Issue of a magazine or periodic publication
     * 
     * @param oID               unique identifier of an order
     * @param pubID             unique identifier of a publication
     * @param issueTitle        title of an specific issue
     * @param dueBy             date when the order is supposed to be delivered
     * @param shippingCost      cost of shipping
     * @param datePlaced        date when the order was placed
     * @param deliveryStatus    delivery status of the current order, such as processed, delivered, etc
     * @param paymentStatus     payment status of the current oder, such as payed, billed, etc
     * @param copies            number of copies of that certain issue that were requiered
     * @return                  true if the new order is created, false otherwise
     * @throws SQLException
     */

    public boolean inputOrderIssue(int oID, int pubID, String issueTitle,
                                String dueBy, Float shippingCost,
                                String datePlaced, String deliveryStatus,
                                String paymentStatus, int copies) throws SQLException {

        String sql = String.format(
            "INSERT INTO `Order` VALUES(%d, '%s', %f, '%s', '%s', '%s', %d)",
            oID, dueBy, shippingCost, datePlaced, deliveryStatus, paymentStatus, copies
        );

        if (!DBManager.executeUpdate(sql)) {
            System.out.println("Couldn't insert order");
            return false;
        }

        String sql2 = String.format(
            "INSERT INTO ContainsIssue VALUES(%d, %d, '%s')",
            oID, pubID, issueTitle
        );

        if (!DBManager.executeUpdate(sql2)) {
            System.out.println("Couldn't insert issue relation");
            return false;
        }

        return true;
    }
    /**
     * Mark an order as billed
     * 
     * @param oID           unique identifier of an order
     * @param distribID     unique idenfier of a distributor
     * @param paymentStatus payment status: which will be updated to billed
     * @return              true if the paymentStatus is changed, otherwise, false.
     * @throws SQLException
     */
    //Bill distributor for an order. 
    public boolean billDistributor(int oID, int distribID, String paymentStatus) throws SQLException {

        String sql = String.format(
            "INSERT INTO PlacedBy VALUES(%d, %d)",
            distribID, oID
        );
        if (!DBManager.executeUpdate(sql)) {
            System.out.println("Couldn't link distributor and order");
            return false;
        }
        String sql2 = String.format(
            "UPDATE `Order` SET paymentStatus = '%s' WHERE oID = %d",
            paymentStatus, oID
        );
        if (!DBManager.executeUpdate(sql2)) {
            System.out.println("Couldn't update payment status");
            return false;
        }
        return true;
    }

    //Receive a payment and change the outstanding balance of a distributor. }
    /**
     * Receive a payment and change the outstanding balance of a distributor.
     * @param distribID     unique identifier of a distributor
     * @param newBalance    new balance of a distributor
     * @return
     * @throws SQLException
     */
    public boolean receivePayment(int distribID, float newBalance) throws SQLException {

        String sql = String.format(
            "UPDATE Distributor SET balance = %f WHERE distribID = %d",
            newBalance, distribID
        );
        if (!DBManager.executeUpdate(sql)) {
            System.out.println("Couldn't update balance");
            return false;
        }
        return true;
    }

    /**
     * Identify distributors whose total billed amount does not match the sum of their recorded payments. 
     * @throws SQLException
     */
    public void identifyMismatchedDistributors() throws SQLException {

        String sql =
            "SELECT CombinedResults.distribID, CombinedResults.name, CombinedResults.balance, " +
                    "SUM(SubBalance) as CalculatedBalance " +
                    "FROM ( " +
                    "SELECT Distributor.distribID, Distributor.name, Distributor.balance, " +
                    "SUM(`Order`.shippingCost + (`Order`.copies * Issue.price)) AS SubBalance " +
                    "FROM Distributor NATURAL JOIN PlacedBy NATURAL JOIN `Order` " +
                    "NATURAL JOIN ContainsIssue NATURAL JOIN Issue " +
                    "WHERE `Order`.paymentStatus = 'Not Paid' " +
                    "GROUP BY Distributor.distribID " +
                    "UNION ALL " +
                    "SELECT Distributor.distribID, Distributor.name, Distributor.balance, " +
                    "SUM(`Order`.shippingCost + (`Order`.copies * Edition.price)) AS SubBalance " +
                    "FROM Distributor NATURAL JOIN PlacedBy NATURAL JOIN `Order` " +
                    "NATURAL JOIN ContainsISBN NATURAL JOIN Edition " +
                    "WHERE `Order`.paymentStatus = 'Not Paid' " +
                    "GROUP BY Distributor.distribID " +
                    ") AS CombinedResults " +
                    "GROUP BY CombinedResults.distribID " +
                    "HAVING ABS(balance - CalculatedBalance) >= 0.01";

        ResultSet rs = DBManager.executeQuery(sql);

                while (rs.next()) {
                    System.out.println("distribID: " + rs.getInt("distribID") + 
                        " | Name: " + rs.getString("name") +
                        " | Balance: " + rs.getFloat("balance") +
                        " | Calculated Balance: " + rs.getFloat("CalculatedBalance"));
                    
                }

                rs.close();
                //pstmt.close();
            }

    /**
     * List all distributors of a specific type located in a given city. 
     * @param category      the category of distributors you want to list
     * @param city          the city in which you want to search
     * @throws SQLException
     */
    public void listDistributors(String category, String city) throws SQLException {

        String sql = String.format(
            "SELECT * FROM Distributor WHERE category = '%s' AND city = '%s'",
            category, city
        );
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