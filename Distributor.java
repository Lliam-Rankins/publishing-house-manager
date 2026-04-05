
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
    public void addDistributor(String distribID, float balance, String contactName, String phoneNumber, String category, String name, String street, String city, String state) throws SQLException {
        String sql = "INSERT INTO Distributor (distribID, balance, contactName, phoneNumber, category, name, street, city, state) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, distribID);
        pstmt.setFloat(2, balance);
        pstmt.setString(3, contactName);
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            pstmt.setNull(4, java.sql.Types.VARCHAR);
        } else {
            pstmt.setString(4, phoneNumber);
        }
        if (category == null || category.isEmpty()) {
            pstmt.setNull(5, java.sql.Types.VARCHAR);
        } else {
            pstmt.setString(5, category);
        }
        if (name == null || name.isEmpty()) {
            pstmt.setNull(6, java.sql.Types.VARCHAR);
        } else {
            pstmt.setString(6, name);
        }
        pstmt.setString(7, street);
        pstmt.setString(8, city);
        pstmt.setString(9, state);
        pstmt.executeUpdate();
        pstmt.close();
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
    public void inputOrderISBN(int oID, long ISBN, String dueBy, Float shippingCost, String datePlaced, String deliveryStatus, String paymentStatus, int copies) throws SQLException {
        String sql = "INSERT INTO `Order` (oID, dueBy, shippingCost, datePlaced, deliveryStatus, paymentStatus, copies) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, oID);
        pstmt.setString(2, dueBy);
        if (shippingCost == null) {
            pstmt.setNull(3, java.sql.Types.FLOAT);
        } else {
            pstmt.setFloat(3, shippingCost);
        }
        pstmt.setString(4, datePlaced);
        pstmt.setString(5, deliveryStatus);
        pstmt.setString(6, paymentStatus);
        pstmt.setInt(7, copies);
        pstmt.executeUpdate();
        pstmt.close();

        String sql2 = "INSERT INTO ContainsISBN (oID, ISBN) VALUES (?, ?)";
        PreparedStatement pstmt2 = connection.prepareStatement(sql2);
        pstmt2.setInt(1, oID);
        pstmt2.setLong(2, ISBN);
        pstmt2.executeUpdate();
        pstmt2.close();
    }

    public void inputOrderIssue(int oID, int pubID, String issueTitle, String dueBy, Float shippingCost, String datePlaced, String deliveryStatus, String paymentStatus, int copies) throws SQLException {
        String sql = "INSERT INTO `Order` (oID, dueBy, shippingCost, datePlaced, deliveryStatus, paymentStatus, copies) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, oID);
        pstmt.setString(2, dueBy);
        if (shippingCost == null) {
            pstmt.setNull(3, java.sql.Types.FLOAT);
        } else {
            pstmt.setFloat(3, shippingCost);
        }
        pstmt.setString(4, datePlaced);
        pstmt.setString(5, deliveryStatus);
        pstmt.setString(6, paymentStatus);
        pstmt.setInt(7, copies);
        pstmt.executeUpdate();
        pstmt.close();

        String sql2 = "INSERT INTO ContainsIssue (oID, pubID, issueTitle) VALUES (?, ?, ?)";
        PreparedStatement pstmt2 = connection.prepareStatement(sql2);
        pstmt2.setInt(1, oID);
        pstmt2.setInt(2, pubID);
        pstmt2.setString(3, issueTitle);
        pstmt2.executeUpdate();
        pstmt2.close();
    }
    //Bill distributor for an order. 
    public void billDistributor(int oID, int distribID, String paymentStatus) throws SQLException {
        String sql = "INSERT INTO PlacedBy (distribID, oID) VALUES (?, ?);";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, distribID);
        pstmt.setInt(2, oID);
        pstmt.executeUpdate();
        pstmt.close();

        String sql2 = "UPDATE `Order` SET paymentStatus = ? WHERE oID = ?";
        PreparedStatement pstmt2 = connection.prepareStatement(sql2);
        pstmt2.setString(1, paymentStatus);
        pstmt2.setInt(2, oID);
        pstmt2.executeUpdate();
        pstmt2.close();
    }

    //Receive a payment and change the outstanding balance of a distributor. }
    public void receivePayment(int distribID, float newBalance) throws SQLException {
        String sql = "UPDATE Distributor SET balance = ? WHERE distribID = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setFloat(1, newBalance);
        pstmt.setInt(2, distribID);
        pstmt.executeUpdate();
        pstmt.close();
    }
    //Identify distributors whose total billed amount does not match the sum of their recorded payments. 
    public void identifyMismatchedDistributors() throws SQLException {
                String sql = "SELECT CombinedResults.distribID, " +
                    "CombinedResults.name, " +
                    "CombinedResults.balance, " +
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
                PreparedStatement pstmt = connection.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    System.out.println("distribID: " + rs.getInt("distribID") + 
                        " | Name: " + rs.getString("name") +
                        " | Balance: " + rs.getFloat("balance") +
                        " | Calculated Balance: " + rs.getFloat("CalculatedBalance"));
                    
                }
                rs.close();
                pstmt.close();
            }

    //List all distributors of a specific type located in a given city. 
    public void listDistributors(String category, String city) throws SQLException {
        String sql = "SELECT * FROM Distributor WHERE category = ? AND city = ?;";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, category);
        pstmt.setString(2, city);
        ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("distribID: " + rs.getInt("distribID") + 
                        " | Name: " + rs.getString("name") +
                        " | Category: " + rs.getString("category") +
                        " | City: " + rs.getString("city"));
            }
            rs.close();
    }


}