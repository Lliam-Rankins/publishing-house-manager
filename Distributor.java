import java.sql.*;
public class Distributor{
    private Connection connection;

    public Distributor(Connection connection){
        this.connection = connection;
        
    }
    // add a new distributor
    public void addDistributor(String distribID, float balance, String contactName, String phoneNumber, String category, String name, String street, String city, String state) throws SQLException {
        String sql = "INSERT INTO distributors (distribID,  balance, contactName, phoneNumber, category, name, street, city, state) " +
                     "VALUES ('"+distribID+"','"+balance+"','"+contactName+",'"+phoneNumber+"','"+category+"','"+name+"','"+street+"','"+city+"','"+state+"')";
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }

    // Update distributor information

    public void updateDistributor(String contactName, int distribID, String type) throws SQLException {
        String sql = "UPDATE Distributor SET contactName = '"+contactName+"' WHERE distribID = '"+distribID+" '; ";
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }

    // delete a distributor.
    public void deleteDistributor(int distribID)throws SQLException{
        String sql = "DELETE FROM Distributor WHERE distribID = '"+distribID+"';";
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }


    //Input orders from distributors, for a book edition or an issue of a publication per distributor, for a certain date. 

    //Bill distributor for an order. 
    //Receive a payment and change the outstanding balance of a distributor. 
    //Identify distributors whose total billed amount does not match the sum of their recorded payments. 
    //List all distributors of a specific type located in a given city. 


}