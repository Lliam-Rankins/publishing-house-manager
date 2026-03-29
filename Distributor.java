import java.sql.*;
public class Distributor{
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
        pstmt.executeUpdate();
        pstmt.close();
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
    //Receive a payment and change the outstanding balance of a distributor. 
    //Identify distributors whose total billed amount does not match the sum of their recorded payments. 
    //List all distributors of a specific type located in a given city. 


}