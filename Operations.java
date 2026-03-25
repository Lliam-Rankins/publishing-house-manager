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
    static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/enkatz2";
    // Put your oracle ID and password here

    private static Connection connection = null;
    private static Statement statement = null;
    private static ResultSet result = null;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter an operation: ");
        String operation = s.nextLine();

        initialize();

        switch (operation) {
            case "enterPublication":
                enterPublication();
                break;

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

            // Create all tables here
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void connectToDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("org.mariadb.jdbc.Driver");

        String user = "enkatz2";
        String password = "200611917";

        connection = DriverManager.getConnection(jdbcURL, user, password);
        statement = connection.createStatement();

        try {
            // Drop tables so we have a fresh slate
        } catch (SQLException e) {
        }
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
