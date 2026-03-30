import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * 
 * Acknowledgments: This example is a modification of code provided by Dimitri Rakitine. Further
 * modified by Shrikanth N C for MySql(MariaDB) support. Replace all $USER$ with your unity id and
 * $PASSWORD$ with your 9 digit student id or updated password (if changed)
 * 
 */



public class Operations {
    static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/pcontre";
    // Put your oracle ID and password here

    private static Connection connection = null;
    private static Statement statement = null;
    private static ResultSet result = null;
    // Adding distributor
    private static Distributor distributor = null;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter an operation: ");
        String operation = s.nextLine();

        initialize();

        switch (operation) {
            case "enterPublication":
                enterPublication();
                break;
            
            case "addDistributor":{
                DistributorUI.handleAddDistributor(s,distributor);
                break;
            }

            case "updateDistributor":{
                DistributorUI.handleUpdateDistributor(s, distributor);
                break;
            }
            case "deleteDistributor":{
                DistributorUI.handleDeleteDistributor(s, distributor);
                break;
            }
            // Input order
            case "inputOrder":{
                DistributorUI.handleInputOrder(s, distributor);
                break;
            }
            //Bill distributor for an order. 
            case "billDistributor":{
                DistributorUI.handleBillDistributor(s, distributor);
                break;
            }
            //Receive a payment and change the outstanding balance of a distributor. 
            case "receivePayment":{
                //thinking about this, maybe we need to first change the status of an order to payed or something
                DistributorUI.handleReceivePayment(s, distributor);
                break;
            }
            //Identify distributors whose total billed amount does not match the sum of their recorded payments. 
            case "identifyMismatchedDistributors":{
                DistributorUI.handleIdentifyMismatchedDistributors(s, distributor);
                break;
            }
            //List all distributors of a specific type located in a given city. 
            case "listDistributors":{
                DistributorUI.handleListDistributors(s, distributor);
                break;
            }
            default:
                break;
        }


        s.close();
        close();
    }

    private static void enterPublication() {
        // execute SQL statements for entering a publication
    }

    private static void initialize() {
        try {
            connectToDatabase();
            //Distribution
            distributor = new Distributor(connection);

            // Create all tables here
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void connectToDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("org.mariadb.jdbc.Driver");

        String user = "pcontre";
        String password = "200669337";

        connection = DriverManager.getConnection(jdbcURL, user, password);
        statement = connection.createStatement();

        //try {
            // Drop tables so we have a fresh slate
        //} catch (SQLException e) {
        //}
    }

    private static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (result != null) {
            try {
                result.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
