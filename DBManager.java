import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {
    private static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/enkatz2";

    public static Connection connection = null;
    public static Statement statement = null;
    public static ResultSet result = null;

    public static void initialize() {
        try {
            connectToDatabase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dropTables();
        createTables();
    }

    private static void connectToDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");

        String user = "enkatz2";
        String password = "200611917";

        connection = DriverManager.getConnection(jdbcURL, user, password);
        statement = connection.createStatement();
    }

    public static void execute() {

    }

    public static void executeUpdate() {

    }

    private static void createTables() {
        try {
            // Drop tables so we have a fresh slate
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            statement.executeUpdate("DROP TABLE Distributor");
            statement.executeUpdate("DROP TABLE `Order`");
            statement.executeUpdate("DROP TABLE Publication");
            statement.executeUpdate("DROP TABLE Issue");
            statement.executeUpdate("DROP TABLE Edition");
            statement.executeUpdate("DROP TABLE ISBNPublication");
            statement.executeUpdate("DROP TABLE ContainsISBN");
            statement.executeUpdate("DROP TABLE PlacedBy");
            statement.executeUpdate("DROP TABLE Person");
            statement.executeUpdate("DROP TABLE Payment");
            statement.executeUpdate("DROP TABLE Receives");
            statement.executeUpdate("DROP TABLE WritesArticle");
            statement.executeUpdate("DROP TABLE WritesChapter");
            statement.executeUpdate("DROP TABLE Edits");
            statement.executeUpdate("DROP TABLE Article");
            statement.executeUpdate("DROP TABLE Chapter");
            statement.executeUpdate("DROP TABLE ContainsIssue");
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");
        } catch (SQLException e) {
        }
    }

    private static void dropTables() {

    }
}
