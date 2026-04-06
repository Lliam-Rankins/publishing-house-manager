
import java.sql.*;


public class Distributor{
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String RESET = "\033[0m";
    private Connection connection;

    public Distributor(Connection connection){
        this.connection = connection;
        
    }
    // add a new distributor
    public boolean addDistributor(String distribID, float balance, String contactName, String phoneNumber, String category, String name, String street, String city, String state) throws SQLException {
        String sql = "INSERT INTO Distributor VALUES(%s, %f, %s, %s, %s, %s, %s, %s) ";
        if (!DBManager.executeUpdate(sql)) {
            System.out.println("Couldn't add this distributor");
            return false;
        }

        return true;
    }

    // Update distributor information
    public void updateDistributor(String field, String value, int distribID) throws SQLException {
        String sql = "UPDATE Distributor SET " + field + " = ? WHERE distribID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, value);
        pstmt.setInt(2, distribID);
        pstmt.executeUpdate();
        pstmt.close();
    }

    // delete a distributor.

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


    //Input orders from distributors, for a book edition or an issue of a publication per distributor, for a certain date. 
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
    //Identify distributors whose total billed amount does not match the sum of their recorded payments. 
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
    }

    //List all distributors of a specific type located in a given city. 
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